package org.solmix.runtime.transaction;

public interface TransactionInfo {

	TransactionPolicy getTransactionPolicy();
	
	TransactionIsolation getTransactionIsolation();
	
	int getTimeout();
	
	boolean isReadOnly();
	
	String getName();
}
