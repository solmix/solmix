/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.commons.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Reflection Utils
 * 
 * @author solmix.f@gmail.com
 */

public class Reflection
{

    private static Logger LOG = LoggerFactory.getLogger(Reflection.class);

    private static Map<String, ClassEntry> reflectionCache = new ConcurrentHashMap<String, ClassEntry>();

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
        return ClassLoaderUtils.loadClass(name, Reflection.class);
    }
    /**
     * Instance of given clazz.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
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
    /**
     * Invoke static method.
     */
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
                LOG.error((new StringBuilder()).append("invokeStaticMethod() for method '").append(methodName).append("' caught a throwable that is").append(
                    " neither an Exception or an Error. Repackaging and re-throwing as an Error").toString());
                Error error = new Error(throwable.getMessage());
                error.fillInStackTrace();
                throw error;
            }
        }
    }
    
    public static Method findMethod(String methodName) throws Exception {
        int index = methodName.lastIndexOf('.');
        return findMethod(methodName.substring(0, index), methodName.substring(index + 1));
    }
    
    public static Method findMethod(String className, String methodName) throws Exception {
        return findMethod(className, methodName, new Class[0]);
    }

    public static Method findMethod(Object instance, String methodName) throws Exception {
        return findMethod(instance.getClass(), methodName, new Class[0]);
    }
    
    public static Method findMethod(String className, String methodName, Class<?>... methodArgs) throws Exception {
        return findMethod(className,methodName,false,methodArgs);
    }
    
    public static Method findMethod(String className, String methodName, boolean populateCache,Class<?>... methodArgs) throws Exception {
       Assert.isNotNull(methodArgs, "methodArgs must not null, An array of length 0 if the underlying method takes no parameters. ");
        if(populateCache){
           ClassEntry classCache = getClassCache(className);
           MethodEntry[] methods=classCache.methods;
           for(MethodEntry entry:methods){
               if(entry.equals(methodName, methodArgs)){
                   return entry.method;
               }
           }
       }else{
           Class<?> classObject = classForName(className);
           Method lookup=findMethod(classObject,methodName, methodArgs);
           if(lookup!=null)
               return lookup;
       }
       String message = (new StringBuilder()).append("Method ").append(methodName).append(" not found on class ").append(className).toString();
       throw new NoSuchMethodException(message);
       
    }

    public static Method findMethod(Class<?> cls, String name,
        Class<?>... params) {

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
            // ignore
        }
        Method m = findMethod(cls.getSuperclass(), name, params);
        if (m == null) {
            try {
                m = cls.getMethod(name, params);
            } catch (Exception e) {
                // ignore
            }
        }
        return m;
    }
    
    public static Object invokeMethod(Object classInstance, String methodName) throws Exception {
        return invokeMethod(classInstance, methodName, new Object[0]);
    }
    
    public static Object invokeMethod(Object classInstance, String methodName, Object... params) throws Exception {
        try {
            Method method = findMethod(classInstance.getClass(), methodName, lookupTypes(params));
            return method.invoke(classInstance, params);
        } catch (InvocationTargetException ite) {
            Throwable throwable = getRealTargetException(ite);
            if (throwable instanceof Exception)
                throw (Exception) throwable;
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                LOG.error((new StringBuilder()).append("invokeMethod() for method '").append(methodName).append(
                    "' caught a throwable that is neither").append(" an Exception nor an Error. Repackaging and re-throwing as an Error").toString());
                Error error = new Error(throwable.getMessage());
                error.fillInStackTrace();
                throw error;
            }
        }
    }
    
    public static Object invokeMethod(Object target, Method method, Object... params) throws Exception {
        setAccessible(method);
        return method.invoke(target, params);

    }
    
    private static ClassEntry getClassCache(String className) throws Exception {
        ClassEntry classCache = reflectionCache.get(className);
        if (classCache == null)
            classCache = populateClassCache(className);
        return classCache;
    }

    private static ClassEntry populateClassCache(String className) throws Exception {
       
        Class<?> classObject = classForName(className);
        ClassEntry classEntry= new ClassEntry(classObject);
        populateMethodCache(classObject,classEntry);
        reflectionCache.put(className, classEntry);
        return classEntry;
    }
    
    private static void populateMethodCache(Class<?> classObject,
        ClassEntry classEntry) {
        Method classMethods[] = classObject.getMethods();
        MethodEntry[] entrys= new MethodEntry[classMethods.length];
        for (int ii = 0; ii < classMethods.length; ii++) {
            entrys[ii].name=classMethods[ii].getName();
            entrys[ii].params= classMethods[ii].getParameterTypes();
            entrys[ii].method= classMethods[ii];
        }
        classEntry.methods=entrys;
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

    /**
     * @param targetClass
     * @return
     */
    public static  Field[] getDeclaredFields(final Class<? extends Object> cls) {
        return AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                return cls.getDeclaredFields();
            }
        });
    }

    /**
     * @param targetClass
     * @return
     */
    public static Method[]  getDeclaredMethods(final Class<? extends Object> cls) {
        return AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return cls.getDeclaredMethods();
            }
        });
    }

    /**
     * @param <T>
     * @param method
     */
    public static <T extends AccessibleObject> T setAccessible(final T o) {
        return AccessController.doPrivileged(new PrivilegedAction<T>() {
            @Override
            public T run() {
                o.setAccessible(true);
                return o;
            }
        });
    }
    
    public static <T extends AccessibleObject> T setAccessible(final T o, final boolean b) {
        return AccessController.doPrivileged(new PrivilegedAction<T>() {
            @Override
            public T run() {
                o.setAccessible(b);
                return o;
            }
        });
    }
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
  }

  /**
   * Determine whether the given method is an "equals" method.
   * @see java.lang.Object#equals(Object)
   */
  public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
              return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
  }

  public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
  }

  
  public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
  }

 
  public static boolean isObjectMethod(Method method) {
        if (method == null) {
              return false;
        }
        try {
              Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
              return true;
        }
        catch (Exception ex) {
              return false;
        }
  }
  
  public static Class<?> getGenericClass(Class<?> cls) {
      return getGenericClass(cls, 0);
  }

    public static Class<?> getGenericClass(Class<?> cls, int i) {
        try {
            ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[0]);
            Object genericClass = parameterizedType.getActualTypeArguments()[i];
            if (genericClass instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) {
                return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
            } else {
                return (Class<?>) genericClass;
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(cls.getName() + " generic type undefined!", e);
        }
    }
}

class MethodEntry
{

    public Method method;

    public String name;

    public Class<?>[] params;

    public boolean equals(String name, Class<?>[] argsType) {
        if (!this.name.equals(name))
            return false;
        if (params.length != argsType.length)
            return false;
        for (int i = 0; i < params.length; i++) {
            if (!params[i].getName().equals(argsType[i].getName()))
                return false;
        }
        return true;
    }
}

class ClassEntry
{

  public  ClassEntry(Class<?> classObject)
    {
        this.classObject = classObject;
    }

   public Class<?> classObject;

   public MethodEntry[] methods;
}