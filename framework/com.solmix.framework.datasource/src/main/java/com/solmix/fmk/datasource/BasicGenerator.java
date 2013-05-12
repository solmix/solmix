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

package com.solmix.fmk.datasource;

import java.util.List;

import javax.xml.namespace.QName;

import com.solmix.api.data.DataSourceData;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.datasource.DataSourceGenerator;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.EserverType;
import com.solmix.api.jaxb.TdataSource;
import com.solmix.api.jaxb.Tfield;
import com.solmix.api.jaxb.Tfields;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.fmk.base.Reflection;
import com.solmix.fmk.internel.DSConfigManager;

/**
 * 
 * @author Administrator
 * @version 110035 2011-6-22
 */

public class BasicGenerator implements DataSourceGenerator
{

    public BasicGenerator()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see com.solmix.api.datasource.DataSourceGenerator#generateDataSource(java.lang.Object)
     */
    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {
        if (context == null || context.getTdataSource().getSchemaClass() == null)
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR,
                " configure anto gernerate DataSource must figure out a schema class");
        String entity = context.getTdataSource().getSchemaClass();
        Class<?> clz = null;
        try {
            clz = Reflection.classForName(entity);
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.NO_FOUND, e);
        }
        TdataSource data = new TdataSource();
        /** ID */
        String ID = clz.getName().substring(clz.getName().lastIndexOf(".") + 1);
        ID = ID + INHERIT_KEY;
        data.setID(ID);
        /** ServerType */
        data.setServerType(EserverType.BASIC);
        String core_version = DSConfigManager.frameworkVersion;
        /** generatedBy */
        data.getOtherAttributes().put(new QName("generatedBy"), core_version);
        data.getOtherAttributes().put(new QName("beanClassName"), clz.getName());
        List<Tfield> f = null;
        try {
            f = Reflection.getBeanFields(clz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (f != null) {
            Tfields fields = new Tfields();
            fields.getField().addAll(f);
            data.setFields(fields);
        }
        BasicDataSource ds = new BasicDataSource();
        DataSourceData schemaContext = new DataSourceData(data);
        schemaContext.setAttribute("_entity_class", clz);
        ds.init(schemaContext);
        return ds;

    }

}
