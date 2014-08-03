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
package org.solmix.jpa;

import java.util.UUID;

import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.fmk.SlxContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-8
 */

public class JPADataSourceBuilder
{
    
    private final  DataSourceManager manager;
    public JPADataSourceBuilder(){
        manager=SlxContext.getSystemContext().getExtension(DataSourceManager.class);
    }
    public DataSource build(DataSourceData data) throws SlxException{
        return manager.generateDataSource(data);
    }
   
    public DataSource build(Class<?> clz,boolean deriveSchema) throws SlxException{
        assertNotNull(manager,"Can't find DataSourceManager.");
        TdataSource tds = new TdataSource();
        tds.setID(UUID.randomUUID().toString());
        tds.setServerType(EserverType.JPA);
        tds.setBean(clz.getName());
        if(deriveSchema){
            tds.setAutoDeriveSchema(true);
            tds.setSchemaClass(clz.getName());
        }
        DataSourceData dsd = new DataSourceData(tds);
        DataSource user= manager.generateDataSource(dsd);
        return user;
    }
    public TdataSource getJPATdata(){
        TdataSource tds = new TdataSource();
        tds.setID(UUID.randomUUID().toString());
        tds.setServerType(EserverType.JPA);
        return tds;
    }
    public DataSource build() throws SlxException{
        assertNotNull(manager,"Can't find DataSourceManager.");
        TdataSource tds = new TdataSource();
        tds.setID(UUID.randomUUID().toString());
        tds.setServerType(EserverType.JPA);
        DataSourceData dsd = new DataSourceData(tds);
        DataSource user= manager.generateDataSource(dsd);
        return user;
    }
    private void assertNotNull(Object o,String msg){
        if(o==null)
            throw new java.lang.IllegalStateException(msg);
    }
}
