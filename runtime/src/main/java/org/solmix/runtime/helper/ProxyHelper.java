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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月6日
 */

public class ProxyHelper
{
    static final ProxyHelper HELPER;
    static {
        ProxyHelper theHelper = null;
        try {
            theHelper = new CglibProxyHelper();
        } catch (Throwable ex) {
            theHelper = new ProxyHelper();
        }
        HELPER = theHelper;
    }

    protected Object getProxyInternal(ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler) {
        ClassLoader combinedLoader = getClassLoaderForInterfaces(loader, interfaces);
        return Proxy.newProxyInstance(combinedLoader, interfaces, handler);
    }

    /**
     * Return a classloader that can see all the given interfaces If the given loader can see all interfaces
     * then it is used. If not then a combined classloader of all interface classloaders is returned.
     * 
     * @param loader use supplied class loader
     * @param interfaces
     * @return classloader that sees all interfaces
     */
    private ClassLoader getClassLoaderForInterfaces(ClassLoader loader, Class<?>[] interfaces) {
        if (canSeeAllInterfaces(loader, interfaces)) {
            return loader;
        }
        ProxyClassLoader combined = new ProxyClassLoader(loader, interfaces);
        for (Class<?> currentInterface : interfaces) {
            combined.addLoader(currentInterface.getClassLoader());
        }
        return combined;
    }

    private boolean canSeeAllInterfaces(ClassLoader loader, Class<?>[] interfaces) {
        for (Class<?> currentInterface : interfaces) {
            String ifName = currentInterface.getName();
            try {
                Class<?> ifClass = Class.forName(ifName, true, loader);
                if (ifClass != currentInterface) {
                    return false;
                }
                //we need to check all the params/returns as well as the Proxy creation 
                //will try to create methods for all of this even if they aren't used
                //by the client and not available in the clients classloader
                for (Method m : ifClass.getMethods()) {
                    Class.forName(m.getReturnType().getName(), true, loader);
                    for (Class<?> p : m.getParameterTypes()) {
                        Class.forName(p.getName(), true, loader);
                    }
                }
            } catch (NoClassDefFoundError e) {
                return false;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    public static Object getProxy(ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler) {
        return HELPER.getProxyInternal(loader, interfaces, handler);
    }
}
