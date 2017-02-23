package org.solmix.runtime.proxy;

public interface AspectProxy {
	Object getProxy();

	Object getProxy(ClassLoader classLoader);
}
