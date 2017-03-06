package org.solmix.runtime.transaction.proxy;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.TransactionState;
import org.solmix.runtime.transaction.config.TransactionMeta;


public class TxProxySupport {

	private static final ThreadLocal<TransactionHolder> transactionInfoHolder =
			new ThreadLocal<TransactionHolder>();

	protected static TransactionHolder currentTransactionHolder() throws TransactionException {
		return transactionInfoHolder.get();
	}
	
	public static TransactionState currentTransactionStatus() throws TransactionException {
		TransactionHolder info = currentTransactionHolder();
		if (info == null) {
			throw new TransactionException("No transaction aspect-managed TransactionStatus in scope");
		}
		return currentTransactionHolder().transactionState;
	}


	protected final Log logger = LogFactory.getLog(getClass());

	private String transactionManagerBeanName;

	private TransactionManager transactionManager;

	private TransactionMetaCreater transactionMetaCreater;


	/**
	 * Specify the name of the default transaction manager bean.
	 */
	public void setTransactionManagerBeanName(String transactionManagerBeanName) {
		this.transactionManagerBeanName = transactionManagerBeanName;
	}

	/**
	 * Return the name of the default transaction manager bean.
	 */
	protected final String getTransactionManagerBeanName() {
		return this.transactionManagerBeanName;
	}

	/**
	 * Specify the target transaction manager.
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Return the transaction manager, if specified.
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	
	/*public void setTransactionAttributes(Properties transactionAttributes) {
		NameMatchTransactionMetaCreator tas = new NameMatchTransactionMetaCreator();
		tas.setProperties(transactionAttributes);
		this.transactionMetaCreater = tas;
	}*/

	
	public void setTransactionMetaCreators(TransactionMetaCreater[] transactionAttributeSources) {
		this.transactionMetaCreater = new CompositeTransactionMetaCreater(transactionAttributeSources);
	}

	public void setTransactionMetaCreator(TransactionMetaCreater transactionAttributeSource) {
		this.transactionMetaCreater = transactionAttributeSource;
	}

