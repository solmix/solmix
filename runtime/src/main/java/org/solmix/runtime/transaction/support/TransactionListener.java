package org.solmix.runtime.transaction.support;

public interface TransactionListener {
	int STATUS_COMMITTED = 0;

	/** Completion status in case of proper rollback */
	int STATUS_ROLLED_BACK = 1;

	/** Completion status in case of heuristic mixed completion or system errors */
	int STATUS_UNKNOWN = 2;
	void suspend();
	void resume();
	void flush();
	void beforeCommit(boolean readOnly);
	void beforeCompletion();
	void afterCommit();
	void afterCompletion(int status);
}
