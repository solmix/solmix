/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.commons.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月14日
 */

public class TransformUtils
{

    public static interface Transformer
    {

        public abstract Object transform(Object obj) throws Exception;
    }

    private static final Logger LOG = LoggerFactory.getLogger(TransformUtils.class.getName());

    @SuppressWarnings("unchecked")
    public static <T> T transformType(Class<T> targetType, Object value)
        throws Exception {
        if (targetType == null)
            return null;
        if (value == null)
            return null;
        if (targetType.isInstance(value))
            return (T) value;
        if ((value instanceof String) && "".equals(value) && Number.class.isAssignableFrom(targetType))
            return null;
        Transformer transformer = (Transformer) defaultTransformers.get(targetType);
        if (transformer != null)
            return (T) transformer.transform(value);
        if (targetType.isEnum())
            return (T) transformEnum(value, targetType);
        if ((value instanceof Map) && !targetType.isPrimitive()
            && !targetType.isInterface() && !targetType.isArray()) {
            Object instance = targetType.newInstance();
            try {
                DataUtils.setProperties((Map) value, instance);
                return (T) instance;
            } catch (Exception ee) {
                LOG.debug((new StringBuilder()).append(
                    "Tried to convert inbound nested Map to: ").append(
                    targetType.getName()).append(
                    " but DataTools.setProperties() on instantiated class failed").append(
                    " with the following error: ").append(ee.getMessage()).toString());
            }
        }
        if (!targetType.isPrimitive() && !targetType.isEnum()) {
            Class<?> types[] = { value.getClass() };
            Constructor<?> constructor = targetType.getConstructor(types);
            Object arguments[] = { value };
            return (T) constructor.newInstance(arguments);
        }
        if (!targetType.isPrimitive()
            && (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())))
            LOG.warn((new StringBuilder()).append(
                "Impossible to convert to target type ").append(
                targetType.getName()).append(" - it is not a concrete class").toString());
        throw new IllegalArgumentException(
            (new StringBuilder()).append("Can't convert value of type ").append(
                value.getClass().getName()).append(" to target type ").append(
                targetType.getName()).toString());
    }

    public static Object transformValue(Object value, Class<?> targetType) {
        if (value == null)
            return null;
        Class<?> valueClass = value.getClass();
        if (Boolean.TYPE.equals(targetType))
            targetType = java.lang.Boolean.class;
        if (Byte.TYPE.equals(targetType))
            targetType = java.lang.Byte.class;
        if (Short.TYPE.equals(targetType))
            targetType = java.lang.Short.class;
        if (Integer.TYPE.equals(targetType))
            targetType = java.lang.Integer.class;
        if (Long.TYPE.equals(targetType))
            targetType = java.lang.Long.class;
        if (Float.TYPE.equals(targetType))
            targetType = java.lang.Float.class;
        if (Double.TYPE.equals(targetType))
            targetType = java.lang.Double.class;
        if (Character.TYPE.equals(targetType))
            targetType = java.lang.Character.class;
        if (targetType.isInstance(value))
            return value;
        if (java.lang.Boolean.class.equals(targetType)) {
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return n.signum() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                if (c == 'T' || c == 't' || c == 'Y' || c == 'y')
                    return Boolean.TRUE;
                else
                    return Boolean.FALSE;
            }
            if (java.lang.String.class.isAssignableFrom(valueClass)) {
                String s = value.toString();
                if ("t".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s)
                    || "true".equalsIgnoreCase(s)
                    || "false".equalsIgnoreCase(s))
                    return Boolean.TRUE;
                else
                    return Boolean.FALSE;
            }
        } else if (java.lang.Byte.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Byte.valueOf((byte) (Boolean.TRUE.equals(value) ? 1 : 0));
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Byte.valueOf(n.byteValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Byte.valueOf((byte) c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
        } else if (java.lang.Short.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Short.valueOf((short) (Boolean.TRUE.equals(value) ? 1
                    : 0));
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Short.valueOf(n.shortValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Short.valueOf((short) c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
        } else if (java.lang.Integer.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Integer.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Integer.valueOf(n.intValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Integer.valueOf(c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Integer.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
        } else if (java.lang.Long.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Long.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Long.valueOf(n.longValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Long.valueOf(c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
            if (java.util.Date.class.isAssignableFrom(valueClass))
                return Long.valueOf(((Date) value).getTime());
        } else if (java.lang.Float.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Float.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Float.valueOf(n.floatValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Float.valueOf(c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
            if (java.util.Date.class.isAssignableFrom(valueClass))
                return Float.valueOf(((Date) value).getTime());
        } else if (java.lang.Double.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Double.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Double.valueOf(n.doubleValue());
            }
            if (java.lang.Character.class.isAssignableFrom(valueClass)) {
                char c = ((Character) value).charValue();
                return Double.valueOf(c);
            }
            if (java.lang.String.class.isAssignableFrom(valueClass))
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException ex) {
                    throw new ClassCastException((new StringBuilder()).append(
                        "Value '").append(value.toString()).append(
                        "' of type '").append(valueClass.toString()).append(
                        "' can not be casted to type '").append(
                        targetType.toString()).append("'.").toString());
                }
            if (java.util.Date.class.isAssignableFrom(valueClass))
                return Double.valueOf(((Date) value).getTime());
        } else if (java.lang.Character.class.equals(targetType)) {
            if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                return Character.valueOf(Boolean.TRUE.equals(value) ? 't' : 'f');
            if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                BigDecimal n = new BigDecimal(((Number) value).toString());
                return Character.valueOf((char) n.intValue());
            }
            if (java.lang.String.class.isAssignableFrom(valueClass)) {
                if ("".equals(value.toString()))
                    return Character.valueOf('\0');
                value.toString().charAt(0);
            }
        } else if (targetType.isEnum()) {
            return transformEnum(value, targetType);
        } else {
            if (java.lang.String.class.isAssignableFrom(targetType))
                return value.toString();
            if (java.math.BigInteger.class.isAssignableFrom(targetType)) {
                if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                    return Boolean.TRUE.equals(value) ? BigInteger.ONE
                        : BigInteger.ZERO;
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return n.toBigInteger();
                }
                if (java.lang.Character.class.isAssignableFrom(valueClass))
                    return BigInteger.valueOf(((Character) value).charValue());
                if (java.lang.String.class.isAssignableFrom(valueClass))
                    try {
                        return new BigInteger(value.toString());
                    } catch (NumberFormatException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return BigInteger.valueOf(((Date) value).getTime());
            } else if (java.math.BigDecimal.class.isAssignableFrom(targetType)) {
                if (java.lang.Boolean.class.isAssignableFrom(valueClass))
                    return Boolean.TRUE.equals(value) ? BigDecimal.ONE
                        : BigDecimal.ZERO;
                if (java.lang.Number.class.isAssignableFrom(valueClass))
                    return new BigDecimal(((Number) value).toString());
                if (java.lang.Character.class.isAssignableFrom(valueClass))
                    return BigDecimal.valueOf(((Character) value).charValue());
                if (java.lang.String.class.isAssignableFrom(valueClass))
                    try {
                        return new BigDecimal(value.toString());
                    } catch (NumberFormatException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return BigDecimal.valueOf(((Date) value).getTime());
            } else if (java.sql.Date.class.isAssignableFrom(targetType)) {
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return new java.sql.Date(n.longValue());
                }
                if (java.lang.String.class.isAssignableFrom(valueClass)) {
                    DateFormat df = DateFormat.getDateInstance();
                    try {
                        java.util.Date d = df.parse(value.toString());
                        return new java.sql.Date(d.getTime());
                    } catch (ParseException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Date) value).getTime());
                if (java.sql.Time.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Time) value).getTime());
                if (java.sql.Timestamp.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Timestamp) value).getTime());
            } else if (java.sql.Time.class.isAssignableFrom(targetType)) {
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return new Time(n.longValue());
                }
                if (java.lang.String.class.isAssignableFrom(valueClass)) {
                    DateFormat df = DateFormat.getTimeInstance();
                    try {
                        Date d = df.parse(value.toString());
                        return new Time(d.getTime());
                    } catch (ParseException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new Time(((Date) value).getTime());
                if (java.sql.Date.class.isAssignableFrom(valueClass))
                    return new Time(((java.sql.Date) value).getTime());
                if (java.sql.Timestamp.class.isAssignableFrom(valueClass))
                    return new Time(((Timestamp) value).getTime());
            } else if (java.sql.Timestamp.class.isAssignableFrom(targetType)) {
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return new Timestamp(n.longValue());
                }
                if (java.lang.String.class.isAssignableFrom(valueClass)) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                    try {
                        Date d = df.parse(value.toString());
                        return new Timestamp(d.getTime());
                    } catch (ParseException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new Timestamp(((Date) value).getTime());
                if (java.sql.Date.class.isAssignableFrom(valueClass))
                    return new Timestamp(((java.sql.Date) value).getTime());
                if (java.sql.Time.class.isAssignableFrom(valueClass))
                    return new Timestamp(((Time) value).getTime());
            } else if (java.util.Date.class.isAssignableFrom(targetType)) {
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return new Date(n.longValue());
                }
                if (java.lang.String.class.isAssignableFrom(valueClass)) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                    try {
                        return df.parse(value.toString());
                    } catch (ParseException ex) {
                        throw new ClassCastException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                targetType.toString()).append("'.").toString());
                    }
                }
            }
        }
        throw new ClassCastException(
            (new StringBuilder()).append("Value '").append(value.toString()).append(
                "' of type '").append(valueClass.toString()).append(
                "' can not be casted to type '").append(targetType.toString()).append(
                "'.").toString());
    }

    /**
     * @param <T>
     * @param value
     * @param targetType
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object transformEnum(Object value, Class targetType) {
        if (!targetType.isEnum())
            return null;
        Enum<?> theEnum = null;
        Object enumConsts[] = targetType.getEnumConstants();
        List<String> constants = new ArrayList<String>();
        for (Object cons : enumConsts) {
            constants.add(cons.toString());
        }
        String valueStr = value.toString();
        if (constants.contains(valueStr)) {
            theEnum = Enum.valueOf(targetType, valueStr);
        } else {
            String valueStrLC = valueStr.toLowerCase();
            for (String constant : constants) {
                if (constant.toLowerCase().equals(valueStrLC)) {
                    theEnum = Enum.valueOf(targetType, constant);
                    break;
                }
            }
        }
        if (theEnum == null) {
            if (LOG.isWarnEnabled())
                LOG.warn("was not found the enum String" + value
                    + "for targetType " + targetType.getName());
        }

        return targetType.cast(theEnum);
    }

    public static HashMap<Class<?>,Object> defaultTransformers;
    static {
        defaultTransformers = new HashMap<Class<?>,Object>();
        Transformer stringTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                return input==null?null: input.toString();
            }

        };
        defaultTransformers.put(String.class, stringTransform);
        Transformer boolTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                return Boolean.valueOf(input.toString());
            }

        };
        defaultTransformers.put(Boolean.TYPE, boolTransform);
        defaultTransformers.put(Boolean.class, boolTransform);
        Transformer charTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Character('\0');
                else
                    return new Character(value.charAt(0));
            }

        };
        defaultTransformers.put(Character.TYPE, charTransform);
        defaultTransformers.put(Character.class, charTransform);
        Transformer byteTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Byte((byte) 0);
                else
                    return Byte.valueOf(value);
            }

        };
        defaultTransformers.put(Byte.TYPE, byteTransform);
        defaultTransformers.put(Byte.class, byteTransform);
        Transformer shortTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Short((short) 0);
                else
                    return Short.valueOf(value);
            }

        };
        defaultTransformers.put(Short.TYPE, shortTransform);
        defaultTransformers.put(Short.class, shortTransform);
        Transformer intTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString().trim();
                if ("".equals(value))
                    return new Integer(0);
                else
                    return Integer.valueOf(value);
            }

        };
        defaultTransformers.put(Integer.TYPE, intTransform);
        defaultTransformers.put(Integer.class, intTransform);
        Transformer longTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Long(0L);
                else
                    return Long.valueOf(value);
            }

        };
        defaultTransformers.put(Long.TYPE, longTransform);
        defaultTransformers.put(Long.class, longTransform);
        Transformer floatTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Float(0.0F);
                else
                    return Float.valueOf(value);
            }

        };
        defaultTransformers.put(Float.TYPE, floatTransform);
        defaultTransformers.put(Float.class, floatTransform);
        Transformer doubleTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new Double(0.0D);
                else
                    return Double.valueOf(value);
            }

        };
        defaultTransformers.put(Double.TYPE, doubleTransform);
        defaultTransformers.put(Double.class, doubleTransform);
        Transformer bigDecimalTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return new BigDecimal(0);
                else
                    return new BigDecimal(value);
            }

        };
        defaultTransformers.put(BigDecimal.class, bigDecimalTransform);
        Transformer bigIntegerTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                String value = input.toString();
                if ("".equals(value))
                    return BigInteger.ZERO;
                else
                    return new BigInteger(value);
            }

        };
        defaultTransformers.put(BigInteger.class, bigIntegerTransform);
        Transformer javaSqlDateTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                if (input instanceof java.util.Date) {
                    Calendar c = Calendar.getInstance();
                    c.setTime((java.util.Date) input);
                    c.set(11, 0);
                    c.set(12, 0);
                    c.set(13, 0);
                    c.set(14, 0);
                    return new Date(c.getTime().getTime());
                } else {
                    throw new Exception(
                        (new StringBuilder()).append("Can't covert type: ").append(
                            input.getClass().getName()).append(
                            " to java.sql.Date").toString());
                }
            }

        };
        defaultTransformers.put(Date.class, javaSqlDateTransform);
        Transformer javaSqlTimeTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                if (input instanceof java.util.Date) {
                    Calendar c = Calendar.getInstance();
                    c.setTime((java.util.Date) input);
                    c.set(1970, 0, 1);
                    return new Time(c.getTime().getTime());
                } else {
                    throw new Exception(
                        (new StringBuilder()).append("Can't covert type: ").append(
                            input.getClass().getName()).append(
                            " to java.sql.Time").toString());
                }
            }

        };
        defaultTransformers.put(Time.class, javaSqlTimeTransform);
        Transformer javaSqlTimestampTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                if (input instanceof java.util.Date)
                    return new Timestamp(((java.util.Date) input).getTime());
                else
                    throw new Exception((new StringBuilder()).append(
                        "Can't covert type: ").append(
                        input.getClass().getName()).append(
                        " to java.sql.Timestamp").toString());
            }

        };
        defaultTransformers.put(Timestamp.class, javaSqlTimestampTransform);
        
        Transformer mapTransform = new Transformer() {

            @Override
            public Object transform(Object input) throws Exception {
                if(input==null){
                    return null;
                }
                Class<?> targetType=input.getClass();
                
                if (input instanceof Map<?,?>)
                    return Map.class.cast(input);
                else if(!targetType.isPrimitive()
                        && !targetType.isInterface() 
                        && !targetType.isArray()
                        && !targetType.isAnnotation()
                        && !targetType.isEnum()){
                  return  DataUtils.getProperties(input, true);
                }else {
                    
                    throw new Exception((new StringBuilder()).append(
                        "Can't covert type: ").append(
                        input.getClass().getName()).append(
                        " to java.util.Map").toString());
                }
            }

        };
        defaultTransformers.put(Map.class, mapTransform);
    }
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() 
                || type == String.class 
                || type == Character.class
                || type == Boolean.class
                || type == Byte.class
                || type == Short.class
                || type == Integer.class 
                || type == Long.class
                || type == Float.class 
                || type == Double.class
                || type == Object.class;
    }
    public static Object convertPrimitive(Class<?> type, String value) {
        if (type == char.class || type == Character.class) {
            return value.length() > 0 ? value.charAt(0) : '\0';
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.valueOf(value);
        } else if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        } else if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        } else if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        } else if (type == float.class || type == Float.class) {
            return Float.valueOf(value);
        } else if (type == double.class || type == Double.class) {
            return Double.valueOf(value);
        }
        return value;
    }
}
