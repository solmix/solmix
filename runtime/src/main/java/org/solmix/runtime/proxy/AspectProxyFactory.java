package org.solmix.runtime.proxy;

import org.solmix.runtime.proxy.support.ProxyAspectSupport;


public interface AspectProxyFactory {
	AspectProxy createAspectProxy(ProxyAspectSupport config) throws ProxyConfigException;
}
