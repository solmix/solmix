package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;

import org.solmix.runtime.proxy.Aspect;

public interface InvokeAfterAspect extends Aspect {

	
	void after(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
