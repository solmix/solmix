package org.solmix.runtime.proxy.support;

import java.io.Serializable;

public class TrueClassFilter implements ClassFilter, Serializable {

	private static final long serialVersionUID = -4276496857208618997L;
	public static final TrueClassFilter INSTANCE = new TrueClassFilter();

	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}

	private Object readResolve() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "ClassFilter.TRUE";
	}

}
