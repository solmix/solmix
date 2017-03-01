package org.solmix.runtime.proxy.support;

public abstract class ProxyContext {
	private static final ThreadLocal<Object> currentProxy = new ThreadLocal<Object>();

	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
		}
		return proxy;
	}

	static Object setCurrentProxy(Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}
}
