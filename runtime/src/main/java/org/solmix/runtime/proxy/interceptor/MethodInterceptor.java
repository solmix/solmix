package org.solmix.runtime.proxy.interceptor;

import org.solmix.runtime.proxy.Interceptor;
import org.solmix.runtime.proxy.support.MethodInvocation;

public interface MethodInterceptor extends Interceptor{

	
	Object invoke(MethodInvocation invocation) throws Throwable;
}
