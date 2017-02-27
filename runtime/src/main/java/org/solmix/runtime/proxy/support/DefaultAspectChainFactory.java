package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.solmix.runtime.proxy.Aspector;
import org.solmix.runtime.proxy.Interceptor;


public class DefaultAspectChainFactory implements AspectChainFactory {

	@Override
	public List<Object> getInterceptorsAndDynamicInterception(
			ProxyAspectSupport config, Method method, Class<?> targetClass) {
		
		List<Object> interceptorList = new ArrayList<Object>(config.getAspectors().length);
		
		Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
		boolean hasIntroductions = hasMatchingIntroductions(config, actualClass);
		AspectorAdapterRegistry registry = AspectorAdapterRegistry.getInstance();

		for (Aspector aspector : config.getAspectors()) {
			
			if (aspector instanceof CutpointAspector) {
				// Add it conditionally.
				CutpointAspector pointcutAdvisor = (CutpointAspector) aspector;
				if (config.isPreFiltered() || pointcutAdvisor.getCutpoint().getClassFilter().matches(actualClass)) {
					MethodInterceptor[] interceptors = registry.getInterceptors(aspector);
					MethodMatcher mm = pointcutAdvisor.getCutpoint().getMethodMatcher();
					if (MethodMatcherHelper.matches(mm, method, actualClass, hasIntroductions)) {
						if (mm.isRuntime()) {
							// Creating a new object instance in the getInterceptors() method
							// isn't a problem as we normally cache created chains.
							for (MethodInterceptor interceptor : interceptors) {
								interceptorList.add(new MethodInterceptorAndMatcher(interceptor, mm));
							}
						}
						else {
							interceptorList.addAll(Arrays.asList(interceptors));
						}
					}
				}
			} else if (aspector instanceof IntroductionAspector) {
				IntroductionAspector ia = (IntroductionAspector) aspector;
				if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
					Interceptor[] interceptors = registry.getInterceptors(aspector);
					interceptorList.addAll(Arrays.asList(interceptors));
				}
			} else {
				Interceptor[] interceptors = registry.getInterceptors(aspector);
				interceptorList.addAll(Arrays.asList(interceptors));
			}
		}

		return interceptorList;
	}

	/**
	 * Determine whether the Advisors contain matching introductions.
	 */
	private static boolean hasMatchingIntroductions(ProxyAspectSupport config, Class<?> actualClass) {
		for (int i = 0; i < config.getAspectors().length; i++) {
			Aspector advisor = config.getAspectors()[i];
			if (advisor instanceof IntroductionAspector) {
				IntroductionAspector ia = (IntroductionAspector) advisor;
				if (ia.getClassFilter().matches(actualClass)) {
					return true;
				}
			}
		}
		return false;
	}

}
