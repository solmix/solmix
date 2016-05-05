package org.solmix.runtime.security;

import javax.net.ssl.SSLContext;

public interface SSLProvider {
	public SSLContext getSSLContext();
}

