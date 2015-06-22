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
package org.solmix.runtime.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


/**
 * {@link InputStream} 资源，如：File或Classpath Resource等。
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月19日
 */

public interface InputStreamResource
{
    InputStream getInputStream() throws IOException;
    
    /**
     * 资源是否存在
     * 
     * @return
     */
    boolean exists();
    
    /**
     * 是否可以通过{@link #getInputStream()}或者{@link #getFile()}读取资源。
     * @return
     */
    boolean isReadable();
    
    /**
     * 将资源作为一个文件返回
     * @return
     */
    File getFile() throws IOException;
    
    URL getURL() throws IOException;
    
    
    URI getURI() throws IOException;
    
    
    /**
     * 最后一次修改的时间戳
     * 
     * @return
     * @throws IOException
     */
    long lastModified() throws IOException;
    
    
    /**
     * 文件名
     * 
     * @return
     */
    String getFilename();
    
    String getDescription();
}
