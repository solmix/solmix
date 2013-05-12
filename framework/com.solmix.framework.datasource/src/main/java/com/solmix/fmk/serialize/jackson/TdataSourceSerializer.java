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

package com.solmix.fmk.serialize.jackson;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import com.solmix.api.jaxb.TdataSource;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.base.Reflection;

/**
 * 
 * @author Administrator
 * @version 110035 2011-6-24
 */

public class TdataSourceSerializer extends JsonSerializer<TdataSource>
{

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator,
     *      org.codehaus.jackson.map.SerializerProvider)
     */
    @Override
    public void serialize(TdataSource data, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Map<String, PropertyDescriptor> fields = null;
        List<String> filter = new ArrayList<String>();
        filter.add("class");
        filter.add("schemaClass");
        filter.add("service");
        filter.add("otherAttributes");
        jgen.writeRaw("isc.DataSource.create(");

        try {
            fields = DataUtil.getPropertyDescriptors(data);
        } catch (Exception e) {
            // just ignore it.
        }
        jgen.writeStartObject();
        PropertyComparator comparator = new PropertyComparator();
        Set<String> fieldNames = fields.keySet();
        List<String> list = new ArrayList<String>(fieldNames);
        Collections.sort(list, comparator);
        for (String fieldName : list) {
            if (filter.contains(fieldName))
                continue;
            PropertyDescriptor des = fields.get(fieldName);
            Class<?> type = des.getPropertyType();
            String typeName = type.getName();
            Method method = des.getReadMethod();
            /**
             * This is for Default JavaBean {@link java.beans.PropertyDescriptor#getReadMethod()} can't return method
             * witch is auto generated by JAXB.
             */
            if (method == null) {
                String methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    method = Reflection.findMethod(data, methodName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Object arguments[] = null;
            Object fieldValue = null;
            try {
                if (method != null)
                    fieldValue = method.invoke(data, arguments);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (fieldValue != null) {
                if (java.lang.String.class.isAssignableFrom(type) || "string".equalsIgnoreCase(typeName) || Character.class.isAssignableFrom(type)
                    || "char".equals(typeName)) {
                    jgen.writeStringField(fieldName, fieldValue.toString());
                } else if (java.lang.Boolean.class.isAssignableFrom(type) || "boolean".equalsIgnoreCase(typeName)) {
                    jgen.writeBooleanField(fieldName, (Boolean) fieldValue);
                } else if (java.lang.Short.class.isAssignableFrom(type) || "short".equals(typeName) || java.lang.Integer.class.isAssignableFrom(type)
                    || "int".equals(typeName)) {
                    jgen.writeNumberField(fieldName, (Integer) fieldValue);
                } else if (java.lang.Long.class.isAssignableFrom(type) || "long".equals(typeName)) {
                    jgen.writeNumberField(fieldName, (Long) fieldValue);
                } else if (java.lang.Double.class.isAssignableFrom(type) || "double".equals(typeName)) {
                    jgen.writeNumberField(fieldName, (Double) fieldValue);
                } else if (java.lang.Float.class.isAssignableFrom(type) || "float".equals(typeName)) {
                    jgen.writeNumberField(fieldName, (Float) fieldValue);
                } else if (java.math.BigDecimal.class.isAssignableFrom(type)) {
                    jgen.writeNumberField(fieldName, (BigDecimal) fieldValue);
                } else {

                    jgen.writeObjectField(fieldName, fieldValue);
                }
            }
        }
        jgen.writeEndObject();
        jgen.writeRaw(")\r");

    }

}
