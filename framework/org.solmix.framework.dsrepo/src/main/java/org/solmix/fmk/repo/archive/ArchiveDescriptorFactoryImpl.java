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
package org.solmix.fmk.repo.archive;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.solmix.api.repo.archive.ArchiveDescriptor;
import org.solmix.api.repo.archive.ArchiveDescriptorFactory;
import org.solmix.commons.util.ArchiveUtil;
import org.solmix.commons.util.DataUtil;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-23
 */

public class ArchiveDescriptorFactoryImpl implements ArchiveDescriptorFactory
{

    public static final ArchiveDescriptorFactoryImpl INSTANCE = new ArchiveDescriptorFactoryImpl();

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.archive.ArchiveDescriptorFactory#buildArchiveDescriptor(java.net.URL)
     */
    @Override
    public ArchiveDescriptor buildArchiveDescriptor(URL url) {
        return buildArchiveDescriptor(url,"");
    }
    @Override
    public ArchiveDescriptor buildArchiveDescriptor(URL url,String entry) {
        final String protocol = url.getProtocol();
        if ( "jar".equals( protocol ) ) {
              return new JarProtocolArchiveDescriptor( this, url, entry );
        }
        else if ( DataUtil.isNullOrEmpty( protocol )
                    || "file".equals( protocol )
                    || "vfszip".equals( protocol )
                    || "vfsfile".equals( protocol ) ) {
              final File file;
              try {
                    final String filePart = url.getFile();
                    if ( filePart != null && filePart.indexOf( ' ' ) != -1 ) {
                          //unescaped (from the container), keep as is
                          file = new File( url.getFile() );
                    }
                    else {
                          file = new File( url.toURI().getSchemeSpecificPart() );
                    }

                    if ( ! file.exists() ) {
                          throw new IllegalArgumentException(
                                      String.format(
                                                  "File [%s] referenced by given URL [%s] does not exist",
                                                  filePart,
                                                  url.toExternalForm()
                                      )
                          );
                    }
              }
              catch (URISyntaxException e) {
                    throw new IllegalArgumentException(
                                "Unable to visit JAR " + url + ". Cause: " + e.getMessage(), e
                    );
              }

              if ( file.isDirectory() ) {
                    return new ExplodedArchiveDescriptor( this, url, entry );
              }
              else {
                    return new JarFileBasedArchiveDescriptor( this, url, entry );
              }
        }
        else {
              //let's assume the url can return the jar as a zip stream
              return new JarInputStreamBasedArchiveDescriptor( this, url, entry );
        }
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.archive.ArchiveDescriptorFactory#getJarURLFromURLEntry(java.net.URL, java.lang.String)
     */
    @Override
    public URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException {
        return ArchiveUtil.getJarURLFromURLEntry(url, entry);
    }
}
