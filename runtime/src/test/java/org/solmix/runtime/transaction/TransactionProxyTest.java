package org.solmix.runtime.transaction;

import junit.framework.TestCase;

import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.proxy.support.ProxyManagerImpl;
import org.solmix.runtime.transaction.proxy.TxProxyHandler;
import org.solmix.tests.transaction.FooManager;
import org.solmix.tests.transaction.FooTransactionManager;
import org.solmix.tests.transaction.IFooManager;

public class TransactionProxyTest extends TestCase {

	private Container c;
	@Override
	public void setUp(){
		c=ContainerFactory.getDefaultContainer(true);
	}
	@Test
	public void  testProxy(){
		ProxyManagerImpl pm = new ProxyManagerImpl(c);
		FooTransactionManager ftm = new FooTransactionManager(false, true);
		c.setExtension(ftm, TransactionManager.class);
		pm.addHandler(new TxProxyHandler());
		FooManager tm = new FooManager();
		IFooManager ptm=(IFooManager)pm.proxy(tm, null);
		assertNotNull(ptm);
		ptm.update("1", "2");
	}
	
	@Override
	public void tearDown(){
		if(c!=null){
			c.close();
		}
	}
}
