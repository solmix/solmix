package org.solmix.runtime.proxy;


public interface Aspector {

	Aspect getAspect();

	boolean isPerInstance();
}
