package org.solmix.runtime.proxy;

@SuppressWarnings("serial")
public class ProxyConfigException extends RuntimeException {

	public ProxyConfigException(String msg) {
		super(msg);
	}

	public ProxyConfigException(String msg, Throwable ex) {
		super(msg,ex);
	}

}
