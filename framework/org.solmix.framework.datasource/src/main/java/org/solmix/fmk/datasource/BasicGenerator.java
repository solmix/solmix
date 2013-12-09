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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.xml.namespace.QName;

import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DataSource;
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
import org.solmix.commons.util.ClassLoaderUtil;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.internal.DatasourceCM;

/**
 * 
 * @author Administrator
 * @version 110035 2011-6-22
 */

public class BasicGenerator implements DataSourceGenerator
{

    private final DataSource datasource;

    /**
     * @param sc
     */
    public BasicGenerator(DataSource datasource)
    {
        this.datasource = datasource;
    }

    protected DataSource getDataSource() {
        return this.datasource;
    }

    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {
        DataSource ds = datasource.instance(context);
        return ds;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceGenerator#deriveSchema(org.solmix.api.data.DataSourceData)
     */
    @Override
    public DataSource deriveSchema(DataSourceData context) throws SlxException {
        TdataSource pds = context.getTdataSource();
        String parentID = pds.getID();
        String schemaClass = context.getTdataSource().getSchemaClass();
        TdataSource data = null;
        Class<?> clz = null;
        // first form schemaClass.
        if (schemaClass != null) {
            clz = loadClass(schemaClass);
            data = deriveFromSchemaClass(parentID, clz);
        } else {// no found schema used bean instead of it.
            String bean = context.getTdataSource().getBean();
            if (bean != null) {
                clz = loadClass(bean);
                data = deriveFromJavaBean(parentID, clz);
            }
        }
        if (data != null) {
            DataSourceData schemaContext = new DataSourceData(data);
            schemaContext.setAttribute("_entity_class", clz);
            return generateDataSource(schemaContext);
        }

        DataSourceData schemaContext = deriveFromType();
        return schemaContext == null ? null : generateDataSource(schemaContext);
    }
    protected TdataSource deriveFromSchemaClass(String parentID, Class<?> clz) throws SlxException {
        return deriveFromField(parentID,clz);
    }
    protected TdataSource deriveFromJavaBean(String parentID, Class<?> clz) throws SlxException {
        return deriveFromProperty(parentID,clz);
    }
    protected TdataSource deriveFromField(String parentID, Class<?> clz) throws SlxException {
        TdataSource data = new TdataSource();
        data.setID(deriveID(clz, parentID));
        /** generatedBy */
        data.getOtherAttributes().put(new QName("generatedBy"), DatasourceCM.FRAMEWORK_VERSION);
        data.getOtherAttributes().put(new QName("schemaClassName"), clz.getName());
        Field[] declaredFields = clz.getDeclaredFields();
        if (declaredFields == null)
            return null;
        Tfields fields = new Tfields();
        data.setFields(fields);
        for (Field field : declaredFields) {
            Tfield tf = deriveTfield(field);
            if (tf != null)
                fields.getField().add(tf);
        }
        return data;
    }
   

    protected Tfield deriveTfield(Field field) {
        String name = field.getName();
        int modifier = field.getModifiers();
        if (Modifier.isStatic(modifier))
            return null;
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
        }
        /** field type */
        Tfield buildField = new Tfield();
        buildField.setName(name);
        buildField.setType(fieldType);
        buildField.setDerived(true);
        if (valueMap != null)
            buildField.setValueMap(valueMap);
        return buildField;

    }

    protected Tfield deriveTfield(PropertyDescriptor propDesc) {
        String name = propDesc.getName();
        Class<?> type = propDesc.getPropertyType();
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
        }
        Tfield buildField = new Tfield();
        buildField.setName(name);
        buildField.setType(fieldType);
        buildField.setDerived(true);
        if (valueMap != null)
            buildField.setValueMap(valueMap);
        return buildField;
    }

    public static Efield adapteType(Class<?> type) {
        Efield fieldType = null;
        String typeName = type.getName();
        if (type.isEnum()) {
            fieldType = Efield.ENUM;
        } else if (String.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type) || "char".equals(typeName))
            fieldType = Efield.TEXT;
        else if (Boolean.class.isAssignableFrom(type) || "boolean".equals(typeName))
            fieldType = Efield.BOOLEAN;
        else if (java.sql.Time.class.isAssignableFrom(type))
            fieldType = Efield.TIME;
        else if (java.sql.Timestamp.class.isAssignableFrom(type))
            fieldType = Efield.DATETIME;
        else if (java.util.Date.class.isAssignableFrom(type))
            fieldType = Efield.DATE;
        else if (java.lang.Byte.class.isAssignableFrom(type) || "byte".equals(typeName) || java.lang.Short.class.isAssignableFrom(type)
            || "short".equals(typeName) || java.lang.Integer.class.isAssignableFrom(type) || "int".equals(typeName)
            || java.lang.Long.class.isAssignableFrom(type) || "long".equals(typeName) || java.math.BigInteger.class.isAssignableFrom(type))
            fieldType = Efield.INTEGER;
        else if (java.lang.Float.class.isAssignableFrom(type) || "float".equals(typeName) || java.lang.Double.class.isAssignableFrom(type)
            || "double".equals(typeName) || java.lang.Number.class.isAssignableFrom(type) || java.math.BigDecimal.class.isAssignableFrom(type))
            fieldType = Efield.FLOAT;
        else {
            fieldType = Efield.UNKNOWN;
        }
        return fieldType;
    }

    protected String deriveID(Class<?> clz, String parentID) {
        String ID = clz.getName().substring(clz.getName().lastIndexOf(".") + 1);
        return new StringBuilder().append(ID).append(DERIVE_KEY).append(parentID).toString();
    }

    protected TdataSource deriveFromProperty(String parentID, Class<?> clz) throws SlxException {
        TdataSource data = new TdataSource();
        data.setID(deriveID(clz, parentID));
        String core_version = DatasourceCM.FRAMEWORK_VERSION;
        /** generatedBy */
        data.getOtherAttributes().put(new QName("generatedBy"), core_version);
        data.getOtherAttributes().put(new QName("beanClassName"), clz.getName());
        try {
            Map<String, PropertyDescriptor> propDes = DataUtil.getPropertyDescriptors(clz);
            if (propDes.isEmpty())
                return null;
            Tfields fields = new Tfields();
            data.setFields(fields);
            for (String key : propDes.keySet()) {
                PropertyDescriptor desc = propDes.get(key);
                Tfield tf = deriveTfield(desc);
                if (tf != null)
                    fields.getField().add(tf);
            }
            return data;
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_GENERATE_DS_ERROR, e);
        }

    }

    /**
     * derived the Tdatasource by custom server type configurarion.such as sql with table ,and jpa with entity bean .
     * 
     * @param clz
     * @return
     * @throws SlxException
     */
    protected DataSourceData deriveFromType() throws SlxException {
        return null;

    }


    protected Class<?> loadClass(String className) throws SlxException {
        Class<?> clz = null;
        try {
            clz = ClassLoaderUtil.loadClass(className, getClass());
            if (clz == null) {
                ClassLoader loader = SlxContext.getThreadSystemContext().getBean(ClassLoader.class);
                clz = loader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.NO_FOUND, e);
        }
        return clz;
    }
}
