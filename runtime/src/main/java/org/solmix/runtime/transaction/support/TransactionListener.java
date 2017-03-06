package org.solmix.runtime.transaction.support;

public interface TransactionListener {

	void suspend();
	void resume();
	void flush();
	void beforeCommit(boolean readOnly);
	void beforeCompletion();
	void afterCommit();
	void afterCompletion(int status);
}
