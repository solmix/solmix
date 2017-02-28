package org.solmix.runtime.proxy.interceptor;

import org.slf4j.Logger;
import org.solmix.runtime.proxy.support.MethodInvocation;

public class SimpleTraceInterceptor extends AbstractTraceInterceptor {

	public SimpleTraceInterceptor() {
	}

	/**
	 * Create a new SimpleTraceInterceptor with dynamic or static logger,
	 * according to the given flag.
	 * @param useDynamicLogger whether to use a dynamic logger or a static logger
	 * @see #setUseDynamicLogger
	 */
	public SimpleTraceInterceptor(boolean useDynamicLogger) {
		setUseDynamicLogger(useDynamicLogger);
	}


	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Logger logger) throws Throwable {
		String invocationDescription = getInvocationDescription(invocation);
		logger.trace("Entering " + invocationDescription);
		try {
			Object rval = invocation.proceed();
			logger.trace("Exiting " + invocationDescription);
			return rval;
		}
		catch (Throwable ex) {
			logger.trace("Exception thrown in " + invocationDescription, ex);
			throw ex;
		}
	}

	/**
	 * Return a description for the given method invocation.
	 * @param invocation the invocation to describe
	 * @return the description
	 */
	protected String getInvocationDescription(MethodInvocation invocation) {
		return "method '" + invocation.getMethod().getName() + "' of class [" +
				invocation.getThis().getClass().getName() + "]";
	}
}
