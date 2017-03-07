package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.extension.ExtensionInfo;
import org.solmix.runtime.proxy.ProxyHandler;
import org.solmix.runtime.transaction.annotation.Transaction;

public class TxProxyHandler implements ProxyHandler,ContainerAware {

	private Container container;
	public boolean requireProxy(ExtensionInfo info) {
		Class<?> clz=info.getClassObject();
		return requireProxy(clz);
	}
	
	@Override
	public boolean requireProxy(Class<?>  clz) {
		Transaction tx=clz.getAnnotation(Transaction.class);
		return tx!=null;
	}

	@Override
	public Object proxy(Object instance, Class<?>[] proxyInterfaces) {
		TransactionPorxyFactory factory = new TransactionPorxyFactory();
		factory.setContainer(container);
		factory.setTarget(instance);
		factory.setProxyInterfaces(proxyInterfaces);
		return factory.getProxy();
	}

	@Override
	public void setContainer(Container container) {
		this.container=container;
		
	}

}
