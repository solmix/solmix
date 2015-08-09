/**
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

package org.solmix.runtime.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class ExtensionInfo {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionInfo.class);

    protected String className;

    protected ClassLoader classloader;

    protected Class<?> clazz;

    protected Class<?> intf;

    protected String interfaceName;

    protected boolean deferred;

    protected Collection<String> namespaces = new ArrayList<String>();

    protected Object args[];

    protected volatile Object obj;

    protected boolean optional;

    protected boolean notFound;

    /**
     * @param e
     */
    public ExtensionInfo(ExtensionInfo ext) {
        className = ext.className;
        interfaceName = ext.interfaceName;
        deferred = ext.deferred;
        namespaces = ext.namespaces;
        obj = ext.obj;
        clazz = ext.clazz;
        intf = ext.intf;
        classloader = ext.classloader;
        args = ext.args;
        optional = ext.optional;
    }

    /**
     * @param loader
     */
    public ExtensionInfo(ClassLoader loader) {
        classloader = loader;
    }

    /**
     * 
     */
    public ExtensionInfo() {
    }

    /**
     * @param class1
     */
    public ExtensionInfo(Class<?> clazz) {
        this.clazz = clazz;
        className = clazz.getName();
        classloader = clazz.getClassLoader();
    }

    /**
     * @return
     */
    public String getName() {
        return className;
    }

    /**
     * @return
     */
    public ExtensionInfo cloneNoObject() {
        ExtensionInfo ext = new ExtensionInfo(this);
        ext.obj = null;
        ext.clazz = null;
        ext.intf = null;
        return ext;
    }

    public String getClassname() {
        return className;
    }

    public void setClassname(String i) {
        setClassname0(i);
    }

    private void setClassname0(String i) {
        clazz = null;
        notFound = false;
        className = i;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String i) {
        interfaceName = i;
        notFound = false;
    }

    public boolean isDeferred() {
        return deferred;
    }

    public void setDeferred(boolean d) {
        deferred = d;
    }

    public Collection<String> getNamespaces() {
        return namespaces;
    }

    public void setArgs(Object a[]) {
        args = a;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("class: ");
        buf.append(className);
        buf.append(", interface: ");
        buf.append(interfaceName);
        buf.append(", interface: ");
        buf.append(interfaceName);

        buf.append(deferred ? "true" : "false");
        buf.append(", namespaces: (");
        int n = 0;
        for (String ns : namespaces) {
            if (n > 0) {
                buf.append(", ");
            }
            buf.append(ns);
            n++;
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * @return
     */
    public Object getLoadedObject() {
        return obj;
    }

    public void setLoadedObject(Object o){
        this.obj=o;
    }
    /**
     * @param loader
     * @return
     */
    public Class<?> loadInterface(ClassLoader loader) {
        if (intf != null || notFound) {
            return intf;
        }
        intf = tryClass(interfaceName, loader);
        return intf;
    }

    protected Class<?> tryClass(String name, ClassLoader cl) {
        Throwable origEx = null;
        if (classloader != null) {
            try {
                return classloader.loadClass(name);
            } catch (Throwable nex) {
                // ignore, fall into the stuff below
                // save the exception though as this is likely the important one
                origEx = nex;
            }
        }
        try {
            return cl.loadClass(name);
        } catch (Throwable ex) {
            try {
                // using the extension classloader as a fallback
                return this.getClass().getClassLoader().loadClass(name);
            } catch (Throwable nex) {
                notFound = true;
                if (!optional) {
                    if (origEx != null) {
                        ex = origEx;
                    }
                    throw new ExtensionException(ex);
                }
            }
        }
        return null;
    }

    public Class<?> getClassObject() {
        return getClassObject(classloader);
    }
    /**
     * @param loader
     * @return
     */
    public Class<?> getClassObject(ClassLoader loader) {
        if (notFound) {
            return null;
        }
        if (clazz == null) {
            clazz = tryClass(className, loader);
        }
        return clazz;
    }

    /**
     * @param loader
     * @param container
     * @return
     */
    public Object load(ClassLoader loader, Container container) {
        if (obj != null) {
            return obj;
        }
        Class<?> cls = getClassObject(loader);
        try {
            if (notFound) {
                return null;
            }
            try {
                // if there is a container constructor, use it.
                if (container != null && args == null) {
                    Constructor<?> con = cls.getConstructor(Container.class);
                    obj = con.newInstance(container);
                    return obj;
                } else if (container != null && args != null) {
                    Constructor<?> con;
                    boolean noc = false;
                    try {
                        con = cls.getConstructor(Container.class,
                            Object[].class);
                    } catch (Exception ex) {
                        con = cls.getConstructor(Object[].class);
                        noc = true;
                    }
                    if (noc) {
                        obj = con.newInstance(args);
                    } else {
                        obj = con.newInstance(container, args);
                    }
                    return obj;
                } else if (args != null) {
                    Constructor<?> con = cls.getConstructor(Object[].class);
                    obj = con.newInstance(args);
                    return obj;
                }
            } catch (InvocationTargetException ex) {
                throw new ExtensionException(
                    "Problem Creating Extension Class", ex.getCause());
            } catch (InstantiationException ex) {
                throw new ExtensionException(
                    "Problem Creating Extension Class", ex);
            } catch (SecurityException ex) {
                throw new ExtensionException(
                    "Problem Creating Extension Class", ex);
            } catch (NoSuchMethodException e) {
                // ignore
            }
            obj = cls.getConstructor().newInstance();
        } catch (ExtensionException ex) {
            notFound = true;
            if (!optional) {
                throw ex;
            } else {
                LOG.info("Could not load optional extension " + getName(), ex);
            }
        } catch (InvocationTargetException ex) {
            notFound = true;
            if (!optional) {
                throw new ExtensionException(
                    "PROBLEM_CREATING_EXTENSION_CLASS", ex.getCause());
            } else {
                LOG.info("Could not load optional extension " + getName(), ex);
            }
        } catch (NoSuchMethodException ex) {
            notFound = true;
            List<Object> a = new ArrayList<Object>();
            if (container != null) {
                a.add(container);
            }
            if (args != null) {
                a.add(args);
            }
            if (!optional) {
                throw new ExtensionException("PROBLEM_FINDING_CONSTRUCTOR", ex);
            } else {
                LOG.info("Could not load optional extension " + getName(), ex);
            }
        } catch (Throwable e) {
            notFound = true;
            if (!optional) {
                throw new ExtensionException(
                    "PROBLEM_CREATING_EXTENSION_CLASS", e);
            } else {
                LOG.info("Could not load optional extension " + getName(), e);
            }
        }
        return obj;
    }

    public void setOptional(boolean b) {
        optional = b;
    }

    public boolean isOptional() {
        return optional;
    }

}
