package org.solmix.runtime.transaction.proxy;

import java.lang.reflect.Method;

import org.solmix.runtime.transaction.config.TransactionMeta;
import org.springframework.util.Assert;

public class CompositeTransactionMetaCreater implements TransactionMetaCreater {


	

	private final TransactionMetaCreater[] transactionAttributeSources;


	/**
	 * Create a new CompositeTransactionAttributeSource for the given sources.
	 * @param transactionAttributeSources the TransactionAttributeSource instances to combine
	 */
	public CompositeTransactionMetaCreater(TransactionMetaCreater[] transactionAttributeSources) {
		Assert.notNull(transactionAttributeSources, "TransactionAttributeSource array must not be null");
		this.transactionAttributeSources = transactionAttributeSources;
	}

	/**
	 * Return the TransactionAttributeSource instances that this
	 * CompositeTransactionAttributeSource combines.
	 */
	public final TransactionMetaCreater[] getTransactionAttributeSources() {
		return this.transactionAttributeSources;
	}


	@Override
	public TransactionMeta getTransactionMeta(Method method, Class<?> targetClass) {
		for (TransactionMetaCreater tas : this.transactionAttributeSources) {
			TransactionMeta ta = tas.getTransactionMeta(method, targetClass);
			if (ta != null) {
				return ta;
			}
		}
		return null;
	}
}
