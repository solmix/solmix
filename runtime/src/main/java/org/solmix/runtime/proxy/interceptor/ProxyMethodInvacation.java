package org.solmix.runtime.proxy.interceptor;

import org.solmix.runtime.proxy.support.MethodInvocation;

public interface ProxyMethodInvacation extends MethodInvocation {
	Object getProxy();

	MethodInvocation invocableClone();

	MethodInvocation invocableClone(Object[] arguments);

	void setArguments(Object[] arguments);

	void setUserAttribute(String key, Object value);

	Object getUserAttribute(String key);

}
