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
package org.solmix.jpa;

import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.context.SlxContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-8
 */

public class JPADataSourceUtil
{
    
    private final  DataSourceManager manager;
    public JPADataSourceUtil(){
        manager=SlxContext.getSystemContext().getBean(DataSourceManager.class);
    }
   
    public DataSource build(Class<?> clz) throws SlxException{
        assertNotNull(manager,"Can't find DataSourceManager.");
        DataSource user= manager.generateDataSource(null);
        return user;
    }

    private void assertNotNull(Object o,String msg){
        if(o==null)
            throw new java.lang.IllegalStateException(msg);
    }
}
