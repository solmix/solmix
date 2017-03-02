package org.solmix.runtime.proxy.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.runtime.proxy.support.MethodInvocation;
import org.solmix.runtime.proxy.support.ProxyUtils;


public class ReflectionMethodInvocation implements ProxyMethodInvacation {

	protected final Object proxy;

	protected final Object target;

	protected final Method method;

	protected Object[] arguments;

	private final Class<?> targetClass;

	/**
	 * Lazily initialized map of user-specific attributes for this invocation.
	 */
	private Map<String, Object> userAttributes;

	/**
	 * List of MethodInterceptor and InterceptorAndDynamicMethodMatcher
	 * that need dynamic checks.
	 */
	protected final List<?> interceptorsAndDynamicMethodMatchers;

	/**
	 * Index from 0 of the current interceptor we're invoking.
	 * -1 until we invoke: then the current interceptor.
	 */
	private int currentInterceptorIndex = -1;
	
	public ReflectionMethodInvocation(Object proxy, Object target,
			Method method, Object[] args, Class<?> targetClass,
			List<Object> interceptorsAndDynamicMethodMatchers) {

		this.proxy = proxy;
		this.target = target;
		this.targetClass = targetClass;
		this.method = ProxyUtils.findBridgedMethod(method);
		this.arguments = args;
		this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
	}

	@Override
	public Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}

	@Override
	public Object proceed() throws Throwable {
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return invokeJoinpoint();
		}

		Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof MethodInterceptorAndMatcher) {
			// Evaluate dynamic method matcher here: static part will already have
			// been evaluated and found to match.
			MethodInterceptorAndMatcher dm =(MethodInterceptorAndMatcher) interceptorOrInterceptionAdvice;
			if (dm.methodMatcher.matches(this.method, this.targetClass, this.arguments)) {
				return dm.interceptor.invoke(this);
			}
			else {
				// Dynamic matching failed.
				// Skip this interceptor and invoke the next in the chain.
				return proceed();
			}
		}
		else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}
	
	protected Object invokeJoinpoint() throws Throwable {
		return ProxyUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
	}

	@Override
	public Object getThis() {
		return  this.target;
	}


	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public Object getProxy() {
		return this.proxy;
	}

	@Override
	public org.solmix.runtime.proxy.support.MethodInvocation invocableClone() {
		Object[] cloneArguments = null;
		if (this.arguments != null) {
			cloneArguments = new Object[this.arguments.length];
			System.arraycopy(this.arguments, 0, cloneArguments, 0, this.arguments.length);
		}
		return invocableClone(cloneArguments);
	}

	@Override
	public org.solmix.runtime.proxy.support.MethodInvocation invocableClone(
			Object[] arguments) {
		if (this.userAttributes == null) {
			this.userAttributes = new HashMap<String, Object>();
		}

		// Create the MethodInvocation clone.
		try {
			ReflectionMethodInvocation clone = (ReflectionMethodInvocation) clone();
			clone.arguments = arguments;
			return clone;
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(
					"Should be able to clone object of type [" + getClass() + "]: " + ex);
		}
	}

	@Override
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	@Override
	public void setUserAttribute(String key, Object value) {
		if (value != null) {
			if (this.userAttributes == null) {
				this.userAttributes = new HashMap<String, Object>();
			}
			this.userAttributes.put(key, value);
		}
		else {
			if (this.userAttributes != null) {
				this.userAttributes.remove(key);
			}
		}
	}

	@Override
	public Object getUserAttribute(String key) {
		return (this.userAttributes != null ? this.userAttributes.get(key) : null);
	}

}
