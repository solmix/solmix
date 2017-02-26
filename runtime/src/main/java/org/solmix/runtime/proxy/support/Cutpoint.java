package org.solmix.runtime.proxy.support;

public interface Cutpoint {
	ClassFilter getClassFilter();
	MethodMatcher getMethodMatcher();

	Cutpoint TRUE = TrueCutpoint.INSTANCE;
}
