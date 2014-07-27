/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.runtime.extension;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class InternalExtensionParser
{
 private static Pattern colonPattern = Pattern.compile(":");
    
    final ClassLoader loader;
    
    public InternalExtensionParser(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * @param nextElement
     * @return
     */
    public List<ExtensionInfo> getExtensions(URL nextElement) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param is
     * @return
     */
    public List<ExtensionInfo> getExtensions(InputStream is) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
