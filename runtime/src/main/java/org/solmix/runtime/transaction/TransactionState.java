package org.solmix.runtime.transaction;

public interface TransactionState  extends SavepointSupport{

	boolean isNewTransaction();
	boolean hasSavepoint();
	void setRollbackOnly();
	boolean isRollbackOnly();
	void flush();
	boolean isCompleted();
}
