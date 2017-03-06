package org.solmix.runtime.transaction.proxy;

import java.io.Serializable;

import org.solmix.commons.util.Assert;


public class RollbackRule implements Serializable {
	private final String exceptionName;
	public static final RollbackRule ROLLBACK_ON_RUNTIME_EXCEPTIONS =
			new RollbackRule(RuntimeException.class);
	
	public RollbackRule(Class<?> clazz) {
		Assert.isNotNull(clazz, "'clazz' cannot be null");
		if (!Throwable.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException(
					"Cannot construct rollback rule from [" + clazz.getName() + "]: it's not a Throwable");
		}
		this.exceptionName = clazz.getName();
	}
	
	public RollbackRule(String exceptionName) {
		this.exceptionName = exceptionName;
	}


	/**
	 * Return the pattern for the exception name.
	 */
	public String getExceptionName() {
		return exceptionName;
	}

	
	public int getDepth(Throwable ex) {
		return getDepth(ex.getClass(), 0);
	}


	private int getDepth(Class<?> exceptionClass, int depth) {
		if (exceptionClass.getName().contains(this.exceptionName)) {
			// Found it!
			return depth;
		}
		// If we've gone as far as we can go and haven't found it...
		if (exceptionClass.equals(Throwable.class)) {
			return -1;
		}
		return getDepth(exceptionClass.getSuperclass(), depth + 1);
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RollbackRule)) {
			return false;
		}
		RollbackRule rhs = (RollbackRule) other;
		return this.exceptionName.equals(rhs.exceptionName);
	}

	@Override
	public int hashCode() {
		return this.exceptionName.hashCode();
	}

	@Override
	public String toString() {
		return "RollbackRuleAttribute with pattern [" + this.exceptionName + "]";
	}
}
