package org.solmix.tests.proxy;

import org.solmix.runtime.proxy.support.MethodInterceptor;
import org.solmix.runtime.proxy.support.MethodInvocation;

public class NopInterceptor implements MethodInterceptor {
	private int count;
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		increment();
		return invocation.proceed();
	}

	public int getCount() {
		return this.count;
	}

	protected void increment() {
		++count;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NopInterceptor)) {
			return false;
		}
		if (this == other) {
			return true;
		}
		return this.count == ((NopInterceptor) other).count;
	}

}
