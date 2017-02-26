package org.solmix.runtime.proxy.support;


public class MethodInterceptorAndMatcher {

	final MethodInterceptor interceptor;

	final MethodMatcher methodMatcher;

	public MethodInterceptorAndMatcher(MethodInterceptor interceptor, MethodMatcher methodMatcher) {
		this.interceptor = interceptor;
		this.methodMatcher = methodMatcher;
	}
}
