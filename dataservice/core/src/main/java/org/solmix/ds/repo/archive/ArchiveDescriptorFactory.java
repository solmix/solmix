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
package org.solmix.ds.repo.archive;

import java.net.URL;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年11月23日
 */

public interface ArchiveDescriptorFactory
{
    
    public ArchiveDescriptor buildArchiveDescriptor(URL url);
    public URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException;
    /**
     * @param fileUrl
     * @param subEntry
     * @return
     */
    public ArchiveDescriptor buildArchiveDescriptor(URL fileUrl, String entry);
}
