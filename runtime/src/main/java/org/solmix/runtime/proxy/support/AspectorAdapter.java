package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;
import org.solmix.runtime.proxy.interceptor.MethodInterceptor;

public interface AspectorAdapter {

	MethodInterceptor getInterceptor(Aspector advisor);

	boolean supportsAspect(Aspect advice);

}
