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

package org.solmix.fmk.datasource;

import java.util.List;

import javax.xml.namespace.QName;

import org.solmix.api.context.SystemContext;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.internal.DatasourceCM;

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
     * @see org.solmix.api.datasource.DataSourceGenerator#generateDataSource(java.lang.Object)
     */
    @Override
    public DataSource generateDataSource(DataSourceData context,SystemContext sc) throws SlxException {
        if (context == null || context.getTdataSource().getSchemaBean() == null)
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR,
                " configure anto gernerate DataSource must figure out a schema class");
        String entity = context.getTdataSource().getSchemaBean();
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
        String core_version = DatasourceCM.FRAMEWORK_VERSION;
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
