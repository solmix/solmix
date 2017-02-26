package org.solmix.runtime.proxy.support;

import java.io.Serializable;


public class TrueCutpoint implements Cutpoint, Serializable {

	private static final long serialVersionUID = 7197657410831534412L;
	public static final TrueCutpoint INSTANCE = new TrueCutpoint();
	@Override
	public ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return MethodMatcher.TRUE;
	}

	private Object readResolve() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "Pointcut.TRUE";
	}

}
