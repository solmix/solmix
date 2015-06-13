/**
 * Copyright (c) 2014 The Solmix Project
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Reflection;

/**
 * Identity 扩展。
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class IdentityExtensionParser {

    private static final Logger LOG = LoggerFactory.getLogger(IdentityExtensionParser.class);
    final ClassLoader loader;
    IdentityExtensionParser(ClassLoader loader) {
        this.loader = loader;
    }

    public List<Namespace> getNamespace(URL url) {
        InputStream is = null;
        try {
            String name = url.getFile();
            name = name.substring(name.lastIndexOf("/") + 1);
            is = url.openStream();
            return getNamespace(is, name);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return new ArrayList<Namespace>();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    public List<Namespace> getNamespace(InputStream is, String inf)
        throws IOException {
        List<Namespace> extensions = new ArrayList<Namespace>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
            "UTF-8"));
        String line = reader.readLine();
        try {
            while (line != null) {
                final Namespace extension = getNamespaceFromTextLine(line, inf);
                if (extension != null) {
                    extensions.add(extension);
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return extensions;
    }

    /**
     * META-INF/solmix/identity #comments class
     * 
     * Just like: class class:interface class::deferend:optional
     * class:::optional
     * 
     * @param line
     * @return
     * @throws Exception
     * @throws ClassNotFoundException
     */
    private Namespace getNamespaceFromTextLine(String line, String inf)
        throws ClassNotFoundException, Exception {
        line = line.trim();
        if (line.length() == 0 || line.charAt(0) == '#') {
            return null;
        }
        final String classname = line;
        Object ns = null;
        try {
            ns = Reflection.newInstance(loader.loadClass(classname));
        } catch (ClassNotFoundException e) {
            LOG.warn("Namespace class not found ", e);
        }
        if (ns != null) {
            return (Namespace) ns;
        } else {
            return null;
        }
    }
}
