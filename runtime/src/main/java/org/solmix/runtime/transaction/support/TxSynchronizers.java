package org.solmix.runtime.transaction.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;

public abstract class TxSynchronizers {

	private static final Logger LOG = LoggerFactory
			.getLogger(TxSynchronizers.class);

	public static void triggerBeforeCompletion() {
		for (TransactionListener synchronization : TxSynchronizer
				.getListeners()) {
			try {
				synchronization.beforeCompletion();
			} catch (Throwable tsex) {
				LOG.error(
						"TransactionListener.beforeCompletion threw exception",
						tsex);
			}
		}
	}

	public static void triggerBeforeCommit(boolean readOnly) {
		for (TransactionListener synchronization : TxSynchronizer
				.getListeners()) {
			synchronization.beforeCommit(readOnly);
		}
	}

	public static void invokeAfterCompletion(
			List<TransactionListener> synchronizations, int completionState) {
		if (synchronizations != null) {
			for (TransactionListener synchronization : synchronizations) {
				try {
					synchronization.afterCompletion(completionState);
				} catch (Throwable tsex) {
					LOG.error("TransactionListener.afterCompletion threw exception",
							tsex);
				}
			}
		}
	}
	public static void triggerAfterCommit() {
		invokeAfterCommit(TxSynchronizer.getListeners());
	}

	public static void invokeAfterCommit(List<TransactionListener> synchronizations) {
		if (synchronizations != null) {
			for (TransactionListener synchronization : synchronizations) {
				synchronization.afterCommit();
			}
		}
	}

	static Object unwrapResourceIfNecessary(Object resource) {
		Assert.isNotNull(resource, "Resource must not be null");
		Object resourceRef = resource;
		
		return resourceRef;
	}

}
