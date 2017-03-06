package org.solmix.runtime.proxy;


public interface ProxyManager {

	void addHandler(ProxyHandler handler);
	
	void removeHandler(ProxyHandler handler);
	
	
	Object proxy(Object instance,Class<?>[] proxyInterfaces);
}
