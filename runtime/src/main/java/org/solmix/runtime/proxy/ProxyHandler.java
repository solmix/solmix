package org.solmix.runtime.proxy;



public interface ProxyHandler {

	
	boolean requireProxy(Class<?> target);
	
	Object proxy(Object instance,Class<?>[] proxyInterfaces);
}
