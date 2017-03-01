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
package org.solmix.runtime.helper;

import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月6日
 */

public class CglibProxyHelper extends ProxyHelper
{
    CglibProxyHelper() throws Exception {
        Class.forName("net.sf.cglib.proxy.Enhancer");
        Class.forName("net.sf.cglib.proxy.MethodInterceptor");
        Class.forName("net.sf.cglib.proxy.MethodProxy");
    }
    @Override
    protected Object getProxyInternal(ClassLoader loader, Class<?>[] interfaces, 
                                      final java.lang.reflect.InvocationHandler h) {
        
        Class<?> superClass = null;
        List<Class<?>> theInterfaces = new ArrayList<Class<?>>();
        
        for (Class<?> c : interfaces) {
            if (!c.isInterface()) {
                if (superClass != null) {
                    throw new IllegalArgumentException("Only a single superclass is supported");
                }
                superClass = c; 
            } else {
                theInterfaces.add(c);
            }
        }
        if (superClass != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setClassLoader(loader);
            enhancer.setSuperclass(superClass);
            enhancer.setInterfaces(theInterfaces.toArray(new Class[theInterfaces.size()]));
            enhancer.setCallback(new MethodInterceptor() {

                @Override
                public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, net.sf.cglib.proxy.MethodProxy proxy) 
                    throws Throwable {
                    return h.invoke(obj, method, args);
                }
                
            });
            return enhancer.create();
        } else {
            return super.getProxyInternal(loader, interfaces, h);
        }
    }
}
