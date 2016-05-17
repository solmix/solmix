/*
 * Copyright 2015 The Solmix Project
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月27日
 */

public class StaticFunctionDelegatorBuilder extends DynamicClassBuilder
{
    private final Map<Signature, Method> methods = new HashMap<Signature, Method>();
    private Class<?> mixinInterface;
    private Enhancer generator;

    public StaticFunctionDelegatorBuilder() {
    }

    public StaticFunctionDelegatorBuilder(ClassLoader cl) {
        super(cl);
    }

    public StaticFunctionDelegatorBuilder addClass(Class<?> utilClass) {
        for (Method method : utilClass.getMethods()) {
            if (isPublicStatic(method)) {
                addMethod(method);
            }
        }

        return this;
    }

    public StaticFunctionDelegatorBuilder addMethod(Method method) {
        return addMethod(method, null);
    }

    public StaticFunctionDelegatorBuilder addMethod(Method method, String rename) {
        Assert.assertNotNull(method, "Method is null");
        Assert.assertTrue(isPublicStatic(method), "Method is not public static: %s", method);

        Signature sig = getSignature(method, rename);

        if (methods.containsKey(sig)) {
            throw new IllegalArgumentException("Duplicated method signature: " + sig + "\n  method: "
                                               + methods.get(sig));
        }

        methods.put(sig, method);

        return this;
    }

    public Class<?> getMixinInterface() {
        if (mixinInterface == null) {
            InterfaceMaker im = new InterfaceMaker();

            for (Map.Entry<Signature, Method> entry : methods.entrySet()) {
                Signature sig = entry.getKey();
                Method method = entry.getValue();

                Type[] exceptionTypes = new Type[method.getExceptionTypes().length];

                for (int i = 0; i < exceptionTypes.length; i++) {
                    exceptionTypes[i] = Type.getType(method.getExceptionTypes()[i]);
                }

                im.add(sig, exceptionTypes);
            }

            im.setClassLoader(getClassLoader());
            im.setNamingPolicy(new DefaultNamingPolicy() {
                @Override
                public String getClassName(String prefix, String source, Object key, Predicate names) {
                    return super.getClassName("", StaticFunctionDelegatorBuilder.class.getName(),
                                              key, names);
                }
            });

            mixinInterface = im.create();
        }

        return mixinInterface;
    }

    public Object toObject() {
        if (generator == null) {
            final Class<?> intfs = getMixinInterface();
            final Map<Method, FastMethod> methodMappings = getMethodMappings(intfs);

            generator = new Enhancer();

            generator.setClassLoader(getClassLoader());
            generator.setSuperclass(Object.class);
            generator.setInterfaces(new Class<?>[] { intfs });

            generator.setCallbacks(new Callback[] {
                    // default callback
                    new MethodInterceptor() {
                        @Override
                        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                                throws Throwable {
                            return proxy.invokeSuper(obj, args);
                        }
                    },

                    // toString callback
                    new MethodInterceptor() {
                        @Override
                        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                                throws Throwable {
                            Map<String,String> mb = new HashMap<String, String>();

                            for (Map.Entry<Method, FastMethod> entry : methodMappings.entrySet()) {
                                mb.put(
                                        entry.getKey().getName(),
                                        ClassDescUtils.getSimpleMethodSignature(entry.getValue().getJavaMethod(), false, true, true, false));
                            }

                            return new StringBuilder().append(intfs.getName()).append(mb).toString();
                        }
                    },

                    // proxied callback
                    new MethodInterceptor() {
                        @Override
                        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                                throws Throwable {
                            FastMethod realMethod = Assert.assertNotNull(methodMappings.get(method), "unknown method: %s", method);
                            return realMethod.invoke(null, args);
                        }
                    } });

            generator.setCallbackFilter(new CallbackFilter() {
                @Override
                public int accept(Method method) {
                    if (isEqualsMethod(method) || isHashCodeMethod(method)) {
                        return 0; // invoke super
                    } else if (isToStringMethod(method)) {
                        return 1; // invoke toString
                    } else {
                        return 2; // invoke proxied object
                    }
                }
            });
        }

        Object obj = generator.create();

        log.debug("Generated mixin function delegator: {}", obj);

        return obj;
    }

    private Map<Method, FastMethod> getMethodMappings(Class<?> intfs) {
        // 查找interface中的方法和被代理方法之间的对应关系
        Map<Method, FastMethod> methodMappings = new HashMap<Method, FastMethod>();

        for (Method method : intfs.getMethods()) {
            Signature sig = getSignature(method, null);
            Method realMethod = Assert.assertNotNull(methods.get(sig), "unknown method signature: %s", sig);
            FastClass fc = FastClass.create(getClassLoader(), realMethod.getDeclaringClass());
            FastMethod fm = fc.getMethod(realMethod);

            methodMappings.put(method, fm);
        }

        return methodMappings;
    }
}
