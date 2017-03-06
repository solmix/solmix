package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;


public abstract class StaticMethodMatcherCutpoint implements Cutpoint,MethodMatcher{

	private ClassFilter classFilter = ClassFilter.TRUE;


	/**
	 * Set the {@link ClassFilter} to use for this pointcut.
	 * Default is {@link ClassFilter#TRUE}.
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.classFilter = classFilter;
	}

	@Override
	public ClassFilter getClassFilter() {
		return this.classFilter;
	}


	@Override
	public final MethodMatcher getMethodMatcher() {
		return this;
	}


	@Override
	public boolean isRuntime() {
		return false;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
		throw new UnsupportedOperationException("Illegal MethodMatcher usage");
	}


}
