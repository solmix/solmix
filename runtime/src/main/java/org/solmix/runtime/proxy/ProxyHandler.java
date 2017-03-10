package org.solmix.runtime.proxy;



public interface ProxyHandler {

	
	
	Object proxy(ClassLoader classLoader,Object instance,Class<?>[] interfaces);
}
