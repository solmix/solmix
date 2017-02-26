package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;

public interface InvokeBeforeAspect extends BeforeAspect {

	void before(Method method, Object[] args, Object target) throws Throwable;
}
