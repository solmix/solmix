package org.solmix.runtime.transaction;


public interface TransactionManager {

	void commit(TransactionState status) throws TransactionException;

	void rollback(TransactionState status) throws TransactionException;

	TransactionState getTransaction(TransactionInfo status) throws TransactionException;
}
