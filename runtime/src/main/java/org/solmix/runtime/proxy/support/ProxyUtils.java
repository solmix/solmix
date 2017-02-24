package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.solmix.commons.util.Reflection;
import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.RuntimeProxy;

public class ProxyUtils {

	
	public static Class<?>[] completeProxiedInterfaces(ProxyAspectSupport advised) {
		Class<?>[] specifiedInterfaces = advised.getProxiedInterfaces();
		if (specifiedInterfaces.length == 0) {
			// No user-specified interfaces: check whether target class is an interface.
			Class<?> targetClass = advised.getTargetClass();
			if (targetClass != null && targetClass.isInterface()) {
				specifiedInterfaces = new Class<?>[] {targetClass};
			}
		}
		boolean addSpringProxy = !advised.isInterfaceProxied(RuntimeProxy.class);
		boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Aspect.class);
		int nonUserIfcCount = 0;
		if (addSpringProxy) {
			nonUserIfcCount++;
		}
		if (addAdvised) {
			nonUserIfcCount++;
		}
		Class<?>[] proxiedInterfaces = new Class<?>[specifiedInterfaces.length + nonUserIfcCount];
		System.arraycopy(specifiedInterfaces, 0, proxiedInterfaces, 0, specifiedInterfaces.length);
		if (addSpringProxy) {
			proxiedInterfaces[specifiedInterfaces.length] = RuntimeProxy.class;
		}
		if (addAdvised) {
			proxiedInterfaces[proxiedInterfaces.length - 1] = Aspect.class;
		}
		return proxiedInterfaces;
	}
	
	public static boolean isEqualsMethod(Method method) {
		return Reflection.isEqualsMethod(method);
	}

	/**
	 * Determine whether the given method is a "hashCode" method.
	 * @see java.lang.Object#hashCode
	 */
	public static boolean isHashCodeMethod(Method method) {
		return Reflection.isHashCodeMethod(method);
	}

	/**
	 * Determine whether the given method is a "toString" method.
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(Method method) {
		return Reflection.isToStringMethod(method);
	}

	public static boolean equalsInProxy(ProxyAspectSupport a, ProxyAspectSupport b) {
		return (a == b ||
				(equalsProxiedInterfaces(a, b) && equalsAdvisors(a, b) && a.getTargetSource().equals(b.getTargetSource())));
	}

	public static boolean equalsProxiedInterfaces(ProxyAspectSupport a, ProxyAspectSupport b) {
		return Arrays.equals(a.getProxiedInterfaces(), b.getProxiedInterfaces());
	}

	
	public static boolean equalsAdvisors(ProxyAspectSupport a, ProxyAspectSupport b) {
		return Arrays.equals(a.getAspects(), b.getAspects());
	}

	public static Object invokeJoinpointUsingReflection(Object target,
			Method method, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
