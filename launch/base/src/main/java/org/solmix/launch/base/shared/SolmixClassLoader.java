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

package org.solmix.launch.base.shared;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-17
 */

public class SolmixClassLoader extends URLClassLoader
{

    private final Set<String> loaderPackages;

    SolmixClassLoader(URL[] loaderJars) throws MalformedURLException
    {

        super(loaderJars, SolmixClassLoader.class.getClassLoader());
        Set<String> collectedPackages = new HashSet<String>();
        for (URL loaderJar : loaderJars) {
            JarFile jar = null;
            try {
                jar = new JarFile(new File(loaderJar.toURI()), false);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String entryName = entries.nextElement().getName();
                    if (entryName.endsWith(".class") && !entryName.startsWith("META-INF/") && !entryName.startsWith("javax/")) {
                        String packageName = getPackageName(entryName, '/');
                        if (packageName != null && collectedPackages.add(packageName)) {
                            collectedPackages.add(packageName.replace('/', '.'));
                        }
                    }
                }
            } catch (IOException ioe) {
                // might log or throw, don't know ??
            } catch (URISyntaxException e) {
                // might log or throw, don't know ??
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }

        loaderPackages = collectedPackages;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            if (containsPackage(name, '.')) {
                // finds the class or throws a ClassNotFoundException if
                // the class cannot be found, which is ok, since we only
                // want the class from our jar file, if it contains the
                // package.
                c = findClass(name);
            } else {
                return super.loadClass(name, resolve);
            }
        }

        if (resolve) {
            resolveClass(c);
        }

        return c;
    }

    @Override
    public URL getResource(String name) {

        // if the package of the name is contained in our jar file
        // file, return the resource or nothing
        if (containsPackage(name, '/')) {
            return findResource(name);
        }

        // try parent class loader only after having checked our packages
        return super.getResource(name);
    }

    private String getPackageName(String name, int separator) {
        int speIdx = name.lastIndexOf(separator);
        return (speIdx > 0) ? name.substring(0, speIdx) : null;
    }

    private boolean containsPackage(String name, int separator) {
        String packageName = getPackageName(name, separator);
        return (packageName == null) ? false : loaderPackages.contains(packageName);
    }
}
