package org.solmix.runtime.proxy.target;

import java.io.Serializable;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ObjectUtils;


@SuppressWarnings("serial")
public class SimpleTargetSource implements TargetSource, Serializable {
	private final Object target;


	/**
	 * Create a new SingletonTargetSource for the given target.
	 * @param target the target object
	 */
	public SimpleTargetSource(Object target) {
		Assert.isNotNull(target, "Target object must not be null");
		this.target = target;
	}


	@Override
	public Class<?> getTargetClass() {
		return this.target.getClass();
	}

	@Override
	public Object getTarget() {
		return this.target;
	}

	@Override
	public boolean isStatic() {
		return true;
	}
	@Override
	public void releaseTarget(Object target) {
		// nothing to do
	}



	/**
	 * Two invoker interceptors are equal if they have the same target or if the
	 * targets or the targets are equal.
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SimpleTargetSource)) {
			return false;
		}
		SimpleTargetSource otherTargetSource = (SimpleTargetSource) other;
		return this.target.equals(otherTargetSource.target);
	}

	/**
	 * SingletonTargetSource uses the hash code of the target object.
	 */
	@Override
	public int hashCode() {
		return this.target.hashCode();
	}

	@Override
	public String toString() {
		return "SingletonTargetSource for target object [" + ObjectUtils.identityToString(this.target) + "]";
	}

}
