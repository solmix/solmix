package org.solmix.runtime.transaction.config;

import org.solmix.runtime.transaction.TransactionInfo;

public interface TransactionMeta extends TransactionInfo {
	String getQualifier();

	
	boolean rollbackOn(Throwable ex);
}
