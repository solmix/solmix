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

package org.solmix.fmk.repo.archive;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.repo.NamedInputStream;
import org.solmix.api.repo.archive.ArchiveDescriptor;
import org.solmix.api.repo.archive.ArchiveDescriptorFactory;
import org.solmix.api.repo.archive.ArchiveEntry;
import org.solmix.api.repo.archive.VisitContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年11月23日
 */

public class ExplodedArchiveDescriptor extends AbstractArchiveDescriptor implements ArchiveDescriptor
{
    private static final Logger LOG = LoggerFactory.getLogger(ExplodedArchiveDescriptor.class.getName());

    /**
     * @param archiveDescriptorFactory
     * @param archiveUrl
     * @param entryBasePrefix
     */
    protected ExplodedArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix)
    {
        super(archiveDescriptorFactory, archiveUrl, entryBasePrefix);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.archive.ArchiveDescriptor#visitArchive(org.solmix.api.repo.archive.VisitContext)
     */
    @Override
    public void visitArchive(VisitContext context) {
        final File rootDirectory = resolveRootDirectory();
        if (rootDirectory == null) {
            return;
        }

        if (rootDirectory.isDirectory()) {
            processDirectory(rootDirectory, null, context);
        } else {
            // assume zipped file
            processZippedRoot(rootDirectory, context);
        }
    }

    private File resolveRootDirectory() {
        final File archiveUrlDirectory;
        try {
            final String filePart = getArchiveUrl().getFile();
            if (filePart != null && filePart.indexOf(' ') != -1) {
                // unescaped (from the container), keep as is
                archiveUrlDirectory = new File(filePart);
            } else {
                archiveUrlDirectory = new File(getArchiveUrl().toURI().getSchemeSpecificPart());
            }
        } catch (URISyntaxException e) {
            LOG.error(getArchiveUrl().toString(), e);
            return null;
        }

        if (!archiveUrlDirectory.exists()) {
            LOG.error(getArchiveUrl().toString()+" is not exists.");
            return null;
        }
        if (!archiveUrlDirectory.isDirectory()) {
            LOG.error(getArchiveUrl().toString()+" is not a directory.");
            return null;
        }

        final String entryBase = getEntryBasePrefix();
        if (entryBase != null && entryBase.length() > 0 && !"/".equals(entryBase)) {
            return new File(archiveUrlDirectory, entryBase);
        } else {
            return archiveUrlDirectory;
        }
    }

    private void processDirectory(File directory, String path, VisitContext context) {
        if (directory == null) {
            return;
        }

        final File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        path = path == null ? "" : path + "/";
        for (final File localFile : files) {
            if (!localFile.exists()) {
                // should never happen conceptually, but...
                continue;
            }

            if (localFile.isDirectory()) {
                processDirectory(localFile, path + localFile.getName(), context);
                continue;
            }

            final String name = localFile.getAbsolutePath();
            final String relativeName = path + localFile.getName();
            final NamedInputStream namedInputStream = new NamedFileInputStream(name, localFile);

            final ArchiveEntry entry = new ArchiveEntry() {

                @Override
                public String getName() {
                    return name;
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

    private void processZippedRoot(File rootFile, VisitContext context) {
        try {
            final JarFile jarFile = new JarFile(rootFile);
            final Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) {
                    continue;
                }

                final String name = extractName(zipEntry);
                final String relativeName = extractRelativeName(zipEntry);
                final NamedInputStream inputStreamAccess;
                try {
                    inputStreamAccess = buildByteBasedInputStream(name, jarFile.getInputStream(zipEntry));
                } catch (IOException e) {
                    throw new java.lang.IllegalStateException(String.format("Unable to access stream from jar file [%s] for entry [%s]",
                        jarFile.getName(), zipEntry.getName()));
                }

                final ArchiveEntry entry = new ArchiveEntry() {

                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public String getNameWithinArchive() {
                        return relativeName;
                    }

                    @Override
                    public NamedInputStream getNamedInputStream() {
                        return inputStreamAccess;
                    }
                };
                context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
            }
        } catch (IOException e) {
            throw new java.lang.IllegalStateException("Error accessing jar file [" + rootFile.getAbsolutePath() + "]", e);
        }
    }

}
