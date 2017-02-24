package org.solmix.runtime.proxy.support;


public interface ProxyAspectSupportListener {

	/**
	 * Invoked when the first proxy is created.
	 * @param advised the AdvisedSupport object
	 */
	void created(ProxyAspectSupport advised);

	/**
	 * Invoked when advice is changed after a proxy is created.
	 * @param advised the AdvisedSupport object
	 */
	void changed(ProxyAspectSupport advised);
}
