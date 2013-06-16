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

package com.solmix.sgt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-7
 */

public class DataSourceReadyEvent extends GwtEvent<DataSourceReadyHandler>
{

    private static Type<DataSourceReadyHandler> TYPE;

    private final DataSource dataSource;

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DataSourceReadyHandler> getAssociatedType() {
        return getType();
    }

    public static Type<DataSourceReadyHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DataSourceReadyHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(DataSourceReadyHandler handler) {
        handler.onReady(this);

    }

    public DataSourceReadyEvent(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

}
