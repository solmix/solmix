/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.fmk.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.DataSourceProvider;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-5 solmix-ds
 */
@SuppressWarnings("unchecked")
public class Reflection
{

    public static class GenericParameterNode
    {

        public Class<?> getClassByIndex(int index) {
            if (index < classes.size())
                return classes.get(index);
            else
                return null;
        }

        public void addClass(Class<?> theClass) {
            classes.add(theClass);
        }

        public GenericParameterNode getChildNode() {
            return childNode;
        }

        public void setChildNode(GenericParameterNode childNode) {
            this.childNode = childNode;
        }

        private final List<Class<?>> classes;

        private GenericParameterNode childNode;

        public GenericParameterNode()
        {
            this(null);
        }

        public GenericParameterNode(Class<?> theClass)
        {
            classes = new ArrayList<Class<?>>();
            if (theClass != null)
                classes.add(theClass);
            childNode = null;
        }
    }

    private static Logger log = LoggerFactory.getLogger(Reflection.class);

    private static Map<String, Object> reflectionCache = new ConcurrentHashMap<String, Object>();

    public static Throwable getRealTargetException(Throwable ite) {
        if (ite instanceof InvocationTargetException)
            return getRealTargetException(((InvocationTargetException) ite).getTargetException());
        else
            return ite;
    }

    public static Object newInstance(String className) throws Exception {
        return newInstance(classForName(className));
    }

    public static Class<?> classForName(String name) throws Exception {
        try {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            return Class.forName(name);
        }
    }

