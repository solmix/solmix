package org.solmix.runtime.proxy.support;

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
