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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;

import org.solmix.api.repo.NamedInputStream;
import org.solmix.api.repo.archive.ArchiveDescriptor;
import org.solmix.api.repo.archive.ArchiveDescriptorFactory;
import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.IOUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-23
 */

public abstract class AbstractArchiveDescriptor implements ArchiveDescriptor
{

    private final ArchiveDescriptorFactory archiveDescriptorFactory;

    private final URL archiveUrl;

    private final String entryBasePrefix;

    protected AbstractArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix)
    {
        this.archiveDescriptorFactory = archiveDescriptorFactory;
        this.archiveUrl = archiveUrl;
        this.entryBasePrefix = normalizeEntryBasePrefix(entryBasePrefix);
    }

    private static String normalizeEntryBasePrefix(String entryBasePrefix) {
        if (DataUtil.isNullOrEmpty(entryBasePrefix) || entryBasePrefix.length() == 1) {
            return null;
        }

        return entryBasePrefix.startsWith("/") ? entryBasePrefix.substring(1) : entryBasePrefix;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected ArchiveDescriptorFactory getArchiveDescriptorFactory() {
        return archiveDescriptorFactory;
    }

    protected URL getArchiveUrl() {
        return archiveUrl;
    }

    protected String getEntryBasePrefix() {
        return entryBasePrefix;
    }

    protected String extractRelativeName(ZipEntry zipEntry) {
        final String entryName = extractName(zipEntry);
        return entryBasePrefix == null ? entryName : entryName.substring(entryBasePrefix.length());
    }

    protected String extractName(ZipEntry zipEntry) {
        return normalizePathName(zipEntry.getName());
    }

    protected String normalizePathName(String pathName) {
        return pathName.startsWith("/") ? pathName.substring(1) : pathName;
    }

    protected NamedInputStream buildByteBasedInputStream(final String name, InputStream inputStream) throws IOException {
        // because of how jar InputStreams work we need to extract the bytes immediately. However, we
        // do delay the creation of the ByteArrayInputStreams until needed
        byte[] bytes = IOUtil.getBytesFromInputStream(inputStream);
        return new NamedByteArrayInputStream(name, bytes);

    }

}
