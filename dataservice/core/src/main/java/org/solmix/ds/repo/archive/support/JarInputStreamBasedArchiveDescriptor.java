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

package org.solmix.ds.repo.archive.support;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.ds.repo.NamedInputStream;
import org.solmix.ds.repo.archive.ArchiveDescriptorFactory;
import org.solmix.ds.repo.archive.ArchiveEntry;
import org.solmix.ds.repo.archive.VisitContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-23
 */

public class JarInputStreamBasedArchiveDescriptor extends AbstractArchiveDescriptor
{

    private static final Logger LOG = LoggerFactory.getLogger(ExplodedArchiveDescriptor.class.getName());

    /**
     * @param archiveDescriptorFactory
     * @param archiveUrl
     * @param entryBasePrefix
     */
    protected JarInputStreamBasedArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix)
    {
        super(archiveDescriptorFactory, archiveUrl, entryBasePrefix);
    }

    @Override
    public void visitArchive(VisitContext context) {
        final JarInputStream jarInputStream;
        try {
            jarInputStream = new JarInputStream(getArchiveUrl().openStream());
        } catch (Exception e) {
            // really should catch IOException but Eclipse is buggy and raise NPE...
            LOG.error("unable to find file" + getArchiveUrl(), e);
            return;
        }

        try {
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                final String jarEntryName = jarEntry.getName();
                if (getEntryBasePrefix() != null && !jarEntryName.startsWith(getEntryBasePrefix())) {
                    continue;
                }

                if (jarEntry.isDirectory()) {
                    continue;
                }

                if (jarEntryName.equals(getEntryBasePrefix())) {
                    // exact match, might be a nested jar entry (ie from jar:file:..../foo.ear!/bar.jar)
                    //
                    // This algorithm assumes that the zipped file is only the URL root (including entry), not
                    // just any random entry
                    try {
                        final JarInputStream subJarInputStream = new JarInputStream(jarInputStream);
                        try {
                            ZipEntry subZipEntry = jarInputStream.getNextEntry();
                            while (subZipEntry != null) {
                                if (!subZipEntry.isDirectory()) {
                                    final String subName = extractName(subZipEntry);
                                    final NamedInputStream namedInputStream = buildByteBasedInputStream(subName, subJarInputStream);

                                    final ArchiveEntry entry = new ArchiveEntry() {

                                        @Override
                                        public String getName() {
                                            return subName;
                                        }

                                        @Override
                                        public String getNameWithinArchive() {
                                            return subName;
                                        }

                                        @Override
                                        public NamedInputStream getNamedInputStream() {
                                            return namedInputStream;
                                        }
                                    };

                                    context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
                                }
                                subZipEntry = jarInputStream.getNextJarEntry();
                            }
                        } finally {
                            subJarInputStream.close();
                        }
                    } catch (Exception e) {
                        throw new java.lang.IllegalArgumentException("Error accessing nested jar", e);
                    }
                } else {
                    final String entryName = extractName(jarEntry);
                    final NamedInputStream namedInputStream = buildByteBasedInputStream(entryName, jarInputStream);

                    final String relativeName = extractRelativeName(jarEntry);

                    final ArchiveEntry entry = new ArchiveEntry() {

                        @Override
                        public String getName() {
                            return entryName;
                        }

                        @Override
                        public String getNameWithinArchive() {
                            return relativeName;
                        }

                        @Override
                        public NamedInputStream getNamedInputStream() {
                            return namedInputStream;
                        }
                    };

                    context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
                }
            }

            jarInputStream.close();
        } catch (IOException ioe) {
            throw new java.lang.IllegalArgumentException(String.format("Error accessing JarInputStream [%s]", getArchiveUrl()), ioe);
        }
    }

}
