package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.extension.ExtensionInfo;
import org.solmix.runtime.proxy.ProxyHandler;
import org.solmix.runtime.transaction.annotation.Transaction;

public class TxProxyHandler implements ProxyHandler {

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
		factory.setTarget(instance);
		factory.setProxyInterfaces(proxyInterfaces);
		return factory.getProxy();
	}

}
