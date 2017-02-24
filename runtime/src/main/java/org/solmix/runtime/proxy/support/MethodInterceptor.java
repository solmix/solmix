package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Interceptor;

public interface MethodInterceptor extends Interceptor{

	
	Object invoke(MethodInvocation invocation) throws Throwable;
}
