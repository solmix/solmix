package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.TransactionException;

public class IllegalTransactionStateException extends TransactionException {

	public IllegalTransactionStateException(String msg) {
		super(msg);
	}

}
