package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.TransactionObject;

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
	public void bind(Object object, TransactionObject transaction) {
		// TODO Auto-generated method stub

	}

	@Override
	public TransactionObject getTransaction(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
