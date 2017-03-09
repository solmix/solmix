package org.solmix.runtime.proxy;

public interface ProxyRule {

	boolean isOptimize();

	boolean isExposeProxy();

	boolean isProxyTargetClass();

	boolean match(String name);

	ProxyHandler getProxyHandler();
}
