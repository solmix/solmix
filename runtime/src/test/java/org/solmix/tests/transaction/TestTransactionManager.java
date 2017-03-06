package org.solmix.tests.transaction;

import org.solmix.runtime.transaction.CreateTransactionException;
import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionInfo;
import org.solmix.runtime.transaction.support.AbstractTransactionManager;
import org.solmix.runtime.transaction.support.DefaultTransactionState;

public class TestTransactionManager extends AbstractTransactionManager {

	private static final Object TRANSACTION = "transaction";

	private final boolean existingTransaction;

	private final boolean canCreateTransaction;

	protected boolean begin = false;

	protected boolean commit = false;

	protected boolean rollback = false;

	protected boolean rollbackOnly = false;

	public TestTransactionManager(boolean existingTransaction,
			boolean canCreateTransaction) {
		this.existingTransaction = existingTransaction;
		this.canCreateTransaction = canCreateTransaction;
		setTransactionSynchronization(SYNCHRONIZATION_NEVER);
	}

	@Override
	protected void doCommit(DefaultTransactionState status)
			throws TransactionException {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException(
					"Not the same transaction object");
		}
		this.commit = true;
	}

	@Override
	protected void doRollback(DefaultTransactionState status)
			throws TransactionException {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException(
					"Not the same transaction object");
		}
		this.rollback = true;
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionState status) {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException(
					"Not the same transaction object");
		}
		this.rollbackOnly = true;
	}

	@Override
	protected void doBegin(Object transaction, TransactionInfo definition)
			throws TransactionException {
		if (!TRANSACTION.equals(transaction)) {
			throw new IllegalArgumentException(
					"Not the same transaction object");
		}
		if (!this.canCreateTransaction) {
			throw new CreateTransactionException("Cannot create transaction");
		}
		this.begin = true;
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
		return existingTransaction;
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return TRANSACTION;
	}
	@Override
	protected Object doSuspend(Object transaction) throws TransactionException {
		return transaction;
	}

	public boolean isBegin() {
		return begin;
	}

	public boolean isCommit() {
		return commit;
	}

	public boolean isRollback() {
		return rollback;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

}
