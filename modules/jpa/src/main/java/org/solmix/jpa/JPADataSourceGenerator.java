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

package org.solmix.jpa;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.namespace.QName;

import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.jaxb.Tvalue;
import org.solmix.api.jaxb.TvalueMap;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.BasicGenerator;
import org.solmix.fmk.internal.DatasourceCM;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2011-6-27
 */

public class JPADataSourceGenerator extends BasicGenerator implements DataSourceGenerator
{

    public JPADataSourceGenerator(JPADataSource jpa)
    {
        super(jpa);
    }

    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {
        return getDataSource().instance(context);
    }

    @Override
    protected TdataSource deriveFromSchemaClass(String parentID, Class<?> clz) throws SlxException {
        if (clz.isAnnotationPresent(Entity.class)) {
            return deriveFromEntity(parentID, clz);
        } else
            return super.deriveFromSchemaClass(parentID, clz);

    }

    @Override
    protected TdataSource deriveFromJavaBean(String parentID, Class<?> clz) throws SlxException {
        if (clz.isAnnotationPresent(Entity.class)) {
            return deriveFromEntity(parentID, clz);
        } else
            return super.deriveFromJavaBean(parentID, clz);
    }

    protected TdataSource deriveFromEntity(String parentID, Class<?> clz) throws SlxException {
        TdataSource data = new TdataSource();
        /** generatedBy */
        data.getOtherAttributes().put(new QName("generatedBy"), DatasourceCM.FRAMEWORK_VERSION);
        data.getOtherAttributes().put(new QName("entityBean"), clz.getName());
        /**********************************************
         * Auto generation ID
         *********************************************/
        Entity e = clz.getAnnotation(Entity.class);
        String ID = e.name();
        if (DataUtil.isNullOrEmpty(ID))
            ID = super.deriveID(clz, parentID);
        else {
            ID = new StringBuilder().append(ID).append(DERIVE_KEY).append(parentID).toString();
        }
        data.setID(ID);
        /**********************************************
         * Auto generation field
         *********************************************/
        Tfields fields = new Tfields();
        data.setFields(fields);
        try {
            Field[] declaredFields = clz.getDeclaredFields();
            Map<String, PropertyDescriptor> propDes= DataUtil.getPropertyDescriptors(clz);
            for (Field field : declaredFields) {
                int modifier = field.getModifiers();
                String propertyName = field.getName();
                if (Modifier.isStatic(modifier))
                    continue;
                Class<?> type = field.getType();
                Efield fieldType = adapteType(type);
                TvalueMap valueMap = null;
                if (fieldType == Efield.ENUM) {
                    Object constants[] = type.getEnumConstants();
                    valueMap = new TvalueMap();
                    for (Object i : constants) {
                        String proName = i.toString();
                        String value = DataUtil.deriveTileFromName(proName);
                        Tvalue tv = new Tvalue();
                        tv.setId(proName);
                        tv.setName(value);
                        valueMap.getValue().add(tv);
                    }
                }else if(fieldType==Efield.UNKNOWN){
                    continue;
                }
                // build Tfield.
                Tfield buildField = new Tfield();
                buildField.setName(propertyName);
                buildField.setType(fieldType);
                buildField.setDerived(true);
                if (valueMap != null)
                    buildField.setValueMap(valueMap);
                // jpa AccessType=FIELD;
                if (field.getAnnotation(Id.class) != null)
                    buildField.setPrimaryKey(true);
                else {// AccessType=PROPERTY
                    PropertyDescriptor propDesc = propDes.get(propertyName);
                    Method read= propDesc.getReadMethod();
                    if(read!=null&&read.getAnnotation(Id.class)!=null)
                        buildField.setPrimaryKey(true);
                }
                fields.getField().add(buildField);

            }

        } catch (Exception exception) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_GENERATE_DS_ERROR, exception);
        }
        return data;
    }

}
