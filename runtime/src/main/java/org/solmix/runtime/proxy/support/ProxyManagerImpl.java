package org.solmix.runtime.proxy.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.proxy.ProxyHandler;
import org.solmix.runtime.proxy.ProxyInfo;
import org.solmix.runtime.proxy.ProxyManager;

public class ProxyManagerImpl implements ProxyManager {

    protected final List<ProxyHandler> handlers = new CopyOnWriteArrayList<ProxyHandler>();

	@Override
	public void addHandler(ProxyHandler handler) {
		 if (!handlers.contains(handler)) {
			 handlers.add(0, handler);
	        }
	}

	@Override
	public void removeHandler(ProxyHandler handler) {
		 if (handlers.contains(handler)) {
			 handlers.remove(handler);
	        }
	}

	@Override
	public Object proxy(Object instance, ProxyInfo config) {
		
		return null;
	}

	@Override
	public Object proxy(Object instance, Class<?>[] proxyInterfaces) {
		Assert.isNotNull(instance,"proxy target must be not null");
		if(proxyInterfaces==null||proxyInterfaces.length==0){
			proxyInterfaces= ClassUtils.getAllInterfacesForClass(instance.getClass());
		}
		for(ProxyHandler handler:handlers){
			if(handler.requireProxy(ProxyUtils.getTargetClass(instance))){
				return handler.proxy(instance, proxyInterfaces);
			}
		}
		return null;
	}


}
