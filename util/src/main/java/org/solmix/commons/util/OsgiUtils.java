/*
 *  Copyright 2012 The Solmix Project
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

import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-5
 */

public final class OsgiUtils
{

    public static Object getService(BundleContext bundleContext,String serviceName) {
        ServiceReference<?> ref = bundleContext.getServiceReference(serviceName);
        return ref == null ? null : bundleContext.getService(ref);
    }
    public static <S> S getService(BundleContext bundleContext,Class<S> clz) {
        ServiceReference<S> ref = bundleContext.getServiceReference(clz);
        return ref == null ? null : bundleContext.getService(ref);
    }
    
    /**
     * 在bundle外部获取指定bundle的classloader的方法
     * 1.查找bundle下所有.class文件
     * 2.根据class文件转化为java类，加载任意的java类
     * 3.根据java类获取其classloader
     * @param bundle
     * @return
     */
    public static ClassLoader getBundleClassLoader(Bundle bundle) {
        Enumeration<URL> classFileEntries = bundle.findEntries("/", "*.class",true);
        if (classFileEntries == null || !classFileEntries.hasMoreElements()) {
            throw new RuntimeException(String.format("Bundle[%s]no include java class！",bundle.getSymbolicName()));
        }
        URL url = classFileEntries.nextElement();
        String bundleOneClassName = url.getPath();
        bundleOneClassName = bundleOneClassName.replace("/", ".").substring(0,
                bundleOneClassName.lastIndexOf("."));
        while (bundleOneClassName.startsWith(".")) {
            bundleOneClassName = bundleOneClassName.substring(1);
        }
        Class<?> bundleOneClass = null;
        try {
            bundleOneClass = bundle.loadClass(bundleOneClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bundleOneClass.getClassLoader();
    }
}
