/*
 * SOLMIX PROJECT
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


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-3
 */

public class ClassLoaderUtil
{
    
    public static URL getResource(ClassLoader loader,String resourceName, Class<?> callingClass) {
        URL url=null;
        if(loader!=null){
            url=  loader.getResource(resourceName);
            if(url!=null)
                return url;
            else
                return getResource(resourceName,callingClass);
        }else{
            return getResource(resourceName,callingClass);
        }
    }
    /**
     * Load a given resource. <p/> This method will try to load the resource
     * using the following methods (in order):
     * <ul>
     * <li>From Thread.currentThread().getContextClassLoader()
     * <li>From ClassLoaderUtil.class.getClassLoader()
     * <li>callingClass.getClassLoader()
     * </ul>
     * 
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    public static URL getResource(String resourceName, Class<?> callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null && resourceName.startsWith("/")) {
            //certain classloaders need it without the leading /
            url = Thread.currentThread().getContextClassLoader()
                .getResource(resourceName.substring(1));
        }

        ClassLoader cluClassloader = ClassLoaderUtil.class.getClassLoader();
        if (cluClassloader == null) {
            cluClassloader = ClassLoader.getSystemClassLoader();
        }
        if (url == null) {
            url = cluClassloader.getResource(resourceName);
        }
        if (url == null && resourceName.startsWith("/")) {
            //certain classloaders need it without the leading /
            url = cluClassloader.getResource(resourceName.substring(1));
        }

        if (url == null&&callingClass!=null) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                url = cl.getResource(resourceName);
            }
            if(url==null)
                url = callingClass.getResource(resourceName); 
        }
        
        if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
            return getResource('/' + resourceName, callingClass);
        }

        return url;
    }
    /**
     * Load a class with a given name. <p/> It will try to load the class in the
     * following order:
     * <ul>
     * <li>From Thread.currentThread().getContextClassLoader()
     * <li>Using the basic Class.forName()
     * <li>From ClassLoaderUtil.class.getClassLoader()
     * <li>From the callingClass.getClassLoader()
     * </ul>
     * 
     * @param className The name of the class to load
     * @param callingClass The Class object of the calling object
     * @throws ClassNotFoundException If the class cannot be found anywhere.
     */
    public static Class<?> loadClass(String className, Class<?> callingClass)
        throws ClassNotFoundException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (cl != null) {
                return cl.loadClass(className);
            }            
        } catch (ClassNotFoundException e) {
            //ignore
        }
        return loadClass2(className, callingClass);
    }
    public static <T> Class<? extends T> loadClass(String className, Class<?> callingClass, Class<T> type)
        throws ClassNotFoundException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (cl != null) {
                return cl.loadClass(className).asSubclass(type);
            }            
        } catch (ClassNotFoundException e) {
            //ignore
        }
        return loadClass2(className, callingClass).asSubclass(type);
    }
    private static Class<?> loadClass2(String className, Class<?> callingClass)
        throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            try {
                if (ClassLoaderUtil.class.getClassLoader() != null) {
                    return ClassLoaderUtil.class.getClassLoader().loadClass(className);
                }
            } catch (ClassNotFoundException exc) {
                if (callingClass != null && callingClass.getClassLoader() != null) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
            throw ex;
        }
    }
}
