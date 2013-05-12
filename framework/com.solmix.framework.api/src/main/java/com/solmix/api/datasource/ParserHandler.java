/*
 * ========THE SOLMIX PROJECT=====================================
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

package com.solmix.api.datasource;

import com.solmix.api.exception.SlxException;

/**
 * Parser used as Before-parser for ds.we can user this interface to parser configuration file before initialize ds.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-26 solmix-api
 */
public interface ParserHandler
{

    public static final String DS_SUFFIX = "ds";

    Object parser(String dsName) throws SlxException;

    Object parser(String repoName, String dsName) throws SlxException;

    /**
     * @param repoName
     * @param dsName
     * @param request
     * @return
     * @throws SlxException
     */
    Object parser(String repoName, String dsName, DSRequest request) throws SlxException;

    Object parser(String repoName, String dsName, String suffix, DSRequest request) throws SlxException;

}
