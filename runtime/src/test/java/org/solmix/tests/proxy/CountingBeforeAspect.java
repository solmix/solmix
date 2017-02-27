package org.solmix.tests.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.support.InvokeBeforeAspect;

public class CountingBeforeAspect implements Aspect,InvokeBeforeAspect {
	private HashMap<String, Integer> map = new HashMap<String, Integer>();

	private int allCount;

	protected void count(Method m) {
		count(m.getName());
	}

	protected void count(String methodName) {
		Integer i = map.get(methodName);
		i = (i != null) ? new Integer(i.intValue() + 1) : new Integer(1);
		map.put(methodName, i);
		++allCount;
	}

	public int getCalls(String methodName) {
		Integer i = map.get(methodName);
		return (i != null ? i.intValue() : 0);
	}

	public int getCalls() {
		return allCount;
	}

	@Override
	public boolean equals(Object other) {
		return (other != null && other.getClass() == this.getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public void before(Method m, Object[] args, Object target)
			throws Throwable {
		count(m);
		
	}
}
