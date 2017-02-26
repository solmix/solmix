package org.solmix.runtime.proxy;

@SuppressWarnings("serial")
public class ProxyInvocationException extends RuntimeException {

	public ProxyInvocationException(String msg) {
		super(msg);
	}

	public ProxyInvocationException(String msg, Throwable ex) {
		super(msg,ex);
	}

}
