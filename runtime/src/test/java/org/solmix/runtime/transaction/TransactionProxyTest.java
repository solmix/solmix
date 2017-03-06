package org.solmix.runtime.transaction;

import junit.framework.TestCase;

import org.solmix.runtime.proxy.support.ProxyManagerImpl;
import org.solmix.runtime.transaction.proxy.TxProxyHandler;

public class TransactionProxyTest extends TestCase {

	
	public void  testProxy(){
		ProxyManagerImpl pm = new ProxyManagerImpl();
		pm.addHandler(new TxProxyHandler());
	}
}
