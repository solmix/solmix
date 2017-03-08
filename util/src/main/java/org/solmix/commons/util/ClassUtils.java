package org.solmix.commons.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public abstract class ClassUtils {

	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";

	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz,
			ClassLoader classLoader) {
		Set<Class<?>> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
		return ifcs.toArray(new Class<?>[ifcs.size()]);
	}

	public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz,
			ClassLoader classLoader) {
		Assert.isNotNull(clazz, "Class must not be null");
		if (clazz.isInterface() && isVisible(clazz, classLoader)) {
			return Collections.<Class<?>> singleton(clazz);
		}
		Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
		while (clazz != null) {
			Class<?>[] ifcs = clazz.getInterfaces();
			for (Class<?> ifc : ifcs) {
				interfaces.addAll(getAllInterfacesForClassAsSet(ifc,
						classLoader));
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
		if (classLoader == null) {
			return true;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return false;
		}
	}

	public static String getClassFileName(Class<?> clazz) {
		Assert.isNotNull(clazz, "Class must not be null");
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	public static String getPackageName(Class<?> clazz) {
		Assert.isNotNull(clazz, "Class must not be null");
		return getPackageName(clazz.getName());
	}

	public static String getPackageName(String fqClassName) {
		Assert.isNotNull(fqClassName, "Class name must not be null");
		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex)
				: "");
	}

	public static boolean hasMethod(Class<?> clazz, String methodName,
			Class<?>... paramTypes) {
		return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
	}

	public static Method getMethodIfAvailable(Class<?> clazz,
			String methodName, Class<?>... paramTypes) {
		Assert.isNotNull(clazz, "Class must not be null");
		Assert.isNotNull(methodName, "Method name must not be null");
		if (paramTypes != null) {
			try {
				return clazz.getMethod(methodName, paramTypes);
			} catch (NoSuchMethodException ex) {
				return null;
			}
		} else {
			Set<Method> candidates = new HashSet<Method>(1);
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (methodName.equals(method.getName())) {
					candidates.add(method);
				}
			}
			if (candidates.size() == 1) {
				return candidates.iterator().next();
			}
			return null;
		}
	}

	public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
		return (getConstructorIfAvailable(clazz, paramTypes) != null);
	}

	public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz,
			Class<?>... paramTypes) {
		Assert.isNotNull(clazz, "Class must not be null");
		try {
			return clazz.getConstructor(paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	public static String classNamesToString(Collection<Class<?>> classes) {
		if (DataUtils.isNullOrEmpty(classes)) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (Iterator<Class<?>> it = classes.iterator(); it.hasNext();) {
			Class<?> clazz = it.next();
			sb.append(clazz.getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	public static String getShortName(String className) {
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	public static String getQualifiedName(Class<?> clazz) {
		Assert.isNotNull(clazz, "Class must not be null");
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append(ClassUtils.ARRAY_SUFFIX);
		}
		result.insert(0, clazz.getName());
		return result.toString();
	}

	public static Collection<? extends Class<?>> getAllInterfacesAsSet(
			Object instance) {
		Assert.isNotNull(instance, "Instance must not be null");
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
		return getAllInterfacesForClassAsSet(clazz, null);
	}

	public static boolean isCglibProxy(Object object) {
		return ClassUtils.isCglibProxyClass(object.getClass());
	}

	public static boolean isCglibProxyClass(Class<?> clazz) {
		return (clazz != null && isCglibProxyClassName(clazz.getName()));
	}

	public static boolean isCglibProxyClassName(String className) {
		return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
	}

	public static boolean isFinalizeMethod(Method method) {
		return (method != null && method.getName().equals("finalize") &&
				method.getParameterTypes().length == 0);
	}

	public static boolean isUserLevelMethod(Method method) {
		Assert.isNotNull(method, "Method must not be null");
		return (method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method)));
	}
	private static boolean isGroovyObjectMethod(Method method) {
		return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
	}

	public static Class<?> getUserClass(Class<?> clazz) {
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
		if (method != null && isOverridable(method, targetClass) &&
				targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
			try {
				if (Modifier.isPublic(method.getModifiers())) {
					try {
						return targetClass.getMethod(method.getName(), method.getParameterTypes());
					}
					catch (NoSuchMethodException ex) {
						return method;
					}
				}
				else {
					Method specificMethod =
							Reflection.findMethod(targetClass, method.getName(), method.getParameterTypes());
					return (specificMethod != null ? specificMethod : method);
				}
			}
			catch (AccessControlException ex) {
				// Security settings are disallowing reflective access; fall back to 'method' below.
			}
		}
		return method;
	}
	
	private static boolean isOverridable(Method method, Class<?> targetClass) {
		if (Modifier.isPrivate(method.getModifiers())) {
			return false;
		}
		if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
			return true;
		}
		return getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass));
	}

	public static Class<?>[] getAllInterfaces(Object instance) {
		Assert.isNotNull(instance, "Instance must not be null");
		return getAllInterfacesForClass(instance.getClass());
	}

}
