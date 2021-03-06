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

import java.net.URL;

import org.solmix.ds.repo.archive.ArchiveDescriptor;
import org.solmix.ds.repo.archive.ArchiveDescriptorFactory;
import org.solmix.ds.repo.archive.VisitContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-23
 */

public class JarProtocolArchiveDescriptor implements ArchiveDescriptor
{

    private final ArchiveDescriptor delegateDescriptor;

    public JarProtocolArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL url, String incomingEntry)
    {
        if (incomingEntry != null && incomingEntry.length() > 0) {
            throw new IllegalArgumentException("jar:jar: not supported: " + url);
        }

        final String urlFile = url.getFile();
        final int subEntryIndex = urlFile.lastIndexOf("!");
        if (subEntryIndex == -1) {
            throw new java.lang.IllegalArgumentException("JAR URL does not contain '!/' :" + url);
        }

        final String subEntry;
        if (subEntryIndex + 1 >= urlFile.length()) {
            subEntry = "";
        } else {
            subEntry = urlFile.substring(subEntryIndex + 1);
        }

        final URL fileUrl = archiveDescriptorFactory.getJarURLFromURLEntry(url, subEntry);
        delegateDescriptor = archiveDescriptorFactory.buildArchiveDescriptor(fileUrl, subEntry);
    }

    @Override
    public void visitArchive(VisitContext context) {
        delegateDescriptor.visitArchive(context);
    }

}
