package org.solmix.runtime.proxy.support;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.Reflection;
import org.solmix.runtime.proxy.Aspected;
import org.solmix.runtime.proxy.ProxyInvocationException;
import org.solmix.runtime.proxy.RuntimeProxy;
import org.solmix.runtime.proxy.TargetClassAware;
import org.solmix.runtime.proxy.target.SimpleTargetSource;
import org.solmix.runtime.proxy.target.TargetSource;

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
		boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Aspected.class);
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
			proxiedInterfaces[proxiedInterfaces.length - 1] = Aspected.class;
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
		return Arrays.equals(a.getAspectors(), b.getAspectors());
	}

	public static Object invokeJoinpointUsingReflection(Object target,
			Method method, Object[] args)throws Throwable {
		try {
			Reflection.setAccessible(method);
			return method.invoke(target, args);
		}
		catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
		catch (IllegalAccessException ex) {
			throw new ProxyInvocationException("Could not access method [" + method + "]", ex);
		}
	}

	public static Method findBridgedMethod(Method bridgeMethod) {
		if (bridgeMethod == null || !bridgeMethod.isBridge()) {
			return bridgeMethod;
		}
		//如果是桥接方法
		// Gather all methods with matching name and parameter size.
		List<Method> candidateMethods = new ArrayList<Method>();
		Method[] methods = Reflection.getDeclaredMethods(bridgeMethod.getDeclaringClass());
		for (Method candidateMethod : methods) {
			if (isBridgedCandidateFor(candidateMethod, bridgeMethod)) {
				candidateMethods.add(candidateMethod);
			}
		}
		//只有一个就用它
		if (candidateMethods.size() == 1) {
			return candidateMethods.get(0);
		}
		// Search for candidate match.
		Method bridgedMethod = searchCandidates(candidateMethods, bridgeMethod);
		if (bridgedMethod != null) {
			// Bridged method found...
			return bridgedMethod;
		}
		else {
			// 标记为桥接方法却找不到具体的桥接方法
			return bridgeMethod;
		}
	}
	/** 方法名相同，参数数量相同*/
	private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
		return (!candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) &&
				candidateMethod.getName().equals(bridgeMethod.getName()) &&
				candidateMethod.getParameterTypes().length == bridgeMethod.getParameterTypes().length);
	}
	
	private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
		if (candidateMethods.isEmpty()) {
			return null;
		}
		Method previousMethod = null;
		boolean sameSig = true;
		for (Method candidateMethod : candidateMethods) {
			if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
				return candidateMethod;
			}
			else if (previousMethod != null) {
				sameSig = sameSig &&Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
			}
			previousMethod = candidateMethod;
		}
		return (sameSig ? candidateMethods.get(0) : null);
	}
	
	static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
		if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
			return true;
		}
		Method method = findGenericDeclaration(bridgeMethod);
		return (method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass));
	}
	private static boolean isResolvedTypeMatch(
			Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
		Type[] genericParameters = genericMethod.getGenericParameterTypes();
		Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
		//没有参数的不存在桥接方法
		if (genericParameters.length != candidateParameters.length) {
			return false;
		}
		for (int i = 0; i < candidateParameters.length; i++) {
			Class<?> candidateParameter = candidateParameters[i];
			if (candidateParameter.isArray()) {
				// An array type: compare the component type.
				if (!candidateParameter.getComponentType().equals(getComponentresolveType(declaringClass,Object.class))) {
					return false;
				}
			}
			// A non-array type: compare the type itself.
			if (!candidateParameter.equals(resolveType(declaringClass,Object.class))) {
				return false;
			}
		}
		return true;
	}
	
	public static Class<?> resolveType(Type type, Class<?> fallback){
		
		Class<?> resolved= resolveType(type);
		if(resolved==null){
			return fallback;
		}else{
			return resolved;
		}
	}
	
	private static Class<?> resolveType(Type type) {
		if (type instanceof Class<?> || type == null) {
			return (Class<?>) type;
		}
		if (type instanceof GenericArrayType) {
			Class<?> resolvedComponent = getComponentresolveType(type);
			return (resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null);
		}
		return getresolveType(type);
	}

	private static Class<?> getresolveType(Type type) {
		if (type instanceof ParameterizedType) {
			return resolveType(((ParameterizedType) type).getRawType());
		}
		if (type instanceof WildcardType) {
			Type resolved = resolveBounds(((WildcardType) type).getUpperBounds());
			if (resolved == null) {
				resolved = resolveBounds(((WildcardType) type).getLowerBounds());
			}
			return resolveType(resolved);
		}
		if (type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) type;
			
			return resolveType(resolveBounds(variable.getBounds()));
		}
		return null;
	}

	private static Type resolveBounds(Type[] bounds) {
		if (ObjectUtils.isEmptyObject(bounds) || Object.class.equals(bounds[0])) {
			return null;
		}
		return bounds[0];
	}
	private static Class<?> getComponentresolveType(Type type, Class<?> fallback) {
		Class<?> resolved = getComponentresolveType(type);
		if(resolved==null){
			return fallback;
		}else{
			return resolved;
		}
	}
	private static Class<?> getComponentresolveType(Type type) {
		if(type instanceof Class){
			Class<?> componentType = ((Class<?>)type).getComponentType();
			return resolveType(componentType);
		}
		if (type instanceof GenericArrayType) {
			return resolveType(((GenericArrayType) type).getGenericComponentType());
		}
		return getComponentresolveType(getresolveType(type));
	}

	/**通过继承关系找到非桥接方法*/
	private static Method findGenericDeclaration(Method bridgeMethod) {
		// Search parent types for method that has same signature as bridge.
		Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
		while (superclass != null && !Object.class.equals(superclass)) {
			Method method = searchForMatch(superclass, bridgeMethod);
			if (method != null && !method.isBridge()) {
				return method;
			}
			superclass = superclass.getSuperclass();
		}

		// Search interfaces.
		Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
		for (Class<?> ifc : interfaces) {
			Method method = searchForMatch(ifc, bridgeMethod);
			if (method != null && !method.isBridge()) {
				return method;
			}
		}

		return null;
	}
	
	private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
		return Reflection.findMethod(type, bridgeMethod.getName(), bridgeMethod.getParameterTypes());
	}

	public static Class<? extends Object> getTargetClass(Object target) {
		Assert.isNotNull(target, "Candidate object must not be null");
		Class<?> result = null;
		while (target instanceof TargetClassAware) {
			result = ((TargetClassAware) target).getTargetClass();
			Object nested = null;
			if (target instanceof Aspected) {
				TargetSource targetSource = ((Aspected) target).getTargetSource();
				if (targetSource instanceof SimpleTargetSource) {
					nested = ((SimpleTargetSource) targetSource).getTarget();
				}
			}
			target = nested;
		}
		if (result == null) {
			result = (isCglibProxy(target) ? target.getClass().getSuperclass() : target.getClass());
		}
		return result;
	}

		public static boolean isJdkDynamicProxy(Object object) {
		return (object instanceof RuntimeProxy && Proxy.isProxyClass(object.getClass()));
	}

		public static boolean isCglibProxy(Object object) {
			return (object instanceof RuntimeProxy && ClassUtils.isCglibProxy(object));
		}
	
}
