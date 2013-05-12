/*
 * SOLMIX PROJECT
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

package com.solmix.fmk.context;

import com.solmix.api.context.DataSourceManagerProvider;
import com.solmix.api.datasource.DataSourceManager;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-8
 */

public class SingleDSMProvider implements DataSourceManagerProvider
{

    private DataSourceManager dataSourceManager;

    public SingleDSMProvider(DataSourceManager dsm)
    {
        this.dataSourceManager = dsm;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.DataSourceManagerProvider#getDataSourceManager()
     */
    @Override
    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.DataSourceManagerProvider#release()
     */
    @Override
    public void release() {

    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

}
