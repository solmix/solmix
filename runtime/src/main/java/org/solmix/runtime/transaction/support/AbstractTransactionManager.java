package org.solmix.runtime.transaction.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.transaction.NestedTransactionNotSupportedException;
import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionInfo;
import org.solmix.runtime.transaction.TransactionIsolation;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.TransactionPolicy;
import org.solmix.runtime.transaction.TransactionState;

public abstract class AbstractTransactionManager implements TransactionManager {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractTransactionManager.class);
	public static final int SYNCHRONIZATION_ALWAYS = 0;
	public static final int SYNCHRONIZATION_ON_ACTUAL_TRANSACTION = 1;
	public static final int SYNCHRONIZATION_NEVER = 2;
	private int transactionSynchronization = SYNCHRONIZATION_ALWAYS;
	private int defaultTimeout = TransactionInfo.TIMEOUT_DEFAULT;
	
	private boolean nestedTransactionAllowed = false;

	private boolean validateExistingTransaction = false;

	private boolean globalRollbackOnParticipationFailure = true;

	private boolean failEarlyOnGlobalRollbackOnly = false;

	private boolean rollbackOnCommitFailure = false;

	@Override
	public void commit(TransactionState state)throws TransactionException {
		if (state.isCompleted()) {
			throw new TransactionException(
					"Transaction is already completed - do not call commit or rollback more than once per transaction");
		}

		DefaultTransactionState defStatus = (DefaultTransactionState) state;
		if (defStatus.isLocalRollbackOnly()) {
			if (defStatus.isDebug()) {
				LOG.debug("Transactional code has requested rollback");
			}
			processRollback(defStatus);
			return;
		}
		if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
			if (defStatus.isDebug()) {
				LOG.debug("Global transaction is marked as rollback-only but transactional code requested commit");
			}
			processRollback(defStatus);
			// Throw UnexpectedRollbackException only at outermost transaction boundary
			// or if explicitly asked to.
			if (state.isNewTransaction() || isFailEarlyOnGlobalRollbackOnly()) {
				throw new UnexpectedRollbackException("Transaction rolled back because it has been marked as rollback-only");
			}
			return;
		}

		processCommit(defStatus);
	}
	
	private void processCommit(DefaultTransactionState status) throws TransactionException {
		try {
			boolean beforeCompletionInvoked = false;
			try {
				prepareForCommit(status);
				triggerBeforeCommit(status);
				triggerBeforeCompletion(status);
				beforeCompletionInvoked = true;
				boolean globalRollbackOnly = false;
				if (status.isNewTransaction() || isFailEarlyOnGlobalRollbackOnly()) {
					globalRollbackOnly = status.isGlobalRollbackOnly();
				}
				if (status.hasSavepoint()) {
					if (status.isDebug()) {
						LOG.debug("Releasing transaction savepoint");
					}
					status.releaseHeldSavepoint();
				}else if (status.isNewTransaction()) {
					if (status.isDebug()) {
						LOG.debug("Initiating transaction commit");
					}
					doCommit(status);
				}
				// Throw UnexpectedRollbackException if we have a global rollback-only
				// marker but still didn't get a corresponding exception from commit.
				if (globalRollbackOnly) {
					throw new UnexpectedRollbackException(
							"Transaction silently rolled back because it has been marked as rollback-only");
				}
			}catch (UnexpectedRollbackException ex) {
				// can only be caused by doCommit
				triggerAfterCompletion(status, TransactionListener.STATUS_ROLLED_BACK);
				throw ex;
			}
			catch (TransactionException ex) {
				// can only be caused by doCommit
				if (isRollbackOnCommitFailure()) {
					doRollbackOnCommitException(status, ex);
				}else {
					triggerAfterCompletion(status, TransactionListener.STATUS_UNKNOWN);
				}
				throw ex;
			}catch (RuntimeException ex) {
				if (!beforeCompletionInvoked) {
					triggerBeforeCompletion(status);
				}
				doRollbackOnCommitException(status, ex);
				throw ex;
			}catch (Error err) {
				if (!beforeCompletionInvoked) {
					triggerBeforeCompletion(status);
				}
				doRollbackOnCommitException(status, err);
				throw err;
			}

			// Trigger afterCommit callbacks, with an exception thrown there
			// propagated to callers but the transaction still considered as committed.
			try {
				triggerAfterCommit(status);
			}finally {
				triggerAfterCompletion(status, TransactionListener.STATUS_COMMITTED);
			}

		}finally {
			cleanupAfterCompletion(status);
		}
	}
	
	protected int determineTimeout(TransactionInfo definition) {
		if (definition.getTimeout() != TransactionInfo.TIMEOUT_DEFAULT) {
			return definition.getTimeout();
		}
		return this.defaultTimeout;
	}
	private void cleanupAfterCompletion(DefaultTransactionState status) {
		status.setCompleted();
		if (status.isNewSynchronization()) {
			TxSynchronizer.clear();
		}
		if (status.isNewTransaction()) {
			doCleanupAfterCompletion(status.getTransaction());
		}
		if (status.getSuspendedResources() != null) {
			if (status.isDebug()) {
				LOG.debug("Resuming suspended transaction after completion of inner transaction");
			}
			resume(status.getTransaction(), (SuspendedResourcesHolder) status.getSuspendedResources());
		}
	}
	
	protected final void resume(Object transaction, SuspendedResourcesHolder resourcesHolder)
			throws TransactionException {

		if (resourcesHolder != null) {
			Object suspendedResources = resourcesHolder.suspendedResources;
			if (suspendedResources != null) {
				doResume(transaction, suspendedResources);
			}
			List<TransactionListener> suspendedSynchronizations = resourcesHolder.suspendedSynchronizations;
			if (suspendedSynchronizations != null) {
				TxSynchronizer.setActualTransactionActive(resourcesHolder.wasActive);
				TxSynchronizer.setCurrentTransactionIsolationLevel(resourcesHolder.isolationLevel);
				TxSynchronizer.setCurrentTransactionReadOnly(resourcesHolder.readOnly);
				TxSynchronizer.setCurrentTransactionName(resourcesHolder.name);
				doResumeSynchronization(suspendedSynchronizations);
			}
		}
	}
	
	private void doResumeSynchronization(List<TransactionListener> suspendedSynchronizations) {
		TxSynchronizer.initSynchronization();
		for (TransactionListener synchronization : suspendedSynchronizations) {
			synchronization.resume();
			TxSynchronizer.registerSynchronization(synchronization);
		}
	}

	
	protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
		throw new TransactionSuspensionNotSupportedException(
				"Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
	}
	
	private void triggerAfterCommit(DefaultTransactionState status) {
		if (status.isNewSynchronization()) {
			if (status.isDebug()) {
				LOG.trace("Triggering afterCommit synchronization");
			}
			TxSynchronizers.triggerAfterCommit();
		}
	}
	
	public final boolean isRollbackOnCommitFailure() {
		return this.rollbackOnCommitFailure;
	}
	
	private void doRollbackOnCommitException(DefaultTransactionState status, Throwable ex) throws TransactionException {
		try {
			if (status.isNewTransaction()) {
				if (status.isDebug()) {
					LOG.debug("Initiating transaction rollback after commit exception", ex);
				}
				doRollback(status);
			}
			else if (status.hasTransaction() && isGlobalRollbackOnParticipationFailure()) {
				if (status.isDebug()) {
					LOG.debug("Marking existing transaction as rollback-only after commit exception", ex);
				}
				doSetRollbackOnly(status);
			}
		}
		catch (RuntimeException rbex) {
			LOG.error("Commit exception overridden by rollback exception", ex);
			triggerAfterCompletion(status, TransactionListener.STATUS_UNKNOWN);
			throw rbex;
		}
		catch (Error rberr) {
			LOG.error("Commit exception overridden by rollback exception", ex);
			triggerAfterCompletion(status, TransactionListener.STATUS_UNKNOWN);
			throw rberr;
		}
		triggerAfterCompletion(status, TransactionListener.STATUS_ROLLED_BACK);
	}
	
	public final boolean isGlobalRollbackOnParticipationFailure() {
		return this.globalRollbackOnParticipationFailure;
	}
	public final void setNestedTransactionAllowed(boolean nestedTransactionAllowed) {
		this.nestedTransactionAllowed = nestedTransactionAllowed;
	}

	/**
	 * Return whether nested transactions are allowed.
	 */
	public final boolean isNestedTransactionAllowed() {
		return this.nestedTransactionAllowed;
	}
	
	private void processRollback(DefaultTransactionState status) {
		try {
			try {
				triggerBeforeCompletion(status);
				if (status.hasSavepoint()) {
					if (status.isDebug()) {
						LOG.debug("Rolling back transaction to savepoint");
					}
					status.rollbackToHeldSavepoint();
				} else if (status.isNewTransaction()) {
					if (status.isDebug()) {
						LOG.debug("Initiating transaction rollback");
					}
					doRollback(status);
				} else if (status.hasTransaction()) {
					if (status.isLocalRollbackOnly()
							|| isGlobalRollbackOnParticipationFailure()) {
						if (status.isDebug()) {
							LOG.debug("Participating transaction failed - marking existing transaction as rollback-only");
						}
						doSetRollbackOnly(status);
					} else {
						if (status.isDebug()) {
							LOG.debug("Participating transaction failed - letting transaction originator decide on rollback");
						}
					}
				} else {
					LOG.debug("Should roll back transaction but cannot - no transaction available");
				}
			} catch (RuntimeException ex) {
				triggerAfterCompletion(status,
						TransactionListener.STATUS_UNKNOWN);
				throw ex;
			} catch (Error err) {
				triggerAfterCompletion(status,
						TransactionListener.STATUS_UNKNOWN);
				throw err;
			}
			triggerAfterCompletion(status,
					TransactionListener.STATUS_ROLLED_BACK);
		} finally {
			cleanupAfterCompletion(status);
		}
	}

	
	protected final void triggerBeforeCompletion(DefaultTransactionState status) {
		if (status.isNewSynchronization()) {
			if (status.isDebug()) {
				LOG.trace("Triggering beforeCompletion synchronization");
			}
			TxSynchronizers.triggerBeforeCompletion();
		}
	}

	
	protected boolean shouldCommitOnGlobalRollbackOnly() {
		return false;
	}
	public final void setFailEarlyOnGlobalRollbackOnly(boolean failEarlyOnGlobalRollbackOnly) {
		this.failEarlyOnGlobalRollbackOnly = failEarlyOnGlobalRollbackOnly;
	}

	/**
	 * Return whether to fail early in case of the transaction being globally marked
	 * as rollback-only.
	 */
	public final boolean isFailEarlyOnGlobalRollbackOnly() {
		return this.failEarlyOnGlobalRollbackOnly;
	}
	@Override
	public void rollback(TransactionState status)
			throws TransactionException {
		if (status.isCompleted()) {
			throw new IllegalTransactionStateException(
					"Transaction is already completed - do not call commit or rollback more than once per transaction");
		}

		DefaultTransactionState defStatus = (DefaultTransactionState) status;
		processRollback(defStatus);
	}
	public final void setTransactionSynchronization(int transactionSynchronization) {
		this.transactionSynchronization = transactionSynchronization;
	}

	/**
	 * Return if this transaction manager should activate the thread-bound
	 * transaction synchronization support.
	 */
	public final int getTransactionSynchronization() {
		return this.transactionSynchronization;
	}

	public final void setValidateExistingTransaction(boolean validateExistingTransaction) {
		this.validateExistingTransaction = validateExistingTransaction;
	}

	/**
	 * Return whether existing transactions should be validated before participating
	 * in them.
	 */
	public final boolean isValidateExistingTransaction() {
		return this.validateExistingTransaction;
	}
	
	private List<TransactionListener> doSuspendSynchronization() {
		List<TransactionListener> suspendedSynchronizations =
				TxSynchronizer.getListeners();
		for (TransactionListener synchronization : suspendedSynchronizations) {
			synchronization.suspend();
		}
		TxSynchronizer.clearSynchronization();
		return suspendedSynchronizations;
	}

	@Override
	public TransactionState getTransaction(TransactionInfo info)
			throws TransactionException {
		Object transaction = doGetTransaction();

		boolean debugEnabled = LOG.isDebugEnabled();

		if (info == null) {
			// 使用默认的值.
			info = new DefaultTransactionInfo();
		}

		if (isExistingTransaction(transaction)) {
			// 已经存在事物，则根据事物策略来处理.
			return handleExistingTransaction(info, transaction, debugEnabled);
		}

		if (info.getTimeout() < TransactionInfo.TIMEOUT_DEFAULT) {
			throw new TransactionException("Invalid transaction timeout"+info.getTimeout());
		}

		// 不存在事物，但是事物策略需要事物，直接抛错.
		if (info.getTransactionPolicy() == TransactionPolicy.MANDATORY) {
			throw new IllegalTransactionStateException(
					"No existing transaction found for transaction marked with policy 'MANDATORY'");
		}else if (info.getTransactionPolicy() == TransactionPolicy.REQUIRED ||
				  info.getTransactionPolicy() == TransactionPolicy.NEW ||
				  info.getTransactionPolicy() == TransactionPolicy.EMBED) {
			SuspendedResourcesHolder suspendedResources = suspend(null);
			if (debugEnabled) {
				LOG.debug("Creating new transaction with name [" + info.getName() + "]: " + info);
			}
			try {
				boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
				DefaultTransactionState status = newTransactionStatus(
						info, transaction, true, newSynchronization, debugEnabled, suspendedResources);
				doBegin(transaction, info);
				prepareSynchronization(status, info);
				return status;
			}
			catch (RuntimeException ex) {
				resume(null, suspendedResources);
				throw ex;
			}
			catch (Error err) {
				resume(null, suspendedResources);
				throw err;
			}
		}
		else {
			// Create "empty" transaction: no actual transaction, but potentially synchronization.
			boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
			return prepareTransactionStatus(info, null, true, newSynchronization, debugEnabled, null);
		}
	}
	
	protected final SuspendedResourcesHolder suspend(Object transaction) throws TransactionException {
		if (TxSynchronizer.isSynchronizationActive()) {
			List<TransactionListener> suspendedSynchronizations = doSuspendSynchronization();
			try {
				Object suspendedResources = null;
				if (transaction != null) {
					suspendedResources = doSuspend(transaction);
				}
				String name = TxSynchronizer.getCurrentTransactionName();
				TxSynchronizer.setCurrentTransactionName(null);
				boolean readOnly = TxSynchronizer.isCurrentTransactionReadOnly();
				TxSynchronizer.setCurrentTransactionReadOnly(false);
				TransactionIsolation isolationLevel = TxSynchronizer.getCurrentTransactionIsolation();
				TxSynchronizer.setCurrentTransactionIsolationLevel(null);
				boolean wasActive = TxSynchronizer.isActualTransactionActive();
				TxSynchronizer.setActualTransactionActive(false);
				return new SuspendedResourcesHolder(
						suspendedResources, suspendedSynchronizations, name, readOnly, isolationLevel, wasActive);
			}
			catch (RuntimeException ex) {
				// doSuspend failed - original transaction is still active...
				doResumeSynchronization(suspendedSynchronizations);
				throw ex;
			}
			catch (Error err) {
				// doSuspend failed - original transaction is still active...
				doResumeSynchronization(suspendedSynchronizations);
				throw err;
			}
		}
		else if (transaction != null) {
			// Transaction active but no synchronization active.
			Object suspendedResources = doSuspend(transaction);
			return new SuspendedResourcesHolder(suspendedResources);
		}
		else {
			// Neither transaction nor synchronization active.
			return null;
		}
	}
	
	protected final DefaultTransactionState prepareTransactionStatus(
			TransactionInfo definition, Object transaction, boolean newTransaction,
			boolean newSynchronization, boolean debug, Object suspendedResources) {

		DefaultTransactionState status = newTransactionStatus(
				definition, transaction, newTransaction, newSynchronization, debug, suspendedResources);
		prepareSynchronization(status, definition);
		return status;
	}
	
	protected void prepareSynchronization(DefaultTransactionState status, TransactionInfo definition) {
		if (status.isNewSynchronization()) {
			TxSynchronizer.setActualTransactionActive(status.hasTransaction());
			TxSynchronizer.setCurrentTransactionIsolationLevel(
					(definition.getTransactionIsolation()!= TransactionIsolation.DEFAULT) ?
							definition.getTransactionIsolation() : null);
			TxSynchronizer.setCurrentTransactionReadOnly(definition.isReadOnly());
			TxSynchronizer.setCurrentTransactionName(definition.getName());
			TxSynchronizer.initSynchronization();
		}
	}
	
	protected DefaultTransactionState newTransactionStatus(
			TransactionInfo definition, Object transaction, boolean newTransaction,
			boolean newSynchronization, boolean debug, Object suspendedResources) {

		boolean actualNewSynchronization = newSynchronization &&
				!TxSynchronizer.isSynchronizationActive();
		return new DefaultTransactionState(
				transaction, newTransaction, actualNewSynchronization,
				definition.isReadOnly(), debug, suspendedResources);
	}
	
	private TransactionState handleExistingTransaction(
			TransactionInfo info, Object transaction, boolean debugEnabled)
			throws TransactionException {

		if (info.getTransactionPolicy()== TransactionPolicy.NEVER) {
			throw new IllegalTransactionStateException(
					"Existing transaction found for transaction marked with propagation 'never'");
		}

		if (info.getTransactionPolicy() == TransactionPolicy.NOT_SUPPORTED) {
			if (debugEnabled) {
				LOG.debug("Suspending current transaction");
			}
			Object suspendedResources = suspend(transaction);
			boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
			return prepareTransactionStatus(
					info, null, false, newSynchronization, debugEnabled, suspendedResources);
		}

		if (info.getTransactionPolicy() == TransactionPolicy.NEW) {
			if (debugEnabled) {
				LOG.debug("Suspending current transaction, creating new transaction with name [" +
						info.getName() + "]");
			}
			SuspendedResourcesHolder suspendedResources = suspend(transaction);
			try {
				boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
				DefaultTransactionState status = newTransactionStatus(
						info, transaction, true, newSynchronization, debugEnabled, suspendedResources);
				doBegin(transaction, info);
				prepareSynchronization(status, info);
				return status;
			}
			catch (RuntimeException beginEx) {
				resumeAfterBeginException(transaction, suspendedResources, beginEx);
				throw beginEx;
			}
			catch (Error beginErr) {
				resumeAfterBeginException(transaction, suspendedResources, beginErr);
				throw beginErr;
			}
		}

		if (info.getTransactionPolicy() == TransactionPolicy.EMBED) {
			if (!isNestedTransactionAllowed()) {
				throw new NestedTransactionNotSupportedException(
						"Transaction manager does not allow nested transactions by default - " +
						"specify 'nestedTransactionAllowed' property with value 'true'");
			}
			if (debugEnabled) {
				LOG.debug("Creating nested transaction with name [" + info.getName() + "]");
			}
			if (useSavepointForNestedTransaction()) {
				// Create savepoint within existing Spring-managed transaction,
				// through the SavepointManager API implemented by TransactionStatus.
				// Usually uses JDBC 3.0 savepoints. Never activates Spring synchronization.
				DefaultTransactionState status =
						prepareTransactionStatus(info, transaction, false, false, debugEnabled, null);
				status.createAndHoldSavepoint();
				return status;
			}
			else {
				// Nested transaction through nested begin and commit/rollback calls.
				// Usually only for JTA: Spring synchronization might get activated here
				// in case of a pre-existing JTA transaction.
				boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
				DefaultTransactionState status = newTransactionStatus(
						info, transaction, true, newSynchronization, debugEnabled, null);
				doBegin(transaction, info);
				prepareSynchronization(status, info);
				return status;
			}
		}

		// Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
		if (debugEnabled) {
			LOG.debug("Participating in existing transaction");
		}
		if (isValidateExistingTransaction()) {
			if (info.getTransactionIsolation() != TransactionIsolation.DEFAULT) {
				TransactionIsolation currentIsolationLevel = TxSynchronizer.getCurrentTransactionIsolation();
				if (currentIsolationLevel == null || currentIsolationLevel != info.getTransactionIsolation()) {
					throw new IllegalTransactionStateException("Participating transaction with definition [" +
							info + "] specifies isolation level which is incompatible with existing transaction: (unknown)");
				}
			}
			if (!info.isReadOnly()) {
				if (TxSynchronizer.isCurrentTransactionReadOnly()) {
					throw new IllegalTransactionStateException("Participating transaction with definition [" +
							info + "] is not marked as read-only but existing transaction is");
				}
			}
		}
		boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
		return prepareTransactionStatus(info, transaction, false, newSynchronization, debugEnabled, null);
	}
	
	private void resumeAfterBeginException(
			Object transaction, SuspendedResourcesHolder suspendedResources, Throwable beginEx) {

		String exMessage = "Inner transaction begin exception overridden by outer transaction resume exception";
		try {
			resume(transaction, suspendedResources);
		}
		catch (RuntimeException resumeEx) {
			LOG.error(exMessage, beginEx);
			throw resumeEx;
		}
		catch (Error resumeErr) {
			LOG.error(exMessage, beginEx);
			throw resumeErr;
		}
	}
	
	protected final void triggerBeforeCommit(DefaultTransactionState status) {
		if (status.isNewSynchronization()) {
			if (status.isDebug()) {
				LOG.trace("Triggering beforeCommit synchronization");
			}
			TxSynchronizers.triggerBeforeCommit(status.isReadOnly());
		}
	}
	private void triggerAfterCompletion(DefaultTransactionState status, int completionStatus) {
		if (status.isNewSynchronization()) {
			List<TransactionListener> synchronizations = TxSynchronizer.getListeners();
			if (!status.hasTransaction() || status.isNewTransaction()) {
				if (status.isDebug()) {
					LOG.trace("Triggering afterCompletion synchronization");
				}
				// No transaction or new transaction for the current scope ->
				// invoke the afterCompletion callbacks immediately
				invokeAfterCompletion(synchronizations, completionStatus);
			}
			else if (!synchronizations.isEmpty()) {
				// Existing transaction that we participate in, controlled outside
				// of the scope of this Spring transaction manager -> try to register
				// an afterCompletion callback with the existing (JTA) transaction.
				registerAfterCompletionWithExistingTransaction(status.getTransaction(), synchronizations);
			}
		}
	}
	
	protected void registerAfterCompletionWithExistingTransaction(
			Object transaction, List<TransactionListener> synchronizations) throws TransactionException {

		LOG.debug("Cannot register Spring after-completion synchronization with existing transaction - " +
				"processing Spring after-completion callbacks immediately, with outcome status 'unknown'");
		invokeAfterCompletion(synchronizations, TransactionListener.STATUS_UNKNOWN);
	}
	protected final void invokeAfterCompletion(List<TransactionListener> synchronizations, int completionStatus) {
		TxSynchronizers.invokeAfterCompletion(synchronizations, completionStatus);
	}
	
	protected void doSetRollbackOnly(DefaultTransactionState status) throws TransactionException {
		throw new IllegalTransactionStateException(
				"Participating in existing transactions is not supported - when 'isExistingTransaction' " +
				"returns true, appropriate 'doSetRollbackOnly' behavior must be provided");
	}
	protected void prepareForCommit(DefaultTransactionState status) {
	}
	protected abstract void doCommit(DefaultTransactionState status) throws TransactionException;
	protected abstract void doRollback(DefaultTransactionState status) throws TransactionException;
	protected void doCleanupAfterCompletion(Object transaction) {
	}
	/**
	 * 检测transaction是否为一个已存在的事物
	 * @param transaction
	 * @return
	 * @throws TransactionException
	 */
	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		return false;
	}
	
	protected Object doSuspend(Object transaction) throws TransactionException {
		throw new TransactionSuspensionNotSupportedException(
				"Transaction manager [" + getClass().getName() + "] does not support transaction suspension");
	}

	
	protected abstract void doBegin(Object transaction, TransactionInfo definition)
			throws TransactionException;
	
	protected boolean useSavepointForNestedTransaction() {
		return true;
	}
	
	protected abstract Object doGetTransaction() throws TransactionException;
	protected static class SuspendedResourcesHolder {

		private final Object suspendedResources;
		private List<TransactionListener> suspendedSynchronizations;
		private String name;
		private boolean readOnly;
		private TransactionIsolation isolationLevel;
		private boolean wasActive;

		private SuspendedResourcesHolder(Object suspendedResources) {
			this.suspendedResources = suspendedResources;
		}

		private SuspendedResourcesHolder(
				Object suspendedResources, List<TransactionListener> suspendedSynchronizations,
				String name, boolean readOnly, TransactionIsolation isolationLevel, boolean wasActive) {
			this.suspendedResources = suspendedResources;
			this.suspendedSynchronizations = suspendedSynchronizations;
			this.name = name;
			this.readOnly = readOnly;
			this.isolationLevel = isolationLevel;
			this.wasActive = wasActive;
		}
	}

}
