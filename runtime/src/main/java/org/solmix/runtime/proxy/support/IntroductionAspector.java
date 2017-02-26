package org.solmix.runtime.proxy.support;


public interface IntroductionAspector {
	ClassFilter getClassFilter();
	void validateInterfaces() throws IllegalArgumentException;
	Class<?>[] getInterfaces();
}
