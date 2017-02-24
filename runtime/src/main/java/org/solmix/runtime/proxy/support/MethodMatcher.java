package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;


public interface MethodMatcher {
	boolean matches(Method method, Class<?> targetClass);
	boolean isRuntime();
	boolean matches(Method method, Class<?> targetClass, Object[] args);
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;
}
