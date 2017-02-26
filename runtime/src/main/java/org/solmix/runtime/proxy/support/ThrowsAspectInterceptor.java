package org.solmix.runtime.proxy.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;


public class ThrowsAspectInterceptor implements MethodInterceptor,AfterAspect {

	
	private static final String AFTER_THROWING = "afterThrowing";

	private static final Logger LOG = LoggerFactory.getLogger(ThrowsAspectInterceptor.class);


	private final Object throwsAdvice;

	/** Methods on throws advice, keyed by exception class */
	private final Map<Class<?>, Method> exceptionHandlerMap = new HashMap<Class<?>, Method>();

	public ThrowsAspectInterceptor(Object throwsAdvice) {
		Assert.isNotNull(throwsAdvice, "Advice must not be null");
		this.throwsAdvice = throwsAdvice;

		Method[] methods = throwsAdvice.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(AFTER_THROWING) &&
					(method.getParameterTypes().length == 1 || method.getParameterTypes().length == 4) &&
					Throwable.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1])
				) {
				// Have an exception handler
				this.exceptionHandlerMap.put(method.getParameterTypes()[method.getParameterTypes().length - 1], method);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Found exception handler method: " + method);
				}
			}
		}

		if (this.exceptionHandlerMap.isEmpty()) {
			throw new IllegalArgumentException(
					"At least one handler method must be found in class [" + throwsAdvice.getClass() + "]");
		}
	}

	public int getHandlerMethodCount() {
		return this.exceptionHandlerMap.size();
	}


	private Method getExceptionHandler(Throwable exception) {
		Class<?> exceptionClass = exception.getClass();
		if (LOG.isTraceEnabled()) {
			LOG.trace("Trying to find handler for exception of type [" + exceptionClass.getName() + "]");
		}
		Method handler = this.exceptionHandlerMap.get(exceptionClass);
		while (handler == null && !exceptionClass.equals(Throwable.class)) {
			exceptionClass = exceptionClass.getSuperclass();
			handler = this.exceptionHandlerMap.get(exceptionClass);
		}
		if (handler != null && LOG.isDebugEnabled()) {
			LOG.debug("Found handler for exception of type [" + exceptionClass.getName() + "]: " + handler);
		}
		return handler;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}
		catch (Throwable ex) {
			Method handlerMethod = getExceptionHandler(ex);
			if (handlerMethod != null) {
				invokeHandlerMethod(mi, ex, handlerMethod);
			}
			throw ex;
		}
	}

	private void invokeHandlerMethod(MethodInvocation mi, Throwable ex, Method method) throws Throwable {
		Object[] handlerArgs;
		if (method.getParameterTypes().length == 1) {
			handlerArgs = new Object[] { ex };
		}
		else {
			handlerArgs = new Object[] {mi.getMethod(), mi.getArguments(), mi.getThis(), ex};
		}
		try {
			method.invoke(this.throwsAdvice, handlerArgs);
		}
		catch (InvocationTargetException targetEx) {
			throw targetEx.getTargetException();
		}
	}

}
