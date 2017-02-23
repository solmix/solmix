package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.AspectProxyFactory;
import org.solmix.runtime.proxy.ProxyInfo;

@SuppressWarnings("serial")
public class ProxyAspectSupport extends ProxyInfo {

	private AspectProxyFactory factory;
	private boolean created = false;
	
	public ProxyAspectSupport(){
	}
}
