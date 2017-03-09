package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.AspectProxy;
import org.solmix.runtime.proxy.AspectProxyFactory;
import org.solmix.runtime.proxy.ProxyConfigException;
import org.solmix.runtime.proxy.RuntimeProxy;

public class DefaultAspectProxyFactory implements AspectProxyFactory {

	@Override
	public AspectProxy createAspectProxy(ProxyAspectSupport config)
			throws ProxyConfigException {
		if (config.isOptimize() || config.isProxyTargetClass()
				|| hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new ProxyConfigException(
						"TargetSource cannot determine target class: "
								+ "Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface()) {
				return new JdkAspectProxy(config);
			}
			return  supportCglib(config);
		} else {
			return new JdkAspectProxy(config);
		}
	}
	
	AspectProxy supportCglib(ProxyAspectSupport config)throws ProxyConfigException{
		 try {
			 	Class.forName("net.sf.cglib.proxy.Enhancer");
			    Class.forName("net.sf.cglib.proxy.MethodInterceptor");
			    Class.forName("net.sf.cglib.proxy.MethodProxy");
		} catch (ClassNotFoundException e) {
			throw new ProxyConfigException("runtime proxy need cglib proxy,check the lib is loaded?");
		}
		 return new CglibAspectProxy(config);
	}

	private boolean hasNoUserSuppliedProxyInterfaces(ProxyAspectSupport config) {
		Class<?>[] interfaces = config.getProxiedInterfaces();
		return (interfaces.length == 0 || (interfaces.length == 1 && RuntimeProxy.class.equals(interfaces[0])));
	}

}
