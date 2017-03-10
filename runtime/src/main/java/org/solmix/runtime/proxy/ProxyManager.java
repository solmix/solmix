package org.solmix.runtime.proxy;


public interface ProxyManager {

	void addRule(ProxyRule rule);
	
	void removeRule(ProxyRule rule);
	
	
	Object proxy(ClassLoader loader,String name,Object instance);
	
	Object proxy(ClassLoader loader,String name,Object instance,Class<?>[] interfaces);
}
