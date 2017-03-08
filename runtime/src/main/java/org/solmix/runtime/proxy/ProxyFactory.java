package org.solmix.runtime.proxy;

import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.proxy.support.ProxyAspectSupport;
import org.solmix.runtime.proxy.target.TargetSource;

@SuppressWarnings("serial")
public class ProxyFactory extends ProxyAspectSupport{
	
	public ProxyFactory() {
	}

	
	public ProxyFactory(Object target) {
		setTarget(target);
		setInterfaces(ClassUtils.getAllInterfaces(target));
	}

	public ProxyFactory(Class<?>... proxyInterfaces) {
		setInterfaces(proxyInterfaces);
	}
	
	public ProxyFactory(Class<?> proxyInterface, TargetSource targetSource) {
		addInterface(proxyInterface);
		setTargetSource(targetSource);
	}
	
	public Object getProxy() {
		return createAspectProxy().getProxy();
	}
	
	public Object getProxy(ClassLoader classLoader) {
		return createAspectProxy().getProxy(classLoader);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<T> proxyInterface, TargetSource targetSource) {
		return (T) new ProxyFactory(proxyInterface, targetSource).getProxy();
	}

	public static Object getProxy(TargetSource targetSource) {
		if (targetSource.getTargetClass() == null) {
			throw new IllegalArgumentException("Cannot create class proxy for TargetSource with null target class");
		}
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTargetSource(targetSource);
		proxyFactory.setProxyTargetClass(true);
		return proxyFactory.getProxy();
	}
}
