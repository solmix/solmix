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

package org.solmix.api.datasource;

import org.solmix.api.data.DataSourceData;
import org.solmix.api.exception.SlxException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-6-22
 */

public interface DataSourceGenerator
{

    public static final String DERIVE_KEY = "_DeriveForm_";

    /**
     * used the datasourcedata context to instance a datasource.if you want to derive a super datasource,please use
     * {@link #deriveSchema(DataSourceData)}
     * 
     * @param context The context of the datasource whiche used to instance a datasource.
     * @return
     * @throws SlxException
     */
    DataSource generateDataSource(DataSourceData context) throws SlxException;

    /**
     * Generate the schema for the datasource.this method will be called when set autoDeriveSchema=true; and the
     * gererated schema used as a super-datasource and merged the field to local.
     * 
     * @param context The context of the datasource whiche want to derive schema
     * @return
     * @throws SlxException
     */
    DataSource deriveSchema(DataSourceData context) throws SlxException;
}
