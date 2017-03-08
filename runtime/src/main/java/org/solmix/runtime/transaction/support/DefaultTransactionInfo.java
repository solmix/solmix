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
	
	@Override
	public String toString(){
		return getTransactionDescription().toString();
	}

	/**输出事务信息状态*/
	protected final StringBuilder getTransactionDescription() {
		StringBuilder result = new StringBuilder();
		result.append("[TransactionPolicy:");
		result.append(this.policy);
		result.append(",TransactionIsolation:");
		result.append(this.isolation);
		if (this.timeout != TIMEOUT_DEFAULT) {
			result.append(',');
			result.append(",timeout:").append(this.timeout);
		}
		if (this.readOnly) {
			result.append(",readOnly");
		}
		result.append("[");
		return result;
	}
}
