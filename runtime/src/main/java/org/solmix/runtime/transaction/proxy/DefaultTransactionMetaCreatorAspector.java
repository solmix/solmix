package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.support.ClassFilter;
import org.solmix.runtime.proxy.support.Cutpoint;
import org.solmix.runtime.proxy.support.CutpointAspector;

public class DefaultTransactionMetaCreatorAspector implements CutpointAspector{
	private TxInterceptor transactionInterceptor;

	
	private final TransactionMetaCutpoint pointcut = new TransactionMetaCutpoint() {
		@Override
		protected TransactionMetaCreater getTransactionMetaCreater() {
			return (transactionInterceptor != null ? transactionInterceptor.getTransactionMetaCreater() : null);
		}
	};
	
	public DefaultTransactionMetaCreatorAspector() {
	}

	/**
	 * Create a new TransactionAttributeSourceAdvisor.
	 * @param interceptor the transaction interceptor to use for this advisor
	 */
	public DefaultTransactionMetaCreatorAspector(TxInterceptor interceptor) {
		setTransactionInterceptor(interceptor);
	}


	/**
	 * Set the transaction interceptor to use for this advisor.
	 */
	public void setTransactionInterceptor(TxInterceptor interceptor) {
		this.transactionInterceptor = interceptor;
	}

	/**
	 * Set the {@link ClassFilter} to use for this pointcut.
	 * Default is {@link ClassFilter#TRUE}.
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	@Override
	public Aspect getAspect() {
		return transactionInterceptor;
	}

	@Override
	public boolean isPerInstance() {
		return true;
	}

	@Override
	public Cutpoint getCutpoint() {
		return pointcut;
	}

}
