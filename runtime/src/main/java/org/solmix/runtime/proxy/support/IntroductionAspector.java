package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspector;


public interface IntroductionAspector extends Aspector,IntroductionAspect {
	ClassFilter getClassFilter();
	void validateInterfaces() throws IllegalArgumentException;
	@Override
	Class<?>[] getInterfaces();
}
