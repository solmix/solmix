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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.namespace.QName;

import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.jaxb.Tvalue;
import org.solmix.api.jaxb.TvalueMap;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.base.Reflection;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2011-6-27
 */

public class JPADataSourceGenerator implements DataSourceGenerator
{

    private final JPADataSource jpa;

    
    public JPADataSourceGenerator(JPADataSource jpa)
    {
        this.jpa = jpa;
    }

    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {
        return jpa.instance(context);
    }

    private List<Tfield> getEntityFields(Class<?> clz) throws Exception {
        List<Tfield> fields = new ArrayList<Tfield>();
        Field[] declaredFields = clz.getDeclaredFields();
        if (declaredFields == null)
            return null;
        for (Field field : declaredFields) {
            /** field name */
            String name = field.getName();
            int modifier = field.getModifiers();
            if (Modifier.isStatic(modifier))
                continue;
            Class<?> type = field.getType();
            String typeName = type.getName();
            Efield fieldType = null;
            /** field type */
            TvalueMap valueMap = null;
            if (type.isEnum()) {
                fieldType = Efield.ENUM;
                Object constants[] = type.getEnumConstants();
                valueMap = new TvalueMap();
                for (Object i : constants) {
                    String proName = i.toString();
                    String value = DataUtil.deriveTileFromName(proName);
                    Tvalue tv = new Tvalue();
                    tv.setId(value);
                    tv.setName(proName);
                    valueMap.getValue().add(tv);
                }
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
                continue;
            }
            /** primary key */
            Boolean isPrimaryKey = null;
            if (field.getAnnotation(Id.class) != null)
                isPrimaryKey = Boolean.TRUE;
            /** build fields */
            Tfield buildField = new Tfield();
            buildField.setName(name);
            buildField.setType(fieldType);
            buildField.setDerived(true);
            if (valueMap != null)
                buildField.setValueMap(valueMap);
            if (isPrimaryKey != null)
                buildField.setPrimaryKey(isPrimaryKey);
            fields.add(buildField);
        }

        return fields;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceGenerator#deriveSchema(org.solmix.api.data.DataSourceData)
     */
    @Override
    public DataSource deriveSchema(DataSourceData context) throws SlxException {
        if (context == null || context.getTdataSource().getSchemaBean() == null)
            throw new SlxException(Tmodule.JPA, Texception.DS_DSCONFIG_ERROR, "JPA DataSource must have a Entity schema class");
        String entity = context.getTdataSource().getSchemaBean();
        Class<?> entityClass = null;
        try {
            entityClass = Reflection.classForName(entity);
        } catch (Exception e) {
            throw new SlxException(Tmodule.JPA, Texception.NO_FOUND, e);
        }
        if (entityClass == null || !entityClass.isAnnotationPresent(Entity.class))
            throw new SlxException(Tmodule.JPA, Texception.DS_DSCONFIG_ERROR, "JPA DataSource must have a Entity schema class");
        ObjectFactory factory = new ObjectFactory();
        TdataSource data = factory.createTdataSource();
        /** ID */
        Entity e = entityClass.getAnnotation(Entity.class);
        String ID = e.name();
        if (DataUtil.isNullOrEmpty(ID))
            ID = entityClass.getName().substring(entityClass.getName().lastIndexOf(".") + 1);
        ID = ID + INHERIT_KEY;
        data.setID(ID);
        /** ServerType */
        data.setServerType(EserverType.BASIC);
        String core_version = "Default core generation";
        /** generatedBy */
        data.getOtherAttributes().put(new QName("generatedBy"), core_version);
        data.getOtherAttributes().put(new QName("beanClassName"), entityClass.getName());
        List<Tfield> autoFields = null;
        try {
            autoFields = this.getEntityFields(entityClass);
        } catch (Exception e1) {
            throw new SlxException(Tmodule.JPA, Texception.DS_GENERAT_SCHEMA_EXCEPTION, e1);
        }
        Tfields fields = factory.createTfields();
        fields.getField().addAll(autoFields);
        data.setFields(fields);
        DataSourceData schemaContext = new DataSourceData(data);
        schemaContext.setAttribute("_entity_class", entityClass);
        return generateDataSource(schemaContext);
    }
}
