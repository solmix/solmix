package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;

public interface MethodInvocation {

	Object[] getArguments();
	Method getMethod();
	Object proceed() throws Throwable;
	Object getThis();
}
