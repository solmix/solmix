package org.solmix.runtime.transaction;

import java.lang.reflect.UndeclaredThrowableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.exception.InvokerException;
import org.solmix.runtime.transaction.support.DefaultTransactionInfo;

public class TransactionSupport extends DefaultTransactionInfo {

	private static final long serialVersionUID = 6485705229883118517L;

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSupport.class);

	private TransactionManager transactionManager;

	public TransactionSupport() {

	}

	public TransactionSupport(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public TransactionSupport(TransactionManager transactionManager,
			TransactionInfo info) {
		super(info);
		this.transactionManager = transactionManager;

	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public <T> T execute(TransactionCallback<T> action)throws TransactionException {

		TransactionState status = this.transactionManager.getTransaction(this);
		T result;
		try {
			result = action.doInTransaction(status);
		} catch (InvokerException ex) {
			rollbackOnException(status, ex.getCause());
			throw ex;
		} catch (RuntimeException ex) {
			rollbackOnException(status, ex);
			throw ex;
		} catch (Error err) {
			rollbackOnException(status, err);
			throw err;
		} catch (Exception ex) {
			rollbackOnException(status, ex);
			throw new UndeclaredThrowableException(ex,"TransactionCallback threw undeclared checked exception");
		}
		this.transactionManager.commit(status);
		return result;
	}

	private void rollbackOnException(TransactionState status, Throwable ex)
			throws TransactionException {
		LOG.debug("Initiating transaction rollback on application exception",ex);
		try {
			this.transactionManager.rollback(status);
		} catch (TransactionException ex2) {
			LOG.error("Application exception overridden by rollback exception",ex);
			throw ex2;
		} catch (RuntimeException ex2) {
			LOG.error("Application exception overridden by rollback exception",ex);
			throw ex2;
		} catch (Error err) {
			LOG.error("Application exception overridden by rollback error", ex);
			throw err;
		}
	}

}
