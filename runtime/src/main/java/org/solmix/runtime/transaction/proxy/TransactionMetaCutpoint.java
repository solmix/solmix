package org.solmix.runtime.transaction.proxy;

import java.lang.reflect.Method;

import org.solmix.runtime.proxy.support.StaticMethodMatcherCutpoint;
import org.springframework.util.ObjectUtils;

public abstract class TransactionMetaCutpoint extends StaticMethodMatcherCutpoint {


	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		TransactionMetaCreater tas = getTransactionMetaCreater();
		return (tas == null || tas.getTransactionMeta(method, targetClass) != null);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TransactionMetaCutpoint)) {
			return false;
		}
		TransactionMetaCutpoint otherPc = (TransactionMetaCutpoint) other;
		return ObjectUtils.nullSafeEquals(getTransactionMetaCreater(), otherPc.getTransactionMetaCreater());
	}

	@Override
	public int hashCode() {
		return TransactionMetaCutpoint.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getTransactionMetaCreater();
	}


	/**
	 * Obtain the underlying TransactionAttributeSource (may be {@code null}).
	 * To be implemented by subclasses.
	 */
	protected abstract TransactionMetaCreater getTransactionMetaCreater();

}
