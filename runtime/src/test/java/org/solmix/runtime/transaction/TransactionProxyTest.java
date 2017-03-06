package org.solmix.runtime.transaction;

import junit.framework.TestCase;

import org.solmix.runtime.proxy.support.ProxyManagerImpl;
import org.solmix.runtime.transaction.proxy.TxProxyHandler;
import org.solmix.tests.transaction.ITestManager;
import org.solmix.tests.transaction.TestManager;

public class TransactionProxyTest extends TestCase {

	
	public void  testProxy(){
		ProxyManagerImpl pm = new ProxyManagerImpl();
		pm.addHandler(new TxProxyHandler());
		TestManager tm = new TestManager();
		ITestManager ptm=(ITestManager)pm.proxy(tm, null);
		assertNotNull(ptm);
		ptm.update("1", "2");
	}
}
