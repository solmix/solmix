package org.solmix.runtime.transaction.support;

import java.io.Serializable;

import org.solmix.runtime.transaction.TransactionInfo;
import org.solmix.runtime.transaction.TransactionIsolation;
import org.solmix.runtime.transaction.TransactionPolicy;

public class DefaultTransactionInfo implements TransactionInfo, Serializable {

	private static final long serialVersionUID = 1L;
	private TransactionIsolation isolation=TransactionIsolation.DEFAULT;
	private TransactionPolicy policy=TransactionPolicy.REQUIRED;
	private String name;
	private int timeout;
	private boolean readOnly;
	
	public DefaultTransactionInfo() {
	}
	
	public DefaultTransactionInfo(TransactionInfo other) {
		this.policy=other.getTransactionPolicy();
		this.isolation=other.getTransactionIsolation();
		this.name=other.getName();
		this.timeout=other.getTimeout();
		this.readOnly=other.isReadOnly();
	}
	
	public DefaultTransactionInfo(TransactionPolicy policy) {
		this.policy = policy;
	}

	@Override
	public TransactionPolicy getTransactionPolicy() {
		return policy;
	}
	
	public void setTransactionPolicy(TransactionPolicy policy){
		this.policy=policy;
	}

	@Override
	public TransactionIsolation getTransactionIsolation() {
		return isolation;
	}

	public void setTransactionIsolation(TransactionIsolation i){
		this.isolation=i;
	}
	@Override
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout){
		if (timeout < -1) {
			throw new IllegalArgumentException("Timeout must be a positive integer or TIMEOUT_DEFAULT");
		}
		this.timeout = timeout;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly){
		this.readOnly=readOnly;
	}

	@Override
	public String getName() {
		return name;
	}

}
