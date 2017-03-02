package org.solmix.runtime.transaction;


public interface SavepointSupport {

	Object createSavepoint() throws TransactionException;
	void rollbackToSavepoint(Object savepoint) throws TransactionException;
	void releaseSavepoint(Object savepoint) throws TransactionException;
}
