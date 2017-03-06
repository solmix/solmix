package org.solmix.runtime.transaction;

import org.springframework.transaction.TransactionStatus;

public interface TransactionCallback <T> {

	T doInTransaction(TransactionStatus status);

}
