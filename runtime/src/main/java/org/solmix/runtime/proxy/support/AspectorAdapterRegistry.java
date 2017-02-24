package org.solmix.runtime.proxy.support;

import java.util.ArrayList;
import java.util.List;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AspectorAdapterRegistry {

	private final List<AspectorAdapter> adapters = new ArrayList<AspectorAdapter>(3);

	private static AspectorAdapterRegistry  instance= new AspectorAdapterRegistry();
	
	public static AspectorAdapterRegistry getInstance() {
		return instance;
	}
	/**
	 * Create a new DefaultAdvisorAdapterRegistry, registering well-known adapters.
	 */
	public AspectorAdapterRegistry() {
		registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
		registerAdvisorAdapter(new AfterReturningAdviceAdapter());
		registerAdvisorAdapter(new ThrowsAdviceAdapter());
	}


	public Aspector wrap(Object adviceObject) throws UnknownAspectTypeException {
		if (adviceObject instanceof Aspector) {
			return (Aspector) adviceObject;
		}
		if (!(adviceObject instanceof Aspect)) {
			throw new UnknownAspectTypeException(adviceObject);
		}
		Aspect advice = (Aspect) adviceObject;
		if (advice instanceof MethodInterceptor) {
			// So well-known it doesn't even need an adapter.
			return new DefaultPointcutAdvisor(advice);
		}
		for (AspectorAdapter adapter : this.adapters) {
			// Check that it is supported.
			if (adapter.supportsAspect(advice)) {
				return new DefaultPointcutAdvisor(advice);
			}
		}
		throw new UnknownAspectTypeException(advice);
	}

	public MethodInterceptor[] getInterceptors(Aspector advisor) throws IllegalArgumentException {
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>(3);
		Aspect advice = advisor.getAspect();
		if (advice instanceof MethodInterceptor) {
			interceptors.add((MethodInterceptor) advice);
		}
		for (AspectorAdapter adapter : this.adapters) {
			if (adapter.supportsAspect(advice)) {
				interceptors.add(adapter.getInterceptor(advisor));
			}
		}
		if (interceptors.isEmpty()) {
			throw new UnknownAspectTypeException(advisor.getAspect());
		}
		return interceptors.toArray(new MethodInterceptor[interceptors.size()]);
	}

	public void registerAspectorAdapter(AspectorAdapter adapter) {
		this.adapters.add(adapter);
	}
}
