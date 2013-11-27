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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.solmix.api.repo.NamedInputStream;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年11月23日
 */

public class NamedFileInputStream implements NamedInputStream
{

    private final String name;

    private final File file;

    public NamedFileInputStream(String name, File file)
    {
        this.name = name;
        this.file = file;
        if (!file.exists()) {
            throw new java.lang.IllegalStateException("File must exist : " + file.getAbsolutePath());
        }
    }

    @Override
    public String getStreamName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.NamedInputStream#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // should never ever ever happen, but...
            throw new java.lang.IllegalArgumentException(
                "File believed to exist based on File.exists threw error when passed to FileInputStream ctor", e);
        }
    }

}
