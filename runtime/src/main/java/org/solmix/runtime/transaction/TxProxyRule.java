package org.solmix.runtime.transaction;

import org.solmix.commons.regex.SimpleUrlPattern;
import org.solmix.runtime.proxy.ProxyHandler;
import org.solmix.runtime.proxy.ProxyRule;
import org.solmix.runtime.transaction.proxy.TxProxyHandler;

public class TxProxyRule implements ProxyRule {
	private TransactionManager transactionManager;
	private boolean proxyTargetClass = false;

	private boolean optimize = false;

	private SimpleUrlPattern pattern;

	boolean exposeProxy = false;
	TxProxyHandler handler = new TxProxyHandler(this);

	@Override
	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	@Override
	public boolean isOptimize() {
		return optimize;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	@Override
	public boolean isExposeProxy() {
		return exposeProxy;
	}

	public void setExposeProxy(boolean exposeProxy) {
		this.exposeProxy = exposeProxy;
	}

	@Override
	public boolean match(String name) {
		if(pattern!=null){
			return pattern.match(name);
		}else{
			return true;
		}
		
	}

	@Override
	public ProxyHandler getProxyHandler() {
		
		return handler;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
		handler.setTransactionManager(transactionManager);
	}


	public void setFilter(String filter) {
		pattern = new SimpleUrlPattern(filter);
	}

	
}
