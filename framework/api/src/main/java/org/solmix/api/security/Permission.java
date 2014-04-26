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

package org.solmix.api.security;

import java.io.Serializable;

import org.solmix.commons.util.UrlPattern;

/**
 * 
 * @version 110035 2012-9-26
 */

public interface Permission extends Serializable
{

    /**
     * bit :000000000
     */
    long NONE = 0;

    /**
     * bit :000000001
     */
    long FETCH = 1;

    /**
     * 000000010
     */
    long ADD = 2;

    /**
     * 000000100
     */
    long UPDATE = 4;

    /**
     * 000001000
     */
    long REMOVE = 8;

    long VALIDATE = 16;

    long DOWNLOAD_FILE = 32;

    long VIEW_FILE = 64;

    long REPLACE = 128;

    long ALL = FETCH | ADD | UPDATE | REMOVE | VALIDATE | REPLACE | DOWNLOAD_FILE | VIEW_FILE;

    void setPermissions(long value);

    long getPermissions();

    void setPattern(UrlPattern value);

    UrlPattern getPattern();

    /**
     * True if this permission matches the path.
     */
    boolean match(String path);
}
