package org.solmix.runtime.proxy;

import org.solmix.runtime.proxy.target.TargetSource;

public interface Aspected {

	boolean isFrozen();
	boolean isProxyTargetClass();
	Class<?>[] getProxiedInterfaces();
	boolean isInterfaceProxied(Class<?> intf);
	void setTargetSource(TargetSource targetSource);
	TargetSource getTargetSource();
	void setExposeProxy(boolean exposeProxy);
	boolean isExposeProxy();
	void setPreFiltered(boolean preFiltered);
	boolean isPreFiltered();
	Aspector[] getAspectors();
	void addAspector(Aspector Aspector) throws ProxyConfigException;
	void addAspector(int pos, Aspector Aspector) throws ProxyConfigException;
	boolean removeAspector(Aspector Aspector);
	void removeAspector(int index) throws ProxyConfigException;
	int indexOf(Aspector Aspector);
	boolean replaceAspector(Aspector a, Aspector b) throws ProxyConfigException;

	void addAspect(Aspect aspect) throws ProxyConfigException;

	void addAspect(int pos, Aspect aspect) throws ProxyConfigException;

	boolean removeAspect(Aspect aspect);
	
	int indexOf(Aspect aspect);


	String toProxyConfigString();
}
