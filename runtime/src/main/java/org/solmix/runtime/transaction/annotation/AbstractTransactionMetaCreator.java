package org.solmix.runtime.transaction.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.runtime.proxy.support.ProxyUtils;
import org.solmix.runtime.transaction.config.TransactionMeta;
import org.solmix.runtime.transaction.proxy.DefaultTransactionMeta;
import org.solmix.runtime.transaction.proxy.TransactionMetaCreater;

public abstract class AbstractTransactionMetaCreator implements TransactionMetaCreater{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractTransactionMetaCreator.class);
	private final static TransactionMeta NULL_TRANSACTION_ATTRIBUTE = new DefaultTransactionMeta();
	final Map<Object, TransactionMeta> attributeCache = new ConcurrentHashMap<Object, TransactionMeta>(1024);

	@Override
	public TransactionMeta getTransactionMeta(Method method, Class<?> targetClass) {
		// First, see if we have a cached value.
		Object cacheKey = getCacheKey(method, targetClass);
		Object cached = this.attributeCache.get(cacheKey);
		if (cached != null) {
			// Value will either be canonical value indicating there is no transaction attribute,
			// or an actual transaction attribute.
			if (cached == NULL_TRANSACTION_ATTRIBUTE) {
				return null;
			}
			else {
				return (TransactionMeta) cached;
			}
		}
		else {
			// We need to work it out.
			TransactionMeta txAtt = computeTransactionAttribute(method, targetClass);
			// Put it in the cache.
			if (txAtt == null) {
				this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
			}
			else {
				if (LOG.isDebugEnabled()) {
					Class<?> classToLog = (targetClass != null ? targetClass : method.getDeclaringClass());
					LOG.debug("Adding transactional method '" + classToLog.getSimpleName() + "." +
							method.getName() + "' with attribute: " + txAtt);
				}
				this.attributeCache.put(cacheKey, txAtt);
			}
			return txAtt;
		}
	}
	
	private TransactionMeta computeTransactionAttribute(Method method, Class<?> targetClass) {
		// Don't allow no-public methods as required.
		if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			return null;
		}

		// Ignore CGLIB subclasses - introspect the actual user class.
		Class<?> userClass = ClassUtils.getUserClass(targetClass);
		// The method may be on an interface, but we need attributes from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
		// If we are dealing with method with generic parameters, find the original method.
		specificMethod = ProxyUtils.findBridgedMethod(specificMethod);

		// First try is the method in the target class.
		TransactionMeta txAtt = findTransactionAttribute(specificMethod);
		if (txAtt != null) {
			return txAtt;
		}

		// Second try is the transaction attribute on the target class.
		txAtt = findTransactionAttribute(specificMethod.getDeclaringClass());
		if (txAtt != null) {
			return txAtt;
		}

		if (specificMethod != method) {
			// Fallback is to look at the original method.
			txAtt = findTransactionAttribute(method);
			if (txAtt != null) {
				return txAtt;
			}
			// Last fallback is the class of the original method.
			return findTransactionAttribute(method.getDeclaringClass());
		}
		return null;
	}
	protected abstract TransactionMeta findTransactionAttribute(Method method);

	/**
	 * Subclasses need to implement this to return the transaction attribute
	 * for the given class, if any.
	 * @param clazz the class to retrieve the attribute for
	 * @return all transaction attribute associated with this class
	 * (or {@code null} if none)
	 */
	protected abstract TransactionMeta findTransactionAttribute(Class<?> clazz);


	/**
	 * Should only public methods be allowed to have transactional semantics?
	 * <p>The default implementation returns {@code false}.
	 */
	protected boolean allowPublicMethodsOnly() {
		return false;
	}
	
	protected Object getCacheKey(Method method, Class<?> targetClass) {
		return new DefaultCacheKey(method, targetClass);
	}
	
	private static class DefaultCacheKey {

		private final Method method;

		private final Class<?> targetClass;

		public DefaultCacheKey(Method method, Class<?> targetClass) {
			this.method = method;
			this.targetClass = targetClass;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof DefaultCacheKey)) {
				return false;
			}
			DefaultCacheKey otherKey = (DefaultCacheKey) other;
			return (this.method.equals(otherKey.method) &&
					ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
		}

		@Override
		public int hashCode() {
			return this.method.hashCode();
		}
	}

}
