package org.solmix.runtime.transaction;

public interface TransactionInfo {

	int TIMEOUT_DEFAULT = -1;

	TransactionPolicy getTransactionPolicy();
	
	TransactionIsolation getTransactionIsolation();
	
	int getTimeout();
	
	boolean isReadOnly();
	
	String getName();
}
