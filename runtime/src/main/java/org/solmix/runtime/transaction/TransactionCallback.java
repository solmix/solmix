package org.solmix.runtime.transaction;


public interface TransactionCallback <T> {

	T doInTransaction(TransactionState status);

}
