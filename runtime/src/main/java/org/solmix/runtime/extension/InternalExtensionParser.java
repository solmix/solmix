/*
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class InternalExtensionParser {

    private static Pattern colonPattern = Pattern.compile(":");

    private static final Logger LOG = LoggerFactory.getLogger(InternalExtensionParser.class);

    final ClassLoader loader;

    public InternalExtensionParser(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * @param nextElement
     * @return
     */
    public List<ExtensionInfo> getExtensions(URL url) {
        InputStream is = null;
        try {
            String name = url.getFile();
            name = name.substring(name.lastIndexOf("/") + 1);
            is = url.openStream();
            return getExtensions(is, name);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return new ArrayList<ExtensionInfo>();
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

    /**
     * @param is
     * @return
     * @throws IOException
     */
    public List<ExtensionInfo> getExtensions(InputStream is, String inf)
        throws IOException {
        List<ExtensionInfo> extensions = new ArrayList<ExtensionInfo>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
            "UTF-8"));
        String line = reader.readLine();
        while (line != null) {
            final ExtensionInfo extension = getExtensionFromTextLine(line, inf);
            if (extension != null) {
                extensions.add(extension);
            }
            line = reader.readLine();
        }
        return extensions;
    }

    /**
     * META-INF/solmix/extensions #comments class:interface:deferend:optional
     * 
     * Just like: class class:interface class::deferend:optional
     * class:::optional
     * 
     * @param line
     * @return
     */
    private ExtensionInfo getExtensionFromTextLine(String line, String inf) {
        line = line.trim();
        if (line.length() == 0 || line.charAt(0) == '#') {
            return null;
        }
        final ExtensionInfo ext = new ExtensionInfo(loader);
        String[] parts = colonPattern.split(line, 0);
        ext.setClassname(parts[0]);
        if (ext.getClassname() == null) {
            return null;
        }
        if (parts.length >= 2) {
            String interfaceName = parts[1];
            if (interfaceName != null && "".equals(interfaceName)) {
                interfaceName = null;
            }
            ext.setInterfaceName(interfaceName);
        }
        if (parts.length >= 3) {
            ext.setDeferred(Boolean.parseBoolean(parts[2]));
        }
        if (parts.length >= 4) {
            ext.setOptional(Boolean.parseBoolean(parts[3]));
        }
        return ext;
    }

}
