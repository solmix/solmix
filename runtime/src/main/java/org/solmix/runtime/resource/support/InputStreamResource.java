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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月19日
 */

public interface InputStreamResource
{
    InputStream getInputStream() throws IOException;
    
    boolean exists();
    
    boolean isReadable();
    
    URL getURL() throws IOException;
    
    
    URI getURI() throws IOException;
    
    
    long lastModified() throws IOException;
    
    
    String getFilename();
    
    String getDescription();
}