    public static <T> T newInstance(Class<T> clazz) throws Exception {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException iae) {
            iae = new IllegalAccessException((new StringBuilder()).append("Instantiation of class: ").append(clazz.getName()).append(
                " threw an IllegalAccessException - either the class or its").append(" zero-argument constructor is not accessible").toString());
            iae.fillInStackTrace();
            throw iae;
        } catch (InstantiationException ie) {
            ie = new InstantiationException((new StringBuilder()).append("Instantiation of class: ").append(clazz.getName()).append(
                " threw an InstantiationException - most likely cause is the").append(" class represents an abstract class, an interface,").append(
                " an array class, a primitive type, or void; or the class has no").append(" zero-argument constructor.").toString());
            ie.fillInStackTrace();
            throw ie;
        } catch (ExceptionInInitializerError eiie) {
            eiie = new ExceptionInInitializerError(
                (new StringBuilder()).append("Instantiation of class: ").append(clazz.getName()).append(
                    " threw an ExceptionInInitializerError - most likely cause is").append(
                    " a failure to initialize the class (check your static initializers).").append(" Root cause: ").append(eiie.getCause().toString()).toString());
            eiie.fillInStackTrace();
            throw eiie;
        } catch (SecurityException se) {
            se = new SecurityException((new StringBuilder()).append("Instantiation of class: ").append(clazz.getName()).append(
                " threw a SecurityException - most likely cause is").append(" that there is no permissio nto create a new instance").append(
                " of this class - error: ").append(se.toString()).toString());
            se.fillInStackTrace();
            throw se;
        }

    }

    public static Object invokeStaticMethod(String className, String methodName, Object param1, Object param2) throws Exception {
        Object params[] = { param1, param2 };
        return invokeStaticMethod(className, methodName, params);
    }

    public static Object invokeStaticMethod(String className, String methodName, Object param1) throws Exception {
        Object params[] = { param1 };
        return invokeStaticMethod(className, methodName, params);
    }

    public static Object invokeStaticMethod(String className, String methodName, Object... params) throws Exception {
        try {
            Method method = findMethod(className, methodName, lookupTypes(params));
            return method.invoke(null, params);
        } catch (InvocationTargetException ite) {
            Throwable throwable = getRealTargetException(ite);
            if (throwable instanceof Exception)
                throw (Exception) throwable;
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                log.error((new StringBuilder()).append("invokeStaticMethod() for method '").append(methodName).append("' caught a throwable that is").append(
                    " neither an Exception or an Error. Repackaging and re-throwing as an Error").toString());
                Error error = new Error(throwable.getMessage());
                error.fillInStackTrace();
                throw error;
            }
        }
    }

    private static Class<?>[] lookupTypes(Object args[]) {
        if (args == null)
            return null;
        Class<?> result[] = new Class[args.length];
        for (int ii = 0; ii < args.length; ii++)
            if (args[ii] == null)
                result[ii] = null;
            else
                result[ii] = args[ii].getClass();

        return result;
    }

    public static Method findMethod(String methodName) throws Exception {
        int index = methodName.lastIndexOf('.');
        return findMethod(methodName.substring(0, index), methodName.substring(index + 1));
    }

    public static Method findMethod(String className, String methodName) throws Exception {
        return findMethod(className, methodName, new Class[0]);
    }

    public static Method findMethod(Object instance, String methodName) throws Exception {
        return findMethod(instance.getClass().getName(), methodName, new Class[0]);
    }

    public static Method findMethod(Class<?> cls, String name, Class<?>... params) {
        
        if (cls == null) {
            return null;
        }
        for (Class<?> cs : cls.getInterfaces()) {
            if (Modifier.isPublic(cs.getModifiers())) {
                Method m = findMethod(cs, name, params);
                if (m != null && Modifier.isPublic(m.getModifiers())) {
                    return m;
                }
            }
        }
        try {
            Method m = cls.getDeclaredMethod(name, params);
            if (m != null && Modifier.isPublic(m.getModifiers())) {
                return m;
            }
        } catch (Exception e) {
            //ignore
        }
        Method m = findMethod(cls.getSuperclass(), name, params);
        if (m == null) {
            try {
                m = cls.getMethod(name, params);
            } catch (Exception e) {
                //ignore
            }
        }
        return m;

    }

    public static Method findMethod(String className, String methodName, Class<?>... methodArgs) throws Exception {
        Map<String, Object> classCache = getClassCache(className);
        Class<?> classObject = (Class<?>) classCache.get("ClassObject");
        Map<?, ?> methodCache = (Map<?, ?>) classCache.get("methods");
        Method directLookup = null;
            directLookup = findMethod(classObject,methodName, methodArgs);
        if (directLookup != null)
            return directLookup;
        Object value = methodCache.get(methodName);
        if (value == null) {
            String message = (new StringBuilder()).append("Method ").append(methodName).append(" not found on class ").append(className).toString();
            throw new NoSuchMethodException(message);
        }
        if (value instanceof Method)
            return (Method) value;
        if (value instanceof Map) {
            Object argValue = ((Map<?, ?>) value).get(new Integer(methodArgs.length));
            if (argValue == null) {
                String message = (new StringBuilder()).append("Method ").append(methodName).append(" exists on ").append(className).append(
                    " but with a different number of parameters - you passed in ").append(methodArgs.length).toString();
                throw new NoSuchMethodException(message);
            }
            if (argValue instanceof Method)
                return (Method) argValue;
            if (argValue instanceof List) {
                String message = (new StringBuilder()).append("Method ").append(className).append(".").append(methodName).append(
                    " is overloaded with the").append(" same number of parameters (").append(methodArgs.length).append(") - this is not supported.").toString();
                throw new Exception(message);
            } else {
                String message = (new StringBuilder()).append(
                    "Shouldn't happen: reflectionCache is corrupt - method lookup by param number returned object type: ").append(methodName).toString();
                throw new Exception(message);
            }
        } else {
            String message = (new StringBuilder()).append("Shouldn't happen: reflectionCache is corrupt - method lookup returned object type: ").append(
                methodName).toString();
            throw new Exception(message);
        }
    }

    private static Map<String, Object> getClassCache(String className) throws Exception {
        Map<String, Object> classCache = (Map<String, Object>) reflectionCache.get(className);
        if (classCache == null)
            classCache = populateClassCache(className);
        return classCache;
    }

    private static Map<String, Object> populateClassCache(String className) throws Exception {
        Map<String, Object> classCache = new HashMap<String, Object>();
        Class<?> classObject = classForName(className);
        classCache.put("ClassObject", classObject);
        Map<String, Object> methodCache = new HashMap<String, Object>();
        classCache.put("methods", methodCache);
        Method classMethods[] = classObject.getMethods();
        for (int ii = 0; ii < classMethods.length; ii++) {
            Method method = classMethods[ii];
            addMethodToCache(method, methodCache);
        }
        reflectionCache.put(className, classCache);
        return classCache;
    }

    private static void addMethodToCache(Method method, Map<String, Object> methodCache) throws Exception {
        String methodName = method.getName();
        Object value = methodCache.get(methodName);
        if (value == null)
            methodCache.put(methodName, method);
        else if (value instanceof Method) {
            Map<String, Object> argCache = new HashMap<String, Object>();
            methodCache.put(methodName, argCache);
            addMethodToCache((Method) value, methodCache);
            addMethodToCache(method, methodCache);
        } else if (value instanceof Map) {
            Map<Object, Object> argCache = (Map<Object, Object>) value;
            Integer numArgs = new Integer(method.getParameterTypes().length);
            Object argValue = argCache.get(numArgs);
            if (argValue == null)
                argCache.put(numArgs, method);
            else if (argValue instanceof Method) {
                List<Object> argList = new ArrayList<Object>();
                argList.add(method);
                argList.add(argValue);
            } else if (argValue instanceof List)
                ((List<Object>) argValue).add(method);
        }
    }

    /**
     * @param serverObjectInstance
     * @param method
     * @param requiredArgs
     * @param optionalArgs
     * @return
     */
    public static Object adaptArgsAndInvoke(Object instance, Method method, ReflectionArgument[] requiredArgs, ReflectionArgument[] optionalArgs)
        throws Exception {
        return adaptArgsAndInvoke(instance, method, requiredArgs, optionalArgs, null);
    }

    public static Object invokeMethod(Object classInstance, String methodName) throws Exception {
        return invokeMethod(classInstance, methodName, new Object[0]);
    }

    public static Object invokeMethod(Object classInstance, String methodName, Object params[]) throws Exception {
        try {
            Method method = findMethod(classInstance.getClass().getName(), methodName, lookupTypes(params));
            return method.invoke(classInstance, params);
        } catch (InvocationTargetException ite) {
            Throwable throwable = getRealTargetException(ite);
            if (throwable instanceof Exception)
                throw (Exception) throwable;
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                log.error((new StringBuilder()).append("invokeMethod() for method '").append(methodName).append(
                    "' caught a throwable that is neither").append(" an Exception nor an Error. Repackaging and re-throwing as an Error").toString());
                Error error = new Error(throwable.getMessage());
                error.fillInStackTrace();
                throw error;
            }
        }
    }

    public static Object invokeMethod(Object classInstance, String methodName, Object param1) throws Exception {
        Object params[] = { param1 };
        return invokeMethod(classInstance, methodName, params);
    }

    public static Object invokeMethod(Object classInstance, String methodName, Object param1, Object param2) throws Exception {
        Object params[] = { param1, param2 };
        return invokeMethod(classInstance, methodName, params);
    }

    public static Object invokeMethod(Object classInstance, String methodName, Object param1, Object param2, Object param3) throws Exception {
        Object params[] = { param1, param2, param3 };
        return invokeMethod(classInstance, methodName, params);
    }

    public static Object adaptArgsAndInvoke(Object instance, Method method, ReflectionArgument requiredArgs[], ReflectionArgument optionalArgs[],
        DataSource dataSource) throws Exception {
        // ClassLoader localLoader =Reflection.class.getClassLoader();
        // ClassLoader threadLoader =Thread.currentThread().getContextClassLoader();
        // Thread.currentThread().setContextClassLoader(localLoader);
        List<Object> methodArgs = new ArrayList<Object>();
        if (optionalArgs == null)
            optionalArgs = new ReflectionArgument[0];
        if (requiredArgs == null)
            requiredArgs = new ReflectionArgument[0];
        log.debug((new StringBuilder()).append("adaptArgsAndInvoke:\n\n ").append(method.toString()).append("\n\nrequiredArgs: ").append(
            typesAsString(requiredArgs)).append(" optionalArgs: ").append(typesAsString(optionalArgs)).toString());
        int requiredArgIndex = 0;
        List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
        List<GenericParameterNode> genericParamTypes = getGenericParameterTypes(method);
        for (int i = 0; i < parameterTypes.size(); i++) {
            Class<?> paramType = parameterTypes.get(i);
            GenericParameterNode genericParamInfo = genericParamTypes.get(i);
            if (genericParamInfo != null && genericParamInfo.getClassByIndex(0) == paramType)
                genericParamInfo = genericParamInfo.getChildNode();
            // Exception requiredAdaptException = null;
            ReflectionArgument reqArg = null;
            if (requiredArgIndex < requiredArgs.length) {
                reqArg = requiredArgs[requiredArgIndex];
                boolean assignedRequiredArg = false;
                try {
                    if (reqArg.getValue() == null) {
                        boolean optionalArgMatch = false;
                        int k = 0;
                        do {
                            if (k >= optionalArgs.length)
                                break;
                            if (paramType.isAssignableFrom(optionalArgs[k].getType()) && !paramType.isAssignableFrom(java.lang.Object.class)) {
                                optionalArgMatch = true;
                                break;
                            }
                            k++;
                        } while (true);
                        if (optionalArgMatch) {
                            log.debug((new StringBuilder()).append("not assigning null value to optional arg slot: ").append(paramType.getName()).toString());
                        } else {
                            methodArgs.add(null);
                            assignedRequiredArg = true;
                        }
                    } else {
                        methodArgs.add(adaptValue(paramType, genericParamInfo, reqArg, optionalArgs, dataSource, null, null, null));
                        assignedRequiredArg = true;
                    }
                    if (assignedRequiredArg) {
                        if (log.isTraceEnabled())
                            log.trace((new StringBuilder()).append("Successfully adapted required arg type: ").append(
                                reqArg.getType() != null ? reqArg.getType().getName() : "null").append(" to type: ").append(paramType.getName()).toString());
                        requiredArgIndex++;
                        continue;
                    }
                } catch (Exception e) {
                    if (log.isTraceEnabled()) {
                        Class<?> type = requiredArgs[requiredArgIndex].getType();
                        log.trace((new StringBuilder()).append("Failed to adapt required arg type: ").append(type != null ? type.getName() : "null").append(
                            " to type: ").append(paramType.getName()).append(". Error string: ").append(e.toString()).append(" in ").append(
                            e.getStackTrace()[0].toString()).toString());
                    }
                    // requiredAdaptException = e;
                }
            }
            boolean assignedOptionalArg = false;
            for (int j = 0; j < optionalArgs.length; j++) {
                ReflectionArgument optionalArg = optionalArgs[j];
                try {
                    methodArgs.add(adaptValue(paramType, genericParamInfo, optionalArg, optionalArgs, dataSource, null, null, null));
                    assignedOptionalArg = true;
                    if (log.isTraceEnabled())
                        log.trace((new StringBuilder()).append("Successfully adapted optional arg type: ").append(optionalArg.getType().getName()).append(
                            " to type: ").append(paramType.getName()).toString());
                    break;
                } catch (Exception e) {
                    if (e.getClass() != Exception.class)
                        log.warn((new StringBuilder()).append("Exception occurred attempting to map arguments: ").append(e.toString()).append(" in ").append(
                            e.getStackTrace()[0].toString()).toString());
                }
            }

            if (assignedOptionalArg)
                continue;
            String errorString = (new StringBuilder()).append("Unable to assign a required or optional argument to slot #").append(i + 1).append(
                " taking type: ").append(paramType.getName()).append(" of method:\n\n").append(method.toString()).append("\n\n").append(
                "No remaining optional arguments match this type").toString();
            if (reqArg != null)
                errorString = (new StringBuilder()).append(errorString).append(".  The next required argument passed by the client was of type: ").append(
                    reqArg.getType() != null ? reqArg.getType().getName() : "null").append(".").toString();
            else
                errorString = (new StringBuilder()).append(errorString).append(
                    " and all required arguments passed by the client have already been assigned.").toString();
            if (optionalArgs.length == 0)
                errorString = (new StringBuilder()).append(errorString).append(
                    "  Note that no optional arguments were available for this assignment, likely because your method definition in your .app.xml file specifies explicit methodArguments.  Please check to make sure this methodArguments declaration matches your method signature.").toString();
            errorString = (new StringBuilder()).append(errorString).append("\n\n").toString();
            throw new Exception(errorString);
        }

        Object args[] = DataUtil.listToArray(methodArgs);
        if (log.isTraceEnabled()) {
            log.trace((new StringBuilder()).append("method takes: ").append(parameterTypes.size()).append(" args.  I've assembled: ").append(
                methodArgs.size()).append(" args").toString());
            log.trace((new StringBuilder()).append("invoking method:\n").append(getFormattedMethodSignature(method)).append("\n\nwith arg types: ").append(
                getFormattedParamTypes(args)).toString());
        }
        return method.invoke(instance, args);
    }

    /**
     * @param method
     * @return
     */
    private static List<GenericParameterNode> getGenericParameterTypes(Method method) {
        List<GenericParameterNode> list = new ArrayList<GenericParameterNode>();
        if (method == null)
            return list;
        Type gParams[] = method.getGenericParameterTypes();
        for (int i = 0; i < gParams.length; i++) {
            GenericParameterNode gpn;
            if (gParams[i] instanceof ParameterizedType)
                gpn = buildGenericParameterTree((ParameterizedType) gParams[i]);
            else
                gpn = new GenericParameterNode((Class<?>) gParams[i]);
            list.add(gpn);
        }
        return list;
    }

    private static GenericParameterNode buildGenericParameterTree(ParameterizedType type) {
        GenericParameterNode gpn = new GenericParameterNode();
        Type paramTypes[] = type.getActualTypeArguments();
        if (paramTypes.length == 1 && (paramTypes[0] instanceof Class<?>)) {
            gpn.addClass((Class<?>) paramTypes[0]);
            return gpn;
        }
        gpn.addClass((Class<?>) type.getRawType());
        for (int j = 0; j < paramTypes.length; j++)
            if (paramTypes[j] instanceof Class) {
                if (gpn.getChildNode() == null)
                    gpn.setChildNode(new GenericParameterNode((Class<?>) paramTypes[j]));
                else
                    gpn.getChildNode().addClass((Class<?>) paramTypes[j]);
            } else {
                gpn.setChildNode(buildGenericParameterTree((ParameterizedType) paramTypes[j]));
            }

        return gpn;
    }

    private static String getFormattedParamTypes(Object params[]) {
        String result = "";
        if (params == null || params.length == 0)
            return "(empty parameter list)";
        for (int ii = 0; ii < params.length; ii++) {
            result = (new StringBuilder()).append(result).append(params[ii] == null ? "null" : params[ii].getClass().getName()).toString();
            if (ii + 1 < params.length)
                result = (new StringBuilder()).append(result).append(", ").toString();
        }

        return result;
    }

    /**
     * Auto adapt method argument value.
     * 
     * @param targetType Target parameter class type.
     * @param targetGenericInfo
     * @param arg
     * @param optionalArgs
     * @param dataSource
     * @param javaClass
     * @param javaCollectionClass
     * @param javaKeyClass
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Object adaptValue(Class<?> targetType, GenericParameterNode targetGenericInfo, ReflectionArgument arg,
        ReflectionArgument optionalArgs[], DataSource dataSource, Class<?> javaClass, Class<?> javaCollectionClass, Class<?> javaKeyClass)
        throws Exception {
        String argTypeName;
        Object argValue;
        String targetTypeName;
        Class<?> argType = arg.getType();
        argTypeName = argType != null ? argType.getName() : "null";
        argValue = arg.getValue();
        targetTypeName = targetType.getName();
        if (argType == null && argValue == null)
            return null;
        if (log.isTraceEnabled())
            log.trace((new StringBuilder()).append("checking whether type: ").append(argTypeName).append(" fulfills type: ").append(targetTypeName).toString());
        // If the target type is a collection class type.
        // && !IDoNotAdapt.class.isAssignableFrom(targetType)
        if ((Collection.class.isAssignableFrom(targetType) || Map.class.isAssignableFrom(targetType))) {
            if (optionalArgs != null) {
                for (int i = 0; i < optionalArgs.length; i++) {
                    Class<?> optionalType = optionalArgs[i].getType();
                    if (optionalType == argType && !optionalArgs[i].isAllowTypeConversion())
                        throw new Exception((new StringBuilder()).append(argTypeName).append(" not adaptable to: ").append(targetTypeName).toString());
                }

            }
            Object newArgValue;
            if (javaCollectionClass != null)
                newArgValue = javaCollectionClass.newInstance();
            else if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) {
                if (targetType.isAssignableFrom(argValue.getClass()))
                    newArgValue = argValue.getClass().newInstance();
                else if (Map.class.isAssignableFrom(targetType))
                    newArgValue = new LinkedMap();
                else if (List.class.isAssignableFrom(targetType))
                    newArgValue = new ArrayList<Object>();
                else if (Set.class.isAssignableFrom(targetType))
                    newArgValue = new HashSet<Object>();
                else if (Queue.class.isAssignableFrom(targetType))
                    newArgValue = new LinkedList<Object>();
                else if (Collection.class.isAssignableFrom(targetType))
                    newArgValue = new ArrayList<Object>();
                else
                    newArgValue = argValue.getClass().newInstance();
            } else {
                newArgValue = targetType.newInstance();
            }
            Iterator<Object> i = null;
            Object key = null;
            Class<?> keyClass = Object.class;
            Class<?> valueClass = null;
            if (Collection.class.isAssignableFrom(targetType)) {
                if (argValue instanceof Map) {
                    Map map = (Map) argValue;
                    if (map.size() == 1) {
                        Iterator mapi = map.entrySet().iterator();
                        java.util.Map.Entry mape = (java.util.Map.Entry) mapi.next();
                        if (mape.getValue() instanceof Collection) {
                            argValue = mape.getValue();
                            argType = argValue.getClass();
                            argTypeName = argType.getName();
                        } else if (mape.getValue() instanceof Map) {
                            argValue = new ArrayList();
                            ((List) argValue).add(mape.getValue());
                            argType = argValue.getClass();
                            argTypeName = argType.getName();
                        }
                    }
                }
                i = ((Collection) argValue).iterator();
                if (targetGenericInfo != null)
                    valueClass = targetGenericInfo.getClassByIndex(0);
            } else if (Collection.class.isAssignableFrom(argType) && Map.class.isAssignableFrom(targetType)) {
                if (((Collection) argValue).size() == 1) {
                    argValue = ((Collection) argValue).iterator().next();
                    argType = argValue.getClass();
                    argTypeName = argType.getName();
                    i = ((Map) argValue).keySet().iterator();
                }
            } else {
                i = ((Map) argValue).keySet().iterator();
                if (targetGenericInfo != null) {
                    keyClass = targetGenericInfo.getClassByIndex(0);
                    valueClass = targetGenericInfo.getClassByIndex(1);
                }
            }
            if (javaClass != null)
                valueClass = javaClass;
            if (javaKeyClass != null)
                keyClass = javaKeyClass;
            boolean resetValueClass = false;
            if (valueClass == null)
                resetValueClass = true;
            while (i.hasNext()) {
                Object value = null;
                if (resetValueClass)
                    valueClass = null;
                if (Collection.class.isAssignableFrom(targetType)) {
                    value = i.next();
                } else {
                    key = i.next();
                    value = ((Map) argValue).get(key);
                }
                if (valueClass == null && value != null)
                    valueClass = value.getClass();
                Object newValue = null;
                if (value != null) {
                    ReflectionArgument subArg = new ReflectionArgument(value.getClass(), value, true, true);
                    newValue = adaptValue(valueClass, targetGenericInfo != null ? targetGenericInfo.getChildNode() : null, subArg, optionalArgs,
                        dataSource, javaClass, javaCollectionClass, javaKeyClass);
                }
                Object newKey = key;
                if (keyClass != Object.class) {
                    ReflectionArgument subArg = new ReflectionArgument(key.getClass(), key, true, true);
                    newKey = adaptValue(keyClass, targetGenericInfo != null ? targetGenericInfo.getChildNode() : null, subArg, optionalArgs,
                        dataSource, null, null, null);
                }
                if (Collection.class.isAssignableFrom(targetType))
                    ((Collection) newArgValue).add(newValue);
                else
                    ((Map) newArgValue).put(newKey, newValue);
            }
            return newArgValue;
        }// END collection type check?
        if (targetType.isAssignableFrom(argType))

            return javaClass != null ? javaClass.cast(argValue) : argType.cast(argValue);
        if (arg.isAllowTypeConversion() && !Map.class.isAssignableFrom(argType))
            try {
                Object translatedValue;
                if (javaClass != null)
                    translatedValue = DataUtil.convertType(javaClass, argValue);
                else
                    translatedValue = DataUtil.convertType(targetType, argValue);
                return translatedValue;
            } catch (IllegalArgumentException iae) {
                throw iae;
            }
        if (!arg.isAllowTypeConversion() || !Map.class.isAssignableFrom(argType))
            if (optionalArgs != null) {
                for (int i = 0; i < optionalArgs.length; i++) {
                    Class optionalType = optionalArgs[i].getType();
                    if (optionalType == targetType)
                        throw new Exception((new StringBuilder()).append(argTypeName).append(" not adaptable to: ").append(targetTypeName).toString());
                }

            }
        if (log.isTraceEnabled())
            log.trace((new StringBuilder()).append("Converting Map arg to bean of type: ").append(targetTypeName).toString());
        if (argValue == null) {
            log.debug((new StringBuilder()).append("Assigning null value to bean: ").append(targetTypeName).toString());
            return null;
        }
        Object beanInstance = null;
        try {
            beanInstance = newInstance(targetType);
        } catch (Exception e) {
            Throwable rte = getRealTargetException(e);
            log.warn((new StringBuilder()).append("Failed to convert Map arg to bean of type: ").append(targetTypeName).append(
                " - because instantiation of ").append(targetTypeName).append(" threw the following Exception: ").append(rte.toString()).toString());
            throw e;
        }
        Map map = (Map) argValue;
        Map newArgMap = (Map) argValue.getClass().newInstance();
        Map props = DataUtil.getPropertyDescriptors(beanInstance);
        Object key;
        Object newArgValue;
        for (Iterator i = map.keySet().iterator(); i.hasNext(); newArgMap.put(key, newArgValue)) {
            key = i.next();
            Object value = map.get(key);
            newArgValue = value;
            String subFieldType = null;
            Class<?> subJavaClass = null;
            Class<?> subJavaCollectionClass = null;
            Class<?> subJavaKeyClass = null;
            String subFieldName = key.toString();
            if (dataSource != null && subFieldName != null)
                // try
                // {
                // Tfield field = dataSource.getContext().getField( subFieldName );
                // if ( field != null )
                // {
                // String typeName = field.getProperty( "javaClass" );
                // if ( typeName != null )
                // subJavaClass = Class.forName( typeName );
                // typeName = field.getProperty( "javaCollectionClass" );
                // if ( typeName != null )
                // subJavaCollectionClass = Class.forName( typeName );
                // typeName = field.getProperty( "javaKeyClass" );
                // if ( typeName != null )
                // subJavaKeyClass = Class.forName( typeName );
                // }
                // } catch ( Exception e )
                // {
                // log.warn( ( new StringBuilder() ).append( e.getClass().getName() ).append( " " ).append(
                // e.getMessage()
                // ).append( " encountered " ).append(
                // "whilst trying to derive Java classes from override class names " ).append( "for DataSource '"
                // ).append(
                // dataSource.getName() ).append(
                // "', field name '" ).append( subFieldName ).append( "'" ).toString() );
                // }
                if (!(value instanceof Collection) && !(value instanceof Map))
                    continue;
            PropertyDescriptor pd = (PropertyDescriptor) props.get(key);
            GenericParameterNode type = null;
            ReflectionArgument ra = null;
            boolean readMethod = true;
            if (pd == null)
                continue;
            Method method = pd.getReadMethod();
            if (method != null) {
                type = getGenericReturnType(method);
                ra = new ReflectionArgument(value.getClass(), value, true, true);
            } else {
                readMethod = false;
                method = pd.getWriteMethod();
                if (method != null) {
                    List paramTypes = getGenericParameterTypes(method);
                    type = (GenericParameterNode) paramTypes.get(0);
                    ra = new ReflectionArgument(value.getClass(), value, true, true);
                }
            }
            if (dataSource != null) {
                subFieldName = key.toString();
                if (subFieldName != null) {
                    Tfield subField = dataSource.getContext().getField(subFieldName);
                    if (subField != null)
                        subFieldType = subField.getType().value();
                }
            }
            DataSource subDataSource;
            if (subFieldType == null)
                subDataSource = null;
            else
                subDataSource = DataSourceProvider.forName(subFieldType);
            newArgValue = adaptValue(readMethod ? method.getReturnType() : method.getParameterTypes()[0], type != null ? type.getChildNode() : null,
                ra, optionalArgs, subDataSource, subJavaClass, subJavaCollectionClass, subJavaKeyClass);
        }

        try {
            if (dataSource != null)
                return new Object();
            // dataSource.setProperties( newArgMap, beanInstance );
        } catch (Exception e) {
            Throwable rte = getRealTargetException(e);
            String error = (new StringBuilder()).append("Failed to convert Map arg to bean of type: ").append(targetTypeName).append(
                " - because bean population via").append(" DataTools.setProperties() threw the following Exception: ").append(rte.toString()).toString();
            if ((rte instanceof IllegalArgumentException) || (rte instanceof ClassCastException))
                error = (new StringBuilder()).append(error).append(
                    ". Maybe you have overridden an automatic binding by setting property 'javaClass' on a DataSourceField to an incorrect class (ie, a class that cannot be cast to the type that the setter function actually takes)?").toString();
            log.warn(error);
            throw e;
        }
        // return DataTools.setProperties(newArgMap, beanInstance, dataSource);
        if (arg.isAllowTypeConversion()) {
            log.trace("trying convertType");
            return DataUtil.convertType(targetType, argValue);
        } else {
            throw new Exception((new StringBuilder()).append(argTypeName).append(" not adaptable to: ").append(targetTypeName).toString());
        }
    }

    /**
     * @param method
     * @return
     */
    private static GenericParameterNode getGenericReturnType(Method method) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getFormattedMethodSignature(Method method) {
        if (method == null)
            return null;
        String result = (new StringBuilder()).append(method.getReturnType().getName()).append(" ").append(method.getDeclaringClass().getName()).append(
            ".").append(method.getName()).append("(").toString();
        Class<?> paramTypes[] = method.getParameterTypes();
        for (int ii = 0; ii < paramTypes.length; ii++) {
            result = (new StringBuilder()).append(result).append(paramTypes[ii].getName()).toString();
            if (ii + 1 < paramTypes.length)
                result = (new StringBuilder()).append(result).append(", ").toString();
        }

        result = (new StringBuilder()).append(result).append(")").toString();
        Class<?> exceptionTypes[] = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            result = (new StringBuilder()).append(result).append(" throws ").toString();
            for (int ii = 0; ii < exceptionTypes.length; ii++) {
                result = (new StringBuilder()).append(result).append(exceptionTypes[ii].getName()).toString();
                if (ii + 1 < exceptionTypes.length)
                    result = (new StringBuilder()).append(result).append(", ").toString();
            }

        }
        return result;
    }

    public static String typesAsString(Class<?> classes[]) {
        List<String> result = new ArrayList<String>();
        if (classes == null)
            return result.toString();
        for (int i = 0; i < classes.length; i++)
            result.add(classes[i].getName());

        return result.toString();
    }

    private static String typesAsString(ReflectionArgument args[]) {
        List<String> result = new ArrayList<String>();
        if (args == null)
            return result.toString();
        for (int i = 0; i < args.length; i++) {
            Class<?> type = args[i].getType();
            if (type == null)
                result.add("null");
            else
                result.add(type.getName());
        }

        return result.toString();
    }

    /**
     * 
     * @param method
     * @param class1
     * @return
     */
    public static boolean methodTakesArgType(Method method, Class<?> type) {
        Class<?> parameterTypes[] = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == type)
                return true;
        }
        return false;
    }

}
