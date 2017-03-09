package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.proxy.ProxyHandler;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.TxProxyRule;
import org.solmix.runtime.transaction.annotation.Transaction;

public class TxProxyHandler implements ProxyHandler, ContainerAware {

	private Container container;
	private TransactionManager transactionManager;
	private TxProxyRule rule;

	public TxProxyHandler() {

	}

	public TxProxyHandler(TxProxyRule rule) {
		this.rule=rule;
	}

	public boolean requireProxy(Object instance, Class<?>[] interfaces) {
		boolean proxyed = instance.getClass().isAnnotationPresent(
				Transaction.class);
		if (interfaces != null && interfaces.length > 0) {
			for (Class<?> inf : interfaces) {
				proxyed = proxyed || inf.isAnnotationPresent(Transaction.class);
			}
		}
		return proxyed;
	}

	@Override
	public Object proxy(Object instance, Class<?>[] interfaces) {
		 if(!requireProxy(instance,interfaces)){
			 return instance;
		 }
		TransactionPorxyFactory factory = new TransactionPorxyFactory(transactionManager);
		factory.setContainer(container);
		factory.setTarget(instance);
		factory.setOptimize(rule.isOptimize());
		factory.setProxyTargetClass(rule.isProxyTargetClass());
		factory.setExposeProxy(rule.isExposeProxy());
		return factory.getProxy();
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;

	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
