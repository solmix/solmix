package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.TransactionException;

public class UnexpectedRollbackException extends TransactionException {
	
	private static final long serialVersionUID = 587603708434273570L;

	public UnexpectedRollbackException() {

	}

	public UnexpectedRollbackException(Throwable e) {
		super(e);
	}

	public UnexpectedRollbackException(String include) {
		super(include);
	}

	/**
	 * @param string
	 * @param e
	 */
	public UnexpectedRollbackException(String string, Throwable e) {
		super(string, e);
	}
}
