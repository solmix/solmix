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

package org.solmix.sgt.client.panel;

import com.smartgwt.client.core.Function;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.DataBoundComponent;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-7
 */

public abstract class DataSourcePanel extends AbstractPanel implements HasDataSource
{

    private boolean dsLock = false;

    private DataSource tmp;

    /**
     * {@inheritDoc}
     * 
     * @see com.ieslab.eimpro.client.panel.HasDataSource#bind(com.smartgwt.client.data.DataSource,
     *      com.smartgwt.client.widgets.DataBoundComponent)
     */
    @Override
    public void bind(DataSource datasource, DataBoundComponent component) {
        component.setDataSource(datasource);

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.ieslab.eimpro.client.panel.HasDataSource#bind(java.lang.String,
     *      com.smartgwt.client.widgets.DataBoundComponent)
     */
    @Override
    public void bind(final String datasourceName, final DataBoundComponent component) {
        bind(datasourceName, component, null);

    }

    /**
     * This Method must be called at the end of DataBoundcomponent initialed.
     * 
     * @param datasourceName
     * @param component
     * @param callback
     */
    public void bind(final String datasourceName, final DataBoundComponent component, final Function callback) {
     final  String dsName=datasourceName.replace("/", "$");
        DataSource ds = DataSource.get(dsName);
        if (ds == null) {
            DataSource.load(dsName, new Function() {

                @Override
                public void execute() {
                    DataSource _ds = DataSource.get(dsName);
                    component.setDataSource(_ds);
                    if (callback != null)
                        callback.execute();
                }
            }, true);
        } else {
            component.setDataSource(ds);
            if (callback != null)
                callback.execute();
        }
    }

    public DataSource getDataSource(String datasourceName) {
        DataSource ds = DataSource.get(datasourceName);
        if (ds == null) {
            DataSource.load(datasourceName, new Function() {

                @Override
                public void execute() {
                    if (!dsLock) {
                        dsLock = true;
                        tmp = DataSource.get("advance$CatvTgLineLose");
                    }
                }
            }, true);
            dsLock = false;
            return tmp;
        } else {
            return ds;
        }
    }
}
