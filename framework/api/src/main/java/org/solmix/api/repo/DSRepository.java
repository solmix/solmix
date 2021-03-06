/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.api.repo;

import org.solmix.api.exception.SlxException;

/**
 * 
 * @version 110035
 */
public interface DSRepository
{

    public enum ObjectType
    {
        URL , SLX_FILE , STREAM;
    }

    public enum ObjectFormat
    {
        XML , CLASS;
    }

    public static final String EXT_FILE = "ExtFile";

    public static final String BUILDIN_FILE = "BuildInFile";

    String getName();

    ObjectType getObjectType();

    ObjectFormat getObjectFormat();

    /**
     * Load Ds object.
     * 
     * @param ds
     * @return
     * @throws Exception
     */
    Object load(String ds) throws SlxException;

}
