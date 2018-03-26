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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.pager.PageList;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月14日
 */

public class TransformUtils
{

    public static interface Transformer<IN,OUT>
    {

        public abstract OUT transform(IN obj) throws TransformException;
    }

    private static final Logger LOG = LoggerFactory.getLogger(TransformUtils.class.getName());

    /**
     * 将指定对象转化为目标类型的对象
     * <li>输入为null，或者targetType为null，返回null
     * <li>输入对象是targetType的实例，直接返回对象
     * <li>返回Boolean.class或者boolean，如果对象是数值类型，数值等于0返回true，否在false，如果对象是String类型，值等一T/t/Y/y/yes/YES返回true，否在false
     * <li>返回Byte.class或者byte，如果对象为Boolean类型，为True返回1,否在0;如果为字符，转化为byte;如果字符串，Byte.valueOf(String);其他类型toString后转化
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * @param value
     * @param targetType
     * @return
     * @throws TransformException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T transform(Object value,Class<T> targetType)throws TransformException {
        if (targetType == null)
            return null;
        if (value == null)
            return null;
        if (targetType.isInstance(value))
            return (T) value;
        if (targetType.isEnum())
            return (T) transformEnum(value, targetType);
        Transformer transformer = defaultTransformers.get(targetType);
        if (transformer != null)
            return (T) transformer.transform(value);
        if ((value instanceof Map) && !targetType.isPrimitive()
            && !targetType.isInterface() && !targetType.isArray()) {
            try {
                Object instance = targetType.newInstance();
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
            
            try {
                Constructor<?>  constructor = targetType.getConstructor(types);
                if(constructor!=null){
                    Object arguments[] = { value };
                    return (T) constructor.newInstance(arguments);
                }
            } catch (NoSuchMethodException e) {
               //IGNORE continue use other 
            } catch (Exception e) {
                throw new TransformException("Can't used constructor to instance class "+targetType.getName(),e);
            }
            
        }
        if (!targetType.isPrimitive()
            && (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())))
            LOG.warn((new StringBuilder()).append(
                "Impossible to convert to target type ").append(
                targetType.getName()).append(" - it is not a concrete class").toString());
        throw new TransformException(
            (new StringBuilder()).append("Can't convert value of type ").append(
                value.getClass().getName()).append(" to target type ").append(
                targetType.getName()).toString());
        
    }
    
    @Deprecated
    public static <T> T transformType(Class<T> targetType, Object value)throws TransformException {
       
      return transform(value,targetType);
    }
    
    public static <IN, OUT> OUT to( IN in,Transformer<IN, OUT> transformer){
        if(in==null){
            return null;
        }
        if(transformer==null){
            return null;
        }
        return transformer.transform(in);
    }
    
    public static <T> Transformer<Object, T> getDefaultTransformer(Class<T> t){
        if(t==null){
            return null;
        }
        return defaultTransformers.get(t);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <IN, OUT> List<OUT> forEach(  List<IN> ins,Transformer<IN, OUT> transformer){
        return (List<OUT>) forEach((Collection)ins, transformer);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <IN, OUT> Set<OUT> forEach(  Set<IN> ins,Transformer<IN, OUT> transformer){
        return (Set<OUT>) forEach((Collection)ins, transformer);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <IN, OUT> Collection<OUT> forEach(  Collection<IN> ins,Transformer<IN, OUT> transformer){
        if(ins==null){
            return null;
        }
        if(transformer==null){
            return null;
        }
        Class<? extends Collection> valueClass = ins.getClass();
        Collection outs=null;
        try {
             outs=   Reflection.newInstance(valueClass);
        } catch (Exception e) {
            throw new TransformException("Error instance new collection for type:"+valueClass);
        }
        for(IN in:ins ){
            OUT out  = transformer.transform(in);
            outs.add(out);
        }
        return outs;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <IN, OUT> PageList<OUT> forEach(  PageList<IN> ins,Transformer<IN, OUT> transformer){
        if(ins==null){
            return null;
        }
        if(transformer==null){
            return null;
        }
        Class<? extends Collection> valueClass = ins.getClass();
        PageList<OUT> outs=new PageList<OUT>(ins.size());
        outs.setTotalSize(ins.getTotalSize());
        outs.setMetaData(ins.getMetaData());
        for(IN in:ins ){
            OUT out  = transformer.transform(in);
            outs.add(out);
        }
        return outs;
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

    public static HashMap<Class<?>, Transformer> defaultTransformers;
    
    static {
        defaultTransformers = new HashMap<Class<?>,Transformer>();
        Transformer<Object,String> stringTransform = new Transformer<Object,String>() {

            @Override
            public String transform(Object input) throws TransformException {
                return input==null?null: input.toString();
            }

        };
        defaultTransformers.put(String.class, stringTransform);
        Transformer<Object,Boolean> boolTransform = new Transformer<Object,Boolean>() {

            @Override
            public Boolean transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                return Boolean.valueOf(value.toString());
            }

        };
        defaultTransformers.put(Boolean.TYPE, boolTransform);
        defaultTransformers.put(Boolean.class, boolTransform);
        Transformer<Object,? extends Character> charTransform = new Transformer<Object, Character>() {

            @Override
            public Character transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                String value1 = value.toString();
                if ("".equals(value))
                    return new Character('\0');
                else
                    return new Character(value1.charAt(0));
            }

        };
        defaultTransformers.put(Character.TYPE, charTransform);
        defaultTransformers.put(Character.class, charTransform);
        Transformer<Object,Byte> byteTransform = new Transformer<Object,Byte>() {

            @Override
            public Byte transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                if (java.lang.String.class.isAssignableFrom(valueClass)){
                    try {
                        return Byte.valueOf(value.toString());
                    } catch (NumberFormatException ex) {
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type java.lang.Byte.'").toString());
                    }
                }
                String value1 = value.toString();
                if ("".equals(value1))
                    return new Byte((byte) 0);
                else
                    return Byte.valueOf(value1);
            }

        };
        defaultTransformers.put(Byte.TYPE, byteTransform);
        defaultTransformers.put(Byte.class, byteTransform);
        Transformer<Object,Short> shortTransform = new Transformer<Object,Short>() {

            @Override
            public Short transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                if (java.lang.String.class.isAssignableFrom(valueClass)){
                    try {
                        return Short.valueOf(value.toString());
                    } catch (NumberFormatException ex) {
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type '").append(
                                Short.class.toString()).append("'.").toString());
                    }
                }
                String value1 = value.toString();
                if ("".equals(value))
                    return new Short((short) 0);
                else
                    return Short.valueOf(value1);
            }

        };
        defaultTransformers.put(Short.TYPE, shortTransform);
        defaultTransformers.put(Short.class, shortTransform);
        Transformer<Object,Integer> intTransform = new Transformer<Object,Integer>() {

            @Override
            public Integer transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type '").append(
                                Integer.class.toString()).append("'.").toString());
                    }
                String value1 = value.toString().trim();
                if ("".equals(value1))
                    return new Integer(0);
                else
                    return Integer.valueOf(value1);
            }

        };
        defaultTransformers.put(Integer.TYPE, intTransform);
        defaultTransformers.put(Integer.class, intTransform);
        Transformer<Object,Long> longTransform = new Transformer<Object,Long>() {

            @Override
            public Long transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type '").append(
                                Long.class.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return Long.valueOf(((Date) value).getTime());
                String value1 = value.toString();
                if ("".equals(value1))
                    return new Long(0L);
                else
                    return Long.valueOf(value1);
            }

        };
        defaultTransformers.put(Long.TYPE, longTransform);
        defaultTransformers.put(Long.class, longTransform);
        Transformer<Object,Float> floatTransform = new Transformer<Object,Float>() {

            @Override
            public Float transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type '").append(
                                Float.class.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return Float.valueOf(((Date) value).getTime());
                String value1 = value.toString();
                if ("".equals(value1))
                    return new Float(0.0F);
                else
                    return Float.valueOf(value1);
            }

        };
        defaultTransformers.put(Float.TYPE, floatTransform);
        defaultTransformers.put(Float.class, floatTransform);
        Transformer<Object,Double> doubleTransform = new Transformer<Object,Double>() {

            @Override
            public Double transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException((new StringBuilder()).append(
                            "Value '").append(value.toString()).append(
                            "' of type '").append(valueClass.toString()).append(
                            "' can not be casted to type '").append(
                            Double.class.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return Double.valueOf(((Date) value).getTime());
                String value1 = value.toString();
                if ("".equals(value1))
                    return new Double(0.0D);
                else
                    return Double.valueOf(value1);
            }

        };
        defaultTransformers.put(Double.TYPE, doubleTransform);
        defaultTransformers.put(Double.class, doubleTransform);
        Transformer<Object,BigDecimal> bigDecimalTransform = new Transformer<Object,BigDecimal>() {

            @Override
            public BigDecimal transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    BigDecimal.class.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return BigDecimal.valueOf(((Date) value).getTime());
                String value1 = value.toString();
                if ("".equals(value1))
                    return new BigDecimal(0);
                else
                    return new BigDecimal(value1);
            }

        };
        defaultTransformers.put(BigDecimal.class, bigDecimalTransform);
        Transformer<Object,BigInteger> bigIntegerTransform = new Transformer<Object,BigInteger>() {

            @Override
            public BigInteger transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    BigInteger.class.toString()).append("'.").toString());
                    }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return BigInteger.valueOf(((Date) value).getTime());
                String value1 = value.toString();
                if ("".equals(value1))
                    return BigInteger.ZERO;
                else
                    return new BigInteger(value1);
            }

        };
        defaultTransformers.put(BigInteger.class, bigIntegerTransform);
        Transformer<Object,java.sql.Date> javaSqlDateTransform = new Transformer<Object,java.sql.Date>() {

            @Override
            public java.sql.Date transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    java.sql.Date.class.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Date) value).getTime());
                if (java.sql.Time.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Time) value).getTime());
                if (java.sql.Timestamp.class.isAssignableFrom(valueClass))
                    return new java.sql.Date(((Timestamp) value).getTime());
                
                    throw new TransformException(
                        (new StringBuilder()).append("Can't covert type: ").append(
                            java.sql.Date.class.getName()).append(
                            " to java.sql.Date").toString());
                }

        };
        defaultTransformers.put(java.sql.Date.class, javaSqlDateTransform);
        Transformer<Object,java.sql.Time> javaSqlTimeTransform = new Transformer<Object,java.sql.Time>() {

            @Override
            public java.sql.Time transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    java.sql.Time.class.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new Time(((Date) value).getTime());
                if (java.sql.Date.class.isAssignableFrom(valueClass))
                    return new Time(((java.sql.Date) value).getTime());
                if (java.sql.Timestamp.class.isAssignableFrom(valueClass))
                    return new Time(((Timestamp) value).getTime());
                
                    throw new TransformException(
                        (new StringBuilder()).append("Can't covert type: ").append(
                            java.sql.Time.class.getName()).append(
                            " to java.sql.Time").toString());
            }

        };
        defaultTransformers.put(Time.class, javaSqlTimeTransform);
        Transformer<Object,java.sql.Timestamp> javaSqlTimestampTransform = new Transformer<Object,java.sql.Timestamp>() {

            @Override
            public java.sql.Timestamp transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
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
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    java.sql.Timestamp.class.toString()).append("'.").toString());
                    }
                }
                if (java.util.Date.class.isAssignableFrom(valueClass))
                    return new Timestamp(((Date) value).getTime());
                if (java.sql.Date.class.isAssignableFrom(valueClass))
                    return new Timestamp(((java.sql.Date) value).getTime());
                if (java.sql.Time.class.isAssignableFrom(valueClass))
                    return new Timestamp(((Time) value).getTime());
                    throw new TransformException((new StringBuilder()).append(
                        "Can't covert type: ").append(
                            java.sql.Timestamp.class.getName()).append(
                        " to java.sql.Timestamp").toString());
            }

        };
        defaultTransformers.put(Timestamp.class, javaSqlTimestampTransform);
        
        Transformer<Object,Date> dateTransform = new Transformer<Object,Date>() {

            @Override
            public Date transform(Object value) throws TransformException {
                Class<?> valueClass = value.getClass();
                if (java.lang.Number.class.isAssignableFrom(valueClass)) {
                    BigDecimal n = new BigDecimal(((Number) value).toString());
                    return new Date(n.longValue());
                }
                if (java.lang.String.class.isAssignableFrom(valueClass)) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                    try {
                        return df.parse(value.toString());
                    } catch (ParseException ex) {
                        throw new TransformException(
                            (new StringBuilder()).append("Value '").append(
                                value.toString()).append("' of type '").append(
                                valueClass.toString()).append(
                                "' can not be casted to type '").append(
                                    Date.class.toString()).append("'.").toString());
                    }
                }
                throw new TransformException((new StringBuilder()).append(
                    "Can't covert type: ").append(
                        Date.class.getName()).append(
                    " to java.sql.Timestamp").toString());
            }

        };
        defaultTransformers.put(Date.class, dateTransform);
        Transformer<Object,Map<String,Object>> mapTransform = new Transformer<Object,Map<String,Object>>() {

            @Override
            public Map<String,Object> transform(Object input) throws TransformException {
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
                  try {
                    return  DataUtils.getProperties(input, true);
                } catch (Exception e) {
                   throw new TransformException("get map property from object"+input.getClass(),e);
                }
                }else {
                    
                    throw new TransformException((new StringBuilder()).append(
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
