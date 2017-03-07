package org.solmix.runtime.transaction;

public interface TransactionResourceProvider extends TransactionManager {
	Object getResourceFactory();
}
