package org.solmix.runtime.proxy.interceptor;

import org.solmix.runtime.proxy.support.InvokeBeforeAspect;
import org.solmix.runtime.proxy.support.MethodInvocation;
import org.springframework.util.Assert;

public class InvokeBeforeAspectInterceptor implements MethodInterceptor {

	private InvokeBeforeAspect aspect;

	public InvokeBeforeAspectInterceptor(InvokeBeforeAspect aspect) {
		Assert.notNull(aspect, "Aspect must not be null");
		this.aspect = aspect;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		this.aspect.before(mi.getMethod(), mi.getArguments(), mi.getThis() );
		return mi.proceed();
	}

}
