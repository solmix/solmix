package org.solmix.runtime.transaction.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.transaction.ResourceBinder;
import org.solmix.runtime.transaction.TransactionIsolation;

public abstract class TxSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(TxSynchronizer.class);
	private static final ThreadLocal<Map<Object, Object>> resources = new ThreadLocal<Map<Object, Object>>();

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

	public static Object getResource(Object key) {
		Object actualKey = TxSynchronizers.unwrapResourceIfNecessary(key);
		Object value = doGetResource(actualKey);
		if (value != null && LOG.isTraceEnabled()) {
			LOG.trace("Retrieved value [" + value + "] for key [" + actualKey + "] bound to thread [" +
					Thread.currentThread().getName() + "]");
		}
		return value;
	}

	private static Object doGetResource(Object actualKey) {
		Map<Object, Object> map = resources.get();
		if (map == null) {
			return null;
		}
		Object value = map.get(actualKey);
		// Transparently remove ResourceHolder that was marked as void...
		if (value instanceof ResourceBinder && ((ResourceBinder) value).isVoid()) {
			map.remove(actualKey);
			// Remove entire ThreadLocal if empty...
			if (map.isEmpty()) {
				resources.remove();
			}
			value = null;
		}
		return value;
	}

	public static void bindResource(Object key,Object value) {
		Object actualKey = TxSynchronizers.unwrapResourceIfNecessary(key);
		Assert.isNotNull(value, "Value must not be null");
		Map<Object, Object> map = resources.get();
		// set ThreadLocal Map if none found
		if (map == null) {
			map = new HashMap<Object, Object>();
			resources.set(map);
		}
		Object oldValue = map.put(actualKey, value);
		// Transparently suppress a ResourceHolder that was marked as void...
		if (oldValue instanceof ResourceBinder && ((ResourceBinder) oldValue).isVoid()) {
			oldValue = null;
		}
		if (oldValue != null) {
			throw new IllegalStateException("Already value [" + oldValue + "] for key [" +
					actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Bound value [" + value + "] for key [" + actualKey + "] to thread [" +
					Thread.currentThread().getName() + "]");
		}
		
	}

	public static Object unbindResource(Object key) throws IllegalStateException {
		Object actualKey = TxSynchronizers.unwrapResourceIfNecessary(key);
		Object value = doUnbindResource(actualKey);
		if (value == null) {
			throw new IllegalStateException(
					"No value for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		return value;
	}
		
	private static Object doUnbindResource(Object actualKey) {
		Map<Object, Object> map = resources.get();
		if (map == null) {
			return null;
		}
		Object value = map.remove(actualKey);
		// Remove entire ThreadLocal if empty...
		if (map.isEmpty()) {
			resources.remove();
		}
		// Transparently suppress a ResourceHolder that was marked as void...
		if (value instanceof ResourceBinder && ((ResourceBinder) value).isVoid()) {
			value = null;
		}
		if (value != null && LOG.isTraceEnabled()) {
			LOG.trace("Removed value [" + value + "] for key [" + actualKey + "] from thread [" +
					Thread.currentThread().getName() + "]");
		}
		return value;
	}

	public static Object unbindResourceIfPossible(Object key) {
	Object actualKey = TxSynchronizers.unwrapResourceIfNecessary(key);
	return doUnbindResource(actualKey);
}
}
