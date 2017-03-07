package org.solmix.runtime.transaction;

public class NestedTransactionNotSupportedException extends
		TransactionException {

	public NestedTransactionNotSupportedException(String string) {
		super(string);
	}

}
