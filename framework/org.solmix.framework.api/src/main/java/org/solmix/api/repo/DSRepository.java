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

package org.solmix.api.repo;

import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;

/**
 * 
 * @version 110035
 */
public interface DSRepository
{

    String getName();

    String getObjectField();

    String[] getLocations();

    String getObjectFormat();

    /**
     * Load Ds object.
     * 
     * @param ds
     * @return
     * @throws Exception
     */
    Object load(String ds) throws SlxException;

    /*
     * Used Filed path ,so not used group at all.
     */
    // Object load(String group, String ds) throws SlxException;

    /**
     * Load system internal Ds object.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    Object loadSystem(String name) throws SlxException;

    /**
     * @param ds
     * @return
     * @throws Exception
     */
    DataSource loadDS(String ds) throws SlxException;

    /*
     * Used Filed path ,so not used group at all.
     */
    // DataSource loadDS(String group, String ds) throws SlxException;

    DataSource loadSystemDS(String ds) throws SlxException;
}
