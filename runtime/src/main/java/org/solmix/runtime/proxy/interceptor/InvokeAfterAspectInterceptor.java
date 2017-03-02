package org.solmix.runtime.proxy.interceptor;

import org.solmix.runtime.proxy.support.InvokeAfterAspect;
import org.solmix.runtime.proxy.support.MethodInvocation;
import org.springframework.util.Assert;

public class InvokeAfterAspectInterceptor implements MethodInterceptor {

	
	private InvokeAfterAspect aspect;

	public InvokeAfterAspectInterceptor(InvokeAfterAspect aspect) {
		Assert.notNull(aspect, "Aspect must not be null");
		this.aspect = aspect;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object result = mi.proceed();
		this.aspect.after(result,mi.getMethod(), mi.getArguments(), mi.getThis() );
		return result;
	}


}
