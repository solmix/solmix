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
package org.solmix.fmk.context.blueprint;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import org.osgi.framework.Bundle;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-5
 */

public class BundleDelegatingClassLoader extends ClassLoader
{
    private final Bundle bundle;
    private final ClassLoader classLoader;

    public BundleDelegatingClassLoader(Bundle bundle) {
        this(bundle, null);
    }

    public BundleDelegatingClassLoader(Bundle bundle, ClassLoader classLoader) {
        this.bundle = bundle;
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                public Class<?> run() throws ClassNotFoundException {
                    return bundle.loadClass(name);
                }
            });
        } catch (PrivilegedActionException e) {
            Exception cause = e.getException();
          
            if (cause instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)cause;
            } else {
                throw (RuntimeException)cause;
            }
        }    
    }

    @Override
    protected URL findResource(final String name) {
        URL resource = AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return bundle.getResource(name);
            }
        });        
        if (classLoader != null && resource == null) {
            resource = classLoader.getResource(name);
        }
        return resource;
    }

    @Override
    protected Enumeration<URL> findResources(final String name) throws IOException {
        Enumeration<URL> urls;
        try {
            urls =  AccessController.doPrivileged(new PrivilegedExceptionAction<Enumeration<URL>>() {
                @SuppressWarnings("unchecked")
                public Enumeration<URL> run() throws IOException {
                    return bundle.getResources(name);
                }
          
            });
        } catch (PrivilegedActionException e) {
            Exception cause = e.getException();
        
            if (cause instanceof IOException) {
                throw (IOException)cause;
            } else {
                throw (RuntimeException)cause;
            }
        }
      
        if (urls == null) {
            urls = Collections.enumeration(new ArrayList<URL>());
        }
      
        return urls;    
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = findClass(name);
        } catch (ClassNotFoundException cnfe) {
            if (classLoader != null) {
                try {
                    clazz = classLoader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    throw new ClassNotFoundException(name + " from bundle " + bundle.getBundleId() 
                                                     + " (" + bundle.getSymbolicName() + ")", cnfe);
                }
            } else {
                throw new ClassNotFoundException(name + " from bundle " + bundle.getBundleId() 
                                                 + " (" + bundle.getSymbolicName() + ")", cnfe);
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    public Bundle getBundle() {
        return bundle;
    }

}
