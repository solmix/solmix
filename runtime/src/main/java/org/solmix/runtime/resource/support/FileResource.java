/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.runtime.resource.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.springframework.core.io.FileSystemResource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月23日
 */

public class FileResource extends AbstractInputStreamResource
{

    private final File file;

    private final String path;

    public FileResource(File file)
    {
        Assert.assertNotNull(file, "File must not be null");
        this.file = file;
        this.path = FileUtils.normalizeAbsolutePath(file.getPath(),true);
    }

    public FileResource(String path)
    {
        Assert.assertNotNull(path, "Path must not be null");
        this.file = new File(path);
        this.path = FileUtils.normalizeAbsolutePath(path,true);
    }

    /**
     * Return the file path for this resource.
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * This implementation returns whether the underlying file exists.
     * 
     * @see java.io.File#exists()
     */
    @Override
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * This implementation checks whether the underlying file is marked as readable (and corresponds to an actual file
     * with content, not to a directory).
     * 
     * @see java.io.File#canRead()
     * @see java.io.File#isDirectory()
     */
    @Override
    public boolean isReadable() {
        return (this.file.canRead() && !this.file.isDirectory());
    }

    /**
     * This implementation opens a FileInputStream for the underlying file.
     * 
     * @see java.io.FileInputStream
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    /**
     * This implementation returns a URL for the underlying file.
     * 
     * @see java.io.File#toURI()
     */
    @Override
    public URL getURL() throws IOException {
        return this.file.toURI().toURL();
    }

    /**
     * This implementation returns a URI for the underlying file.
     * 
     * @see java.io.File#toURI()
     */
    @Override
    public URI getURI() throws IOException {
        return this.file.toURI();
    }

    /**
     * This implementation returns the underlying File reference.
     */
    @Override
    public File getFile() {
        return this.file;
    }

    /**
     * This implementation creates a FileSystemResource, applying the given path relative to the path of the underlying
     * file of this resource descriptor.
     * 
     * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
     */
    @Override
    public FileResource createRelative(String relativePath) {
        String pathToUse = FileUtils.applyRelativePath(this.path, relativePath);
        return new FileResource(pathToUse);
    }

    /**
     * This implementation returns the name of the file.
     * 
     * @see java.io.File#getName()
     */
    @Override
    public String getFilename() {
        return this.file.getName();
    }

    /**
     * This implementation returns a description that includes the absolute path of the file.
     * 
     * @see java.io.File#getAbsolutePath()
     */
    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    public boolean isWritable() {
        return (this.file.canWrite() && !this.file.isDirectory());
    }

    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.file);
    }

    /**
     * This implementation compares the underlying File references.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this || (obj instanceof FileSystemResource && this.path.equals(((FileResource) obj).path)));
    }

    /**
     * This implementation returns the hash code of the underlying File reference.
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}
