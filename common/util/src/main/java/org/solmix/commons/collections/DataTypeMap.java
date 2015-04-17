/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.commons.collections;

import static org.solmix.commons.util.DataUtils.commaSeparatedStringToList;
import static org.solmix.commons.util.DataUtils.getSubtreePrefixed;
import static org.solmix.commons.util.DataUtils.listToArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.map.AbstractMapDecorator;

/**
 * Provides a base datatype decorator that enables additional functionality to be added to a Map via decoration.
 * <p>
 * Methods are forwarded directly to the decorated map.
 * <p>
 * This implementation does not perform any special processing with {@link #entrySet()}, {@link #keySet()} or
 * {@link #values()}.
 * <p>
 * It simply returns the Set/Collection from the wrapped map.
 * 
 * @version 110035
 * @author solmix.f@gmail.com
 * @since 0.0.1
 */
public class DataTypeMap extends AbstractMapDecorator
{

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    public DataTypeMap()
    {
        super(new HashMap<Object,Object>());
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map
     */
    public DataTypeMap(Map<?,?> map)
    {
        super(map);
    }

    /**
     * return String value of the key
     * 
     * @param key
     * @return
     */
    public String getString(Object key) {
        return getString(key, null);
    }

    /**
     * @param key
     * @param defaultValue
     * @return return key's value if value is null return defaultValue.
     */
    public String getString(Object key, String defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        else
            return value.toString();
    }

    public DataTypeMap getSubtree(String key) {
        return new DataTypeMap(getSubtreePrefixed(key, this));
    }

    /**
     * @param key
     * @param defaultValue
     * @return return key's value if value is null return defaultValue.
     */
    public String[] getStringArray(Object key, String defaultValue[]) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        else
            return (String[]) listToArray(getList(key));
    }

    /**
     * @param key
     * @param defaultValue
     * @return return key's value if value is null return defaultValue.
     */
    public String[] getStringArray(String key, String defaultValue[]) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        else
            return (String[]) listToArray(getList(key));
    }

    /**
     * Gets the map being decorated.
     * 
     * @return the decorated map
     */
    public DataTypeMap getMap(Object key) {
        return getMap(key, null);
    }

    /**
     * Gets the map being decorated.
     * 
     * @param key
     * @param defaultValue
     * @return if value is null return default value.
     */
    public DataTypeMap getMap(Object key, Map<?, ?> defaultValue) {
        Object value = get(key);
        if (value instanceof DataTypeMap)
            return (DataTypeMap) value;
        if (value instanceof Map<?, ?>)
            return new DataTypeMap((Map<?, ?>) value);
        if (value == null) {
            if (defaultValue != null) {
                if (defaultValue instanceof DataTypeMap)
                    return (DataTypeMap) defaultValue;
                else
                    return new DataTypeMap(defaultValue);
            } else {
                return null;
            }
        } else {
            return new DataTypeMap((Map<?, ?>) value);
        }
    }

    /**
     * @param key
     * @return
     */
    public List<?> getList(Object key) {
        return getList(key, null);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public List<?> getList(Object key, List<?> defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof List<?>)
            return (List<?>) value;
        List<String> result = new ArrayList<String>();
        for (StringTokenizer st = new StringTokenizer(value.toString().trim(), " \r\t\n,"); st.hasMoreTokens(); result.add(st.nextToken().toString().trim()))
            ;
        return result;
    }

    /**
     * put comma separated objects into a List
     * 
     * @param key
     * @return
     */
    public List<?> getCommaSeparatedList(Object key) {
        return getCommaSeparatedList(key, null);
    }

    /**
     * put comma separated objects into a List
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public List<?> getCommaSeparatedList(Object key, List<?> defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof List<?>)
            return (List<?>) value;
        else
            return commaSeparatedStringToList(value.toString().trim());
    }

    /**
     * Provided a flexible way to get boolean value
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(Object key, boolean defaultValue) {
        return getBoolean(key, new Boolean(defaultValue)).booleanValue();
    }

    /**
     * Provided a flexible way to get boolean value
     * 
     * @param key
     * @return
     */
    public Boolean getBoolean(Object key) {
        return getBoolean(key, ((Boolean) (null)));
    }

    /**
     * Provided a flexible way to get boolean value
     * <p>
     * NOTE:String value "true","yes" return true; "false","no" return false.
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Boolean getBoolean(Object key, Boolean defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Boolean)
            return (Boolean) value;
        String s = value.toString().toLowerCase().trim();
        if (s.equals("true") || s.equals("yes")|| s.equals("1"))
            return new Boolean(true);
        if (s.equals("false") || s.equals("no")|| s.equals("0"))
            return new Boolean(false);
        else
            return defaultValue;
    }

    /**
     * @param key
     * @return
     */
    public Byte getByte(Object key) {
        return getByte(key, ((Byte) (null)));
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public byte getByte(Object key, byte defaultValue) {
        return getByte(key, new Byte(defaultValue)).byteValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Byte getByte(Object key, Byte defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Byte)
            return (Byte) value;
        else
            return new Byte(value.toString().trim());
    }

    public short getShort(Object key, short defaultValue) {
        return getShort(key, new Short(defaultValue)).shortValue();
    }

    /**
     * @param key
     * @return
     */
    public Short getShort(Object key) {
        return getShort(key, ((Short) (null)));
    }

    /**
     * get short type object if the key equal null return defaultValue
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Short getShort(Object key, Short defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Short)
            return (Short) value;
        else
            return new Short(value.toString().trim());
    }

    /**
     * @param key
     * @return
     */
    public Integer getInt(Object key) {
        return getInteger(key, ((Integer) (null)));
    }

    /**
     * @param key
     * @return
     */
    public Integer getInteger(Object key) {
        return getInteger(key, ((Integer) (null)));
    }

    public int getInt(Object key, int defaultValue) {
        return getInteger(key, new Integer(defaultValue)).intValue();
    }

    public int getInteger(Object key, int defaultValue) {
        return getInteger(key, new Integer(defaultValue)).intValue();
    }

    public Integer getInteger(Object key, Integer defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Integer)
            return (Integer) value;
        else
            return new Integer(value.toString().trim());
    }

    /**
     * @param key
     * @return
     */
    public Long getLong(Object key) {
        return getLong(key, ((Long) (null)));
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(Object key, long defaultValue) {
        return getLong(key, new Long(defaultValue)).longValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getLong(Object key, Long defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Long)
            return (Long) value;
        else
            return new Long(value.toString().trim());
    }

    public Float getFloat(Object key) {
        return getFloat(key, ((Float) (null)));
    }

    public float getFloat(Object key, float defaultValue) {
        return getFloat(key, new Float(defaultValue)).floatValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Float getFloat(Object key, Float defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Float)
            return (Float) value;
        else
            return new Float(value.toString().trim());
    }

    public Double getDouble(Object key) {
        return getDouble(key, ((Double) (null)));
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(Object key, double defaultValue) {
        return getDouble(key, new Double(defaultValue)).doubleValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Double getDouble(Object key, Double defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Double)
            return (Double) value;
        else
            return new Double(value.toString().trim());
    }

}