	/**
	 * Return the transaction attribute source.
	 */
	public TransactionMetaCreater getTransactionMetaCreater() {
		return this.transactionMetaCreater;
	}



	
	protected Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation)
			throws Throwable {

		// If the transaction attribute is null, the method is non-transactional.
		final TransactionMeta txAttr = getTransactionMetaCreater().getTransactionMeta(method, targetClass);
		final TransactionManager tm = determineTransactionManager(txAttr);
		final String joinpointIdentification = methodIdentification(method, targetClass);

//		if (txAttr == null /*|| !(tm instanceof CallbackPreferringPlatformTransactionManager)*/) {
			// Standard transaction demarcation with getTransaction and commit/rollback calls.
			TransactionHolder txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
			Object retVal = null;
			try {
				// This is an around advice: Invoke the next interceptor in the chain.
				// This will normally result in a target object being invoked.
				retVal = invocation.proceedWithInvocation();
			}
			catch (Throwable ex) {
				// target invocation exception
				completeTransactionAfterThrowing(txInfo, ex);
				throw ex;
			}
			finally {
				cleanupTransactionInfo(txInfo);
			}
			commitTransactionAfterReturning(txInfo);
			return retVal;
//		}

		/*else {
			// It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
			try {
				Object result = ((CallbackPreferringPlatformTransactionManager) tm).execute(txAttr,
						new TransactionCallback<Object>() {
							@Override
							public Object doInTransaction(TransactionState status) {
								TransactionHolder txInfo = prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
								try {
									return invocation.proceedWithInvocation();
								}
								catch (Throwable ex) {
									if (txAttr.rollbackOn(ex)) {
										// A RuntimeException: will lead to a rollback.
										if (ex instanceof RuntimeException) {
											throw (RuntimeException) ex;
										}
										else {
											throw new ThrowableHolderException(ex);
										}
									}
									else {
										// A normal return value: will lead to a commit.
										return new ThrowableHolder(ex);
									}
								}
								finally {
									cleanupTransactionInfo(txInfo);
								}
							}
						});

				// Check result: It might indicate a Throwable to rethrow.
				if (result instanceof ThrowableHolder) {
					throw ((ThrowableHolder) result).getThrowable();
				}
				else {
					return result;
				}
			}
			catch (ThrowableHolderException ex) {
				throw ex.getCause();
			}
		}*/
	}

	/**
	 * Determine the specific transaction manager to use for the given transaction.
	 */
	protected TransactionManager determineTransactionManager(TransactionMeta txAttr) {
		if (this.transactionManager != null ) {
			return this.transactionManager;
		}
		return null;
//		String qualifier = txAttr.getQualifier();
//		if (StringUtils.hasLength(qualifier)) {
//			return BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, TransactionManager.class, qualifier);
//		}
//		else if (this.transactionManagerBeanName != null) {
//			return this.beanFactory.getBean(this.transactionManagerBeanName, TransactionManager.class);
//		}
//		else {
//			return this.beanFactory.getBean(TransactionManager.class);
//		}
	}

	protected String methodIdentification(Method method, Class<?> targetClass) {
		String simpleMethodId = methodIdentification(method);
		if (simpleMethodId != null) {
			return simpleMethodId;
		}
		return (targetClass != null ? targetClass : method.getDeclaringClass()).getName() + "." + method.getName();
	}


	@Deprecated
	protected String methodIdentification(Method method) {
		return null;
	}

	
	@Deprecated
	protected TransactionHolder createTransactionIfNecessary(Method method, Class<?> targetClass) {
		// If the transaction attribute is null, the method is non-transactional.
		TransactionMeta txAttr = getTransactionMetaCreater().getTransactionMeta(method, targetClass);
		TransactionManager tm = determineTransactionManager(txAttr);
		return createTransactionIfNecessary(tm, txAttr, methodIdentification(method, targetClass));
	}

	@SuppressWarnings("serial")
	protected TransactionHolder createTransactionIfNecessary(
			TransactionManager tm, TransactionMeta txAttr, final String joinpointIdentification) {

		// If no name specified, apply method identification as transaction name.
		if (txAttr != null && txAttr.getName() == null) {
			txAttr = new DelegatingTransactionMeta(txAttr) {
				@Override
				public String getName() {
					return joinpointIdentification;
				}
			};
		}

		TransactionState status = null;
		if (txAttr != null) {
			if (tm != null) {
				status = tm.getTransaction(txAttr);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
							"] because no transaction manager has been configured");
				}
			}
		}
		return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
	}

	protected TransactionHolder prepareTransactionInfo(TransactionManager tm,
			TransactionMeta txAttr, String joinpointIdentification, TransactionState status) {

		TransactionHolder txInfo = new TransactionHolder(tm, txAttr, joinpointIdentification);
		if (txAttr != null) {
			// We need a transaction for this method
			if (logger.isTraceEnabled()) {
				logger.trace("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]");
			}
			// The transaction manager will flag an error if an incompatible tx already exists
			txInfo.newTransactionState(status);
		}
		else {
			// The TransactionInfo.hasTransaction() method will return
			// false. We created it only to preserve the integrity of
			// the ThreadLocal stack maintained in this class.
			if (logger.isTraceEnabled())
				logger.trace("Don't need to create transaction for [" + joinpointIdentification +
						"]: This method isn't transactional.");
		}

		// We always bind the TransactionInfo to the thread, even if we didn't create
		// a new transaction here. This guarantees that the TransactionInfo stack
		// will be managed correctly even if no transaction was created by this aspect.
		txInfo.bindToThread();
		return txInfo;
	}

	/**
	 * Execute after successful completion of call, but not after an exception was handled.
	 * Do nothing if we didn't create a transaction.
	 * @param txInfo information about the current transaction
	 */
	protected void commitTransactionAfterReturning(TransactionHolder txInfo) {
		if (txInfo != null && txInfo.hasTransaction()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
			}
			txInfo.getTransactionManager().commit(txInfo.getTransactionState());
		}
	}

	/**
	 * Handle a throwable, completing the transaction.
	 * We may commit or roll back, depending on the configuration.
	 * @param txInfo information about the current transaction
	 * @param ex throwable encountered
	 */
	protected void completeTransactionAfterThrowing(TransactionHolder txInfo, Throwable ex) {
		if (txInfo != null && txInfo.hasTransaction()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() +
						"] after exception: " + ex);
			}
			if (txInfo.transactionMeta.rollbackOn(ex)) {
				try {
					txInfo.getTransactionManager().rollback(txInfo.getTransactionState());
				}
				catch (RuntimeException ex2) {
					logger.error("Application exception overridden by rollback exception", ex);
					throw ex2;
				}
				catch (Error err) {
					logger.error("Application exception overridden by rollback error", ex);
					throw err;
				}
			}
			else {
				// We don't roll back on this exception.
				// Will still roll back if TransactionStatus.isRollbackOnly() is true.
				try {
					txInfo.getTransactionManager().commit(txInfo.getTransactionState());
				}
				catch (RuntimeException ex2) {
					logger.error("Application exception overridden by commit exception", ex);
					throw ex2;
				}
				catch (Error err) {
					logger.error("Application exception overridden by commit error", ex);
					throw err;
				}
			}
		}
	}

	/**
	 * Reset the TransactionInfo ThreadLocal.
	 * <p>Call this in all cases: exception or normal return!
	 * @param txInfo information about the current transaction (may be {@code null})
	 */
	protected void cleanupTransactionInfo(TransactionHolder txInfo) {
		if (txInfo != null) {
			txInfo.restoreThreadLocalStatus();
		}
	}
	
	
	protected final class TransactionHolder {

		private final TransactionManager transactionManager;

		private final TransactionMeta transactionMeta;

		private final String joinpointIdentification;

		private TransactionState transactionState;

		private TransactionHolder oldTransactionInfo;

		public TransactionHolder(TransactionManager transactionManager,
				TransactionMeta transactionMeta, String joinpointIdentification) {
			this.transactionManager = transactionManager;
			this.transactionMeta = transactionMeta;
			this.joinpointIdentification = joinpointIdentification;
		}

		public TransactionManager getTransactionManager() {
			return this.transactionManager;
		}

		public TransactionMeta getTransactionMeta() {
			return this.transactionMeta;
		}

		/**
		 * Return a String representation of this joinpoint (usually a Method call)
		 * for use in logging.
		 */
		public String getJoinpointIdentification() {
			return this.joinpointIdentification;
		}

		public void newTransactionState(TransactionState status) {
			this.transactionState = status;
		}

		public TransactionState getTransactionState() {
			return this.transactionState;
		}

		/**
		 * Return whether a transaction was created by this aspect,
		 * or whether we just have a placeholder to keep ThreadLocal stack integrity.
		 */
		public boolean hasTransaction() {
			return (this.transactionState != null);
		}

		private void bindToThread() {
			// Expose current TransactionStatus, preserving any existing TransactionStatus
			// for restoration after this transaction is complete.
			this.oldTransactionInfo = transactionInfoHolder.get();
			transactionInfoHolder.set(this);
		}

		private void restoreThreadLocalStatus() {
			// Use stack to restore old transaction TransactionInfo.
			// Will be null if none was set.
			transactionInfoHolder.set(this.oldTransactionInfo);
		}

		@Override
		public String toString() {
			return this.transactionMeta.toString();
		}
	}


	/**
	 * Simple callback interface for proceeding with the target invocation.
	 * Concrete interceptors/aspects adapt this to their invocation mechanism.
	 */
	protected interface InvocationCallback {

		Object proceedWithInvocation() throws Throwable;
	}


	/**
	 * Internal holder class for a Throwable, used as a return value
	 * from a TransactionCallback (to be subsequently unwrapped again).
	 */
	private static class ThrowableHolder {

		private final Throwable throwable;

		public ThrowableHolder(Throwable throwable) {
			this.throwable = throwable;
		}

		public final Throwable getThrowable() {
			return this.throwable;
		}
	}


	/**
	 * Internal holder class for a Throwable, used as a RuntimeException to be
	 * thrown from a TransactionCallback (and subsequently unwrapped again).
	 */
	@SuppressWarnings("serial")
	private static class ThrowableHolderException extends RuntimeException {

		public ThrowableHolderException(Throwable throwable) {
			super(throwable);
		}

		@Override
		public String toString() {
			return getCause().toString();
		}
	}

}
