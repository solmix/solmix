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
package org.solmix.api.cm;

import java.io.IOException;
import java.util.Dictionary;

import org.solmix.commons.collections.DataTypeMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-5
 */

public interface ConfigureUnit
{
    public static final String USER_CONFIG_DIR="classpath*:config/*.cfg";
    public static final String USER_CONFIG_DIR_PROPERTY_NAME="solmix.config.dir";
    public DataTypeMap getProperties();
    public void delete() throws IOException;
    public void update() throws IOException;
    public void update(Dictionary<String, ? > properties) throws IOException;
    public String getPid();
}
