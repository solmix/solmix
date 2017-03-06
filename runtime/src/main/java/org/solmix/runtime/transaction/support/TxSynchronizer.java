package org.solmix.runtime.transaction.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.transaction.TransactionIsolation;

public abstract class TxSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(TxSynchronizer.class);
	private static final ThreadLocal<Set<TransactionListener>> synchronizations = new ThreadLocal<Set<TransactionListener>>();
	private static final ThreadLocal<Boolean> actualTransactionActive = new ThreadLocal<Boolean>();

	private static final ThreadLocal<TransactionIsolation> currentTransactionIsolationLevel = new ThreadLocal<TransactionIsolation>();

	private static final ThreadLocal<Boolean> currentTransactionReadOnly = new ThreadLocal<Boolean>();
	private static final ThreadLocal<String> currentTransactionName = new ThreadLocal<String>();

	public static List<TransactionListener> getListeners() {
		Set<TransactionListener> synchs = synchronizations.get();
		if (synchs == null) {
			throw new IllegalStateException(
					"Transaction synchronization is not active");
		}
		// Return unmodifiable snapshot, to avoid
		// ConcurrentModificationExceptions
		// while iterating and invoking synchronization callbacks that in turn
		// might register further synchronizations.
		if (synchs.isEmpty()) {
			return Collections.emptyList();
		} else {
			// Sort lazily here, not in registerSynchronization.
			List<TransactionListener> sortedSynchs = new ArrayList<TransactionListener>(
					synchs);
			return Collections.unmodifiableList(sortedSynchs);
		}
	}

	public static void clear() {
		clearSynchronization();
		setCurrentTransactionName(null);
		setCurrentTransactionReadOnly(false);
		setCurrentTransactionIsolationLevel(null);
		setActualTransactionActive(false);
	}

	public static void setActualTransactionActive(boolean active) {
		actualTransactionActive.set(active ? Boolean.TRUE : null);
	}

	public static void setCurrentTransactionIsolationLevel(
			TransactionIsolation isolationLevel) {
		currentTransactionIsolationLevel.set(isolationLevel);
	}

	public static void setCurrentTransactionReadOnly(boolean readOnly) {
		currentTransactionReadOnly.set(readOnly ? Boolean.TRUE : null);
	}

	public static void setCurrentTransactionName(String name) {
		currentTransactionName.set(name);
	}
	public static boolean isSynchronizationActive() {
		return (synchronizations.get() != null);
	}
	public static void clearSynchronization() throws IllegalStateException {
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
		}
		LOG.trace("Clearing transaction synchronization");
		synchronizations.remove();
	}

	public static void registerSynchronization(TransactionListener synchronization)
			throws IllegalStateException {

		Assert.isNotNull(synchronization, "TransactionListener must not be null");
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		synchronizations.get().add(synchronization);
	}

	public static void initSynchronization() {
		if (isSynchronizationActive()) {
			throw new IllegalStateException("Cannot activate transaction synchronization - already active");
		}
		LOG.trace("Initializing transaction synchronization");
		synchronizations.set(new LinkedHashSet<TransactionListener>());
	}

	public static TransactionIsolation getCurrentTransactionIsolation() {
		return currentTransactionIsolationLevel.get();
	}

	public static String getCurrentTransactionName() {
		return currentTransactionName.get();
	}

	public static boolean isCurrentTransactionReadOnly() {
		return (currentTransactionReadOnly.get() != null);
	}

		public static boolean isActualTransactionActive() {
			return (actualTransactionActive.get() != null);
		}


}
