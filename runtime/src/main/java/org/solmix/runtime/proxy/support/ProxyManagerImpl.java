package org.solmix.runtime.proxy.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.proxy.ProxyManager;
import org.solmix.runtime.proxy.ProxyRule;

public class ProxyManagerImpl implements ProxyManager {

    protected final List<ProxyRule> rules = new CopyOnWriteArrayList<ProxyRule>();

    private final Container container;
    public ProxyManagerImpl(Container container){
    	this.container=container;
    }
	@Override
	public void addRule(ProxyRule handler) {
		 if (!rules.contains(handler)) {
			 if(handler.getProxyHandler() instanceof ContainerAware){
				 ((ContainerAware)handler.getProxyHandler()).setContainer(container);
			 }
			 rules.add(0, handler);
	        }
	}

	@Override
	public void removeRule(ProxyRule handler) {
		 if (rules.contains(handler)) {
			 rules.remove(handler);
	        }
	}

	

	@Override
	public Object proxy(String name,Object instance) {
		Assert.isNotNull(instance,"proxy target must be not null");
		Object proxy=instance;
		for(ProxyRule handler:rules){
			if(handler.match(name)){
				proxy=handler.getProxyHandler().proxy(proxy, null);
			}
			
		}
		return proxy;
	}


}
