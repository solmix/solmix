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

package org.solmix.fmk.engine;

/**
 * The <code>MimeTypeProvider</code> interface defines an API for services which are asked for MIME type mappings for
 * unknown MIME types or extensions.
 * <p>
 * It is important to understand, that services registered with this interface are only called as a last resort and that
 * the first service asked and replying with a non-<code>null</code> answer wins. Specifically if a MIME type mapping is
 * configured by default or as an extension to the MIME type service, MimeTypeProvider services are not queried.
 * <p>
 * This interface may be implemented by bundles wishing to provide control over how extensions are mapped to MIME types
 * and vice-versa.
 */
public interface MimeTypeProvider
{

    /**
     * Returns the MIME type of the extension of the given <code>name</code>. The extension is the part of the name
     * after the last dot. If the name does not contain a dot, the name as a whole is assumed to be the extension.
     * 
     * @param name The name for which the MIME type is to be returned.
     * @return The MIME type for the extension of the name. If the extension cannot be mapped to a MIME type or
     *         <code>name</code> is <code>null</code>, <code>null</code> is returned.
     * @see #getExtension(String)
     */
    String getMimeType(String name);

    /**
     * Returns the primary name extension to which the given <code>mimeType</code> maps. The returned extension must map
     * to the given <code>mimeType</code> when fed to the {@link #getMimeType(String)} method. In other words, the
     * expression <code>mimeType.equals(getMimeType(getExtension(mimeType)))</code> must always be <code>true</code> for
     * any non-<code>null</code> MIME type.
     * <p>
     * A MIME type may be mapped to multiple extensions (e.g. <code>text/plain</code> to <code>txt</code>,
     * <code>log</code>, ...). This method is expected to returned one of those extensions. It is up to the
     * implementation to select an appropriate extension if multiple mappings exist for a single MIME type.
     * 
     * @param mimeType The MIME type whose primary extension is requested.
     * @return A extension which maps to the given MIME type or <code>null</code> if no such mapping exists.
     * @see #getMimeType(String)
     */
    String getExtension(String mimeType);

}
