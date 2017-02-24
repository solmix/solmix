package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;

public interface AspectorAdapter {

	MethodInterceptor getInterceptor(Aspector advisor);

	boolean supportsAspect(Aspect advice);

}
