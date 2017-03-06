package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.TransactionException;

public class TransactionSuspensionNotSupportedException extends
		TransactionException {

	public TransactionSuspensionNotSupportedException(String msg) {
		super(msg);
	}

}
