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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年11月23日
 */

public class ArchiveUtil
{

    private static final Logger log = LoggerFactory.getLogger(ArchiveUtil.class);

    /**
     * Get the JAR URL of the JAR containing the given entry Method used in a non managed environment
     * 
     * @param url URL pointing to the known file in the JAR
     * @param entry file known to be in the JAR
     * @return the JAR URL
     * @throws IllegalArgumentException if none URL is found
     */
    public static URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException {
        URL jarUrl;
        String file = url.getFile();
        if (!entry.startsWith("/")) {
            entry = "/" + entry;
        }
        file = file.substring(0, file.length() - entry.length());
        if (file.endsWith("!")) {
            file = file.substring(0, file.length() - 1);
        }
        try {
            final String protocol = url.getProtocol();

            if ("jar".equals(protocol) || "wsjar".equals(protocol)) {
                // Original URL is like jar:protocol
                // WebSphere has it's own way
                jarUrl = new URL(file);
                if ("file".equals(jarUrl.getProtocol())) {
                    if (file.indexOf(' ') != -1) {
                        // not escaped, need to voodoo; goes by toURI to escape the path
                        jarUrl = new File(jarUrl.getFile()).toURI().toURL();
                    }
                }
            } else if ("zip".equals(protocol)
            // OC4J prevent ejb.jar access (ie everything without path)
                || "code-source".equals(url.getProtocol())
                // if no wrapping is done
                || "file".equals(protocol)) {
                // we have extracted the zip file, so it should be read as a file
                if (file.indexOf(' ') != -1) {
                    // not escaped, need to voodoo; goes by toURI to escape the path
                    jarUrl = new File(file).toURI().toURL();
                } else {
                    jarUrl = new File(file).toURL();
                }
            } else {
                try {
                    // We reconstruct the URL probably to make it work in some specific environments
                    // Forgot the exact details, sorry (and the Git history does not help)
                    jarUrl = new URL(protocol, url.getHost(), url.getPort(), file);
                }
                // HHH-6442: Arquilian
                catch (final MalformedURLException e) {
                    // Just use the provided URL as-is, likely it has a URLStreamHandler
                    // associated w/ the instance
                    jarUrl = url;
                }
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to determine JAR Url from " + url + ". Cause: " + e.getMessage());
        }
        log.trace("JAR URL from URL Entry: " + url + " >> " + jarUrl);
        return jarUrl;
    }
}
