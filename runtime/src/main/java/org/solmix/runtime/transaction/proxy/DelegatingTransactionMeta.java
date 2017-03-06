package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.transaction.TransactionIsolation;
import org.solmix.runtime.transaction.TransactionPolicy;
import org.solmix.runtime.transaction.config.TransactionMeta;

public class DelegatingTransactionMeta implements TransactionMeta {

	private TransactionMeta o;
	
	public DelegatingTransactionMeta(TransactionMeta o){
		this.o=o;
	}
	@Override
	public TransactionPolicy getTransactionPolicy() {
		return o.getTransactionPolicy();
	}

	@Override
	public TransactionIsolation getTransactionIsolation() {
		return o.getTransactionIsolation();
	}

	@Override
	public int getTimeout() {
		return o.getTimeout();
	}

	@Override
	public boolean isReadOnly() {
		return o.isReadOnly();
	}

	@Override
	public String getName() {
		return o.getName();
	}

	@Override
	public String getQualifier() {
		return o.getQualifier();
	}

	@Override
	public boolean rollbackOn(Throwable ex) {
		return o.rollbackOn(ex);
	}

}
