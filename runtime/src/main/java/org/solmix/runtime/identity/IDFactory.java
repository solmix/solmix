/**
 * Copyright (container) 2014 The Solmix Project
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

package org.solmix.runtime.identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.SystemPropertyAction;
import org.solmix.runtime.identity.support.GUID;
import org.solmix.runtime.identity.support.GUIDNamespace;
import org.solmix.runtime.identity.support.LongNamespace;
import org.solmix.runtime.identity.support.StringNamespace;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public final class IDFactory implements IIDFactory {

    public static final String DEFAULT_IDENTITY_LOCATION = "META-INF/solmix/identity";

    public static final String PROP_IDENTITY_LOCATION = "solmix.identity.location";

    private static final Logger LOG = LoggerFactory.getLogger(IDFactory.class);

    private static IDFactory instance = null;

    private static Hashtable<String, Namespace> namespaces = new Hashtable<String, Namespace>();
    
    static {
        instance = new IDFactory();
        try {
            initLoadAllNamespace();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Load identity Namespace Error", e);
        }
    }
    
    private IDFactory() {
    }
    
    public static IDFactory getDefault() {
        return instance;
    }

    private  static void initLoadAllNamespace() throws Exception {
        String location = SystemPropertyAction.getProperty(
            PROP_IDENTITY_LOCATION, DEFAULT_IDENTITY_LOCATION);
        ClassLoader cl = IDFactory.class.getClassLoader();
        Enumeration<URL> urls = cl.getResources(location);
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Load identity namespace from :" + url.getPath());
            }
            InputStream is;
            String inf = url.getFile();
            try {
                is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {

                    @Override
                    public InputStream run() throws Exception {
                        return url.openStream();
                    }
                });
            } catch (PrivilegedActionException pae) {
                throw (IOException) pae.getException();
            }
            try {
                List<Namespace> nss = new IdentityExtensionParser(cl).getNamespace(
                    is, inf);
                for (Namespace ns : nss) {
                    addNamespace0(ns);
                }
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

    public final static Namespace addNamespace0(Namespace namespace) {
        if (namespace == null) {
            return null;
        }
        return namespaces.put(namespace.getName(), namespace);
    }

    public final static Namespace removeNamespace0(Namespace n) {
        if (n == null) {
            return null;
        }
        return namespaces.remove(n.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#containsNamespace(org.solmix.runtime.identity.Namespace)
     */
    @Override
    public boolean containsNamespace(Namespace namespace)
        throws SecurityException {
        if (namespace == null) {
            return false;
        }
        return namespaces.containsKey(namespace.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#getNamespaces()
     */
    @Override
    public List<Namespace> getNamespaces() throws SecurityException {
        return new ArrayList<Namespace>(namespaces.values());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#getNamespace(org.solmix.runtime.identity.Namespace)
     */
    @Override
    public Namespace getNamespace(Namespace namespace) throws SecurityException {
        if (namespace == null) {
            return null;
        }
        return namespaces.get(namespace.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#getNamespaceByName(java.lang.String)
     */
    @Override
    public Namespace getNamespaceByName(String name) throws SecurityException {
        return namespaces.get(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createGUID()
     */
    @Override
    public ID createGUID() throws IDCreateException {
        return createGUID(GUID.DEFAULT_BYTE_LENGTH);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createGUID(int)
     */
    @Override
    public ID createGUID(int length) throws IDCreateException {
        return createID(new GUIDNamespace(),
            new Integer[] {Integer.valueOf(length) });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createID(org.solmix.runtime.identity.Namespace,
     *      java.lang.Object[])
     */
    @Override
    public ID createID(Namespace n, Object[] args) throws IDCreateException {
        Namespace ns = getNamespace(n);
        if (ns == null) {
            throw new IDCreateException("Namespace " + n.getName()
                + " not found", null);
        }
        return ns.createID(args);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createID(java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public ID createID(String namespaceName, Object[] args)
        throws IDCreateException {
        Namespace n = getNamespaceByName(namespaceName);
        if (n == null) {
            throw new IDCreateException("Namespace " + namespaceName
                + " not found");
        }
        return createID(n, args);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createID(org.solmix.runtime.identity.Namespace,
     *      java.lang.String)
     */
    @Override
    public ID createID(Namespace namespace, String uri)
        throws IDCreateException {
        return createID(namespace, new Object[] {uri });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createID(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public ID createID(String namespace, String uri) throws IDCreateException {
        return createID(namespace, new Object[] {uri });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createStringID(java.lang.String)
     */
    @Override
    public ID createStringID(String idString) throws IDCreateException {
        if (idString == null) {
            throw new IDCreateException("StringID cannot be null");
        }
        return createID(new StringNamespace(), new String[] {idString });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#createLongID(long)
     */
    @Override
    public ID createLongID(long l) throws IDCreateException {
        return createID(new LongNamespace(), new Long[] {Long.valueOf(l) });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IDFactory#removeNamespace(org.solmix.runtime.identity.Namespace)
     */
    @Override
    public Namespace removeNamespace(Namespace namespace)
        throws SecurityException {
        if (namespace == null) {
            return null;
        }
        return namespaces.remove(namespace.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.IIDFactory#addNamespace(org.solmix.runtime.identity.Namespace)
     */
    @Override
    public Namespace addNamespace(Namespace namespace) throws SecurityException {
        if (namespace == null) {
            return null;
        }
        return addNamespace0(namespace);
    }

}
