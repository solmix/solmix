package org.solmix.runtime.proxy;



public interface ProxyHandler {

	
	
	Object proxy(Object instance,Class<?>[] interfaces);
}
