package org.solmix.runtime.transaction;

public interface TransactionState  extends SavepointSupport{

	boolean isNewTransaction();
	boolean hasSavepoint();
	/**
	 * 将事务状态标记为rollback
	 */
	void setRollbackOnly();
	boolean isRollbackOnly();
	void flush();
	boolean isCompleted();
}
