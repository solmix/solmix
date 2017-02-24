package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;


public class TrueMethodMatcher implements MethodMatcher {

	public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

	/**
	 * Enforce Singleton pattern.
	 */
	private TrueMethodMatcher() {
	}

	@Override
	public boolean isRuntime() {
		return false;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return true;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
		// Should never be invoked as isRuntime returns false.
		throw new UnsupportedOperationException();
	}

	/**
	 * Required to support serialization. Replaces with canonical
	 * instance on deserialization, protecting Singleton pattern.
	 * Alternative to overriding {@code equals()}.
	 */
	private Object readResolve() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "MethodMatcher.TRUE";
	}


}
