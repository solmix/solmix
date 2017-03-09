package org.solmix.runtime.proxy;


public interface ProxyManager {

	void addRule(ProxyRule rule);
	
	void removeRule(ProxyRule rule);
	
	
	Object proxy(String name,Object instance);
}
