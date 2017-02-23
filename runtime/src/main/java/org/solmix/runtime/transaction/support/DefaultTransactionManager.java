package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.TransactionService;

public class DefaultTransactionManager implements TransactionManager {

	@Override
	public void rollback() throws TransactionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() throws TransactionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bind(Object object, TransactionService transaction) {
		// TODO Auto-generated method stub

	}

	@Override
	public TransactionService getTransaction(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
