package org.solmix.runtime.transaction.support;

import java.util.Date;

import org.solmix.runtime.transaction.ResourceBinder;
import org.springframework.transaction.TransactionTimedOutException;

public  abstract class AbstractResourceBinder implements ResourceBinder {

	private boolean synchronizedWithTransaction = false;

	private boolean rollbackOnly = false;

	private Date deadline;

	private int referenceCount = 0;

	private boolean isVoid = false;


	/**
	 * Mark the resource as synchronized with a transaction.
	 */
	public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
		this.synchronizedWithTransaction = synchronizedWithTransaction;
	}

	/**
	 * Return whether the resource is synchronized with a transaction.
	 */
	public boolean isSynchronizedWithTransaction() {
		return this.synchronizedWithTransaction;
	}

	/**
	 * Mark the resource transaction as rollback-only.
	 */
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	/**
	 * Return whether the resource transaction is marked as rollback-only.
	 */
	public boolean isRollbackOnly() {
		return this.rollbackOnly;
	}

	/**
	 * Set the timeout for this object in seconds.
	 * @param seconds number of seconds until expiration
	 */
	public void setTimeoutInSeconds(int seconds) {
		setTimeoutInMillis(seconds * 1000);
	}

	/**
	 * Set the timeout for this object in milliseconds.
	 * @param millis number of milliseconds until expiration
	 */
	public void setTimeoutInMillis(long millis) {
		this.deadline = new Date(System.currentTimeMillis() + millis);
	}

	/**
	 * Return whether this object has an associated timeout.
	 */
	public boolean hasTimeout() {
		return (this.deadline != null);
	}

	/**
	 * Return the expiration deadline of this object.
	 * @return the deadline as Date object
	 */
	public Date getDeadline() {
		return this.deadline;
	}

	/**
	 * Return the time to live for this object in seconds.
	 * Rounds up eagerly, e.g. 9.00001 still to 10.
	 * @return number of seconds until expiration
	 * @throws TransactionTimedOutException if the deadline has already been reached
	 */
	public int getTimeToLiveInSeconds() {
		double diff = ((double) getTimeToLiveInMillis()) / 1000;
		int secs = (int) Math.ceil(diff);
		checkTransactionTimeout(secs <= 0);
		return secs;
	}

	/**
	 * Return the time to live for this object in milliseconds.
	 * @return number of millseconds until expiration
	 * @throws TransactionTimedOutException if the deadline has already been reached
	 */
	public long getTimeToLiveInMillis() throws TransactionTimedOutException{
		if (this.deadline == null) {
			throw new IllegalStateException("No timeout specified for this resource holder");
		}
		long timeToLive = this.deadline.getTime() - System.currentTimeMillis();
		checkTransactionTimeout(timeToLive <= 0);
		return timeToLive;
	}

	/**
	 * Set the transaction rollback-only if the deadline has been reached,
	 * and throw a TransactionTimedOutException.
	 */
	private void checkTransactionTimeout(boolean deadlineReached) throws TransactionTimedOutException {
		if (deadlineReached) {
			setRollbackOnly();
			throw new TransactionTimedOutException("Transaction timed out: deadline was " + this.deadline);
		}
	}

	/**
	 * Increase the reference count by one because the holder has been requested
	 * (i.e. someone requested the resource held by it).
	 */
	public void requested() {
		this.referenceCount++;
	}

	/**
	 * Decrease the reference count by one because the holder has been released
	 * (i.e. someone released the resource held by it).
	 */
	public void released() {
		this.referenceCount--;
	}

	/**
	 * Return whether there are still open references to this holder.
	 */
	public boolean isOpen() {
		return (this.referenceCount > 0);
	}

	/**
	 * Clear the transactional state of this resource holder.
	 */
	public void clear() {
		this.synchronizedWithTransaction = false;
		this.rollbackOnly = false;
		this.deadline = null;
	}

	/**
	 * Reset this resource holder - transactional state as well as reference count.
	 */
	@Override
	public void reset() {
		clear();
		this.referenceCount = 0;
	}

	@Override
	public void unbind() {
		this.isVoid = true;
	}

	@Override
	public boolean isVoid() {
		return this.isVoid;
	}


}
