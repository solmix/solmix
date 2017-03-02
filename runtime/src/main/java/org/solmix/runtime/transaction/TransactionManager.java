package org.solmix.runtime.transaction;


public interface TransactionManager {

	void rollback() throws TransactionException;

    void commit() throws TransactionException;

    void bind(Object object, TransactionObject transaction);

//    Map<Object, Transaction> getTransactions();

    TransactionObject getTransaction(Object object);
}
