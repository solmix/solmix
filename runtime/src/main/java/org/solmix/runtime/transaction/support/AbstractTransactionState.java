package org.solmix.runtime.transaction.support;

import org.solmix.runtime.transaction.SavepointException;
import org.solmix.runtime.transaction.SavepointSupport;
import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionState;

public abstract class AbstractTransactionState implements TransactionState {

	private boolean rollbackOnly = false;

	private boolean completed = false;

	private Object savepoint;


	//---------------------------------------------------------------------
	// Handling of current transaction state
	//---------------------------------------------------------------------

	@Override
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	/**
	 * Determine the rollback-only flag via checking both the local rollback-only flag
	 * of this TransactionState and the global rollback-only flag of the underlying
	 * transaction, if any.
	 * @see #isLocalRollbackOnly()
	 * @see #isGlobalRollbackOnly()
	 */
	@Override
	public boolean isRollbackOnly() {
		return (isLocalRollbackOnly() || isGlobalRollbackOnly());
	}

	/**
	 * Determine the rollback-only flag via checking this TransactionState.
	 * <p>Will only return "true" if the application called {@code setRollbackOnly}
	 * on this TransactionState object.
	 */
	public boolean isLocalRollbackOnly() {
		return this.rollbackOnly;
	}

	/**
	 * Template method for determining the global rollback-only flag of the
	 * underlying transaction, if any.
	 * <p>This implementation always returns {@code false}.
	 */
	public boolean isGlobalRollbackOnly() {
		return false;
	}

	/**
	 * This implementations is empty, considering flush as a no-op.
	 */
	@Override
	public void flush() {
	}

	/**
	 * Mark this transaction as completed, that is, committed or rolled back.
	 */
	public void setCompleted() {
		this.completed = true;
	}

	@Override
	public boolean isCompleted() {
		return this.completed;
	}


	//---------------------------------------------------------------------
	// Handling of current savepoint state
	//---------------------------------------------------------------------

	/**
	 * Set a savepoint for this transaction. Useful for PROPAGATION_NESTED.
	 */
	protected void setSavepoint(Object savepoint) {
		this.savepoint = savepoint;
	}

	/**
	 * Get the savepoint for this transaction, if any.
	 */
	protected Object getSavepoint() {
		return this.savepoint;
	}

	@Override
	public boolean hasSavepoint() {
		return (this.savepoint != null);
	}

	/**
	 * Create a savepoint and hold it for the transaction.
	 * if the underlying transaction does not support savepoints
	 */
	public void createAndHoldSavepoint() throws TransactionException {
		setSavepoint(getSavepointSupport().createSavepoint());
	}

	/**
	 * Roll back to the savepoint that is held for the transaction.
	 */
	public void rollbackToHeldSavepoint() throws TransactionException {
		if (!hasSavepoint()) {
			throw new SavepointException("No savepoint associated with current transaction");
		}
		getSavepointSupport().rollbackToSavepoint(getSavepoint());
		setSavepoint(null);
	}

	/**
	 * Release the savepoint that is held for the transaction.
	 */
	public void releaseHeldSavepoint() throws TransactionException {
		if (!hasSavepoint()) {
			throw new SavepointException("No savepoint associated with current transaction");
		}
		getSavepointSupport().releaseSavepoint(getSavepoint());
		setSavepoint(null);
	}


	//---------------------------------------------------------------------
	// Implementation of SavepointManager
	//---------------------------------------------------------------------

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * @see #getSavepoint()
	 */
	@Override
	public Object createSavepoint() throws TransactionException {
		return getSavepointSupport().createSavepoint();
	}

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * @see #getSavepoint()
	 */
	@Override
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		getSavepointSupport().rollbackToSavepoint(savepoint);
	}

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * @see #getSavepoint()
	 */
	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		getSavepointSupport().releaseSavepoint(savepoint);
	}

	/**
	 * Return a SavepointManager for the underlying transaction, if possible.
	 * <p>Default implementation always throws a NestedTransactionNotSupportedException.
	 * if the underlying transaction does not support savepoints
	 */
	protected SavepointSupport getSavepointSupport() {
		throw new SavepointException("This transaction does not support savepoints");
	}

}
