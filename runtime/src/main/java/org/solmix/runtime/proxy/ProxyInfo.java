package org.solmix.runtime.proxy;

import java.io.Serializable;

import org.solmix.commons.util.Assert;


public class ProxyInfo implements Serializable {

	private static final long serialVersionUID = -8409359707199703185L;


	private boolean proxyTargetClass = false;

	private boolean optimize = false;

	boolean opaque = false;

	boolean exposeProxy = false;

	private boolean frozen = false;
	
	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}
	public boolean isProxyTargetClass() {
		return this.proxyTargetClass;
	}
	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}
	
	/**
	 * 是否需要性能优化
	 * @return
	 */
	public boolean isOptimize() {
		return this.optimize;
	}
	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}
	public boolean isOpaque() {
		return this.opaque;
	}
	public void setExposeProxy(boolean exposeProxy) {
		this.exposeProxy = exposeProxy;
	}
	public boolean isExposeProxy() {
		return this.exposeProxy;
	}
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/**
	 * Return whether the config is frozen, and no advice changes can be made.
	 */
	public boolean isFrozen() {
		return this.frozen;
	}


	/**
	 * Copy configuration from the other config object.
	 * @param other object to copy configuration from
	 */
	public void copyFrom(ProxyInfo other) {
		Assert.isNotNull(other, "Other ProxyConfig object must not be null");
		this.proxyTargetClass = other.proxyTargetClass;
		this.optimize = other.optimize;
		this.exposeProxy = other.exposeProxy;
		this.frozen = other.frozen;
		this.opaque = other.opaque;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("proxyTargetClass=").append(this.proxyTargetClass).append("; ");
		sb.append("optimize=").append(this.optimize).append("; ");
		sb.append("opaque=").append(this.opaque).append("; ");
		sb.append("exposeProxy=").append(this.exposeProxy).append("; ");
		sb.append("frozen=").append(this.frozen);
		return sb.toString();
	}
}
