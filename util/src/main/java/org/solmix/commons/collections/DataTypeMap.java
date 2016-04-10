/**
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Provides a base datatype decorator that enables additional functionality to
 * be added to a Map via decoration.
 * <p>
 * Methods are forwarded directly to the decorated map.
 * <p>
 * This implementation does not perform any special processing with
 * {@link #entrySet()}, {@link #keySet()} or {@link #values()}.
 * <p>
 * It simply returns the Set/Collection from the wrapped map.
 * 
 * @version 110035
 * @author solmix.f@gmail.com
 * @since 0.0.1
 */
public class DataTypeMap implements Map<String, Object> {

    protected transient Map<String, Object> map;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    public DataTypeMap() {
        this(new HashMap<String, Object>());
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map
     */
    public DataTypeMap(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        this.map = map;
    }
    
    public static DataTypeMap typeof(Map<String,Object> map){
        if(map instanceof DataTypeMap){
            return (DataTypeMap)map;
        }else{
            return new DataTypeMap(map);
        }
    }

    /**
     * return String value of the key
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * @param key
     * @param defaultValue
     * @return return key's value if value is null return defaultValue.
     */
    public String getString(Object key, String defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

    public DataTypeMap getSubtree(String key) {
        return new DataTypeMap(getSubtreePrefixed(key, this));
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
    public DataTypeMap getMap(String key) {
        return getMap(key, null);
    }

    /**
     * Gets the map being decorated.
     * 
     * @param key
     * @param defaultValue
     * @return if value is null return default value.
     */
    @SuppressWarnings("unchecked")
    public DataTypeMap getMap(String key, Map<String, Object> defaultValue) {
        Object value = get(key);
        if (value instanceof DataTypeMap) {
            return (DataTypeMap) value;
        }
        if (value instanceof Map<?, ?>) {
            return new DataTypeMap((Map<String, Object>) value);
        }
        if (value == null) {
            if (defaultValue != null) {
                if (defaultValue instanceof DataTypeMap) {
                    return (DataTypeMap) defaultValue;
                } else {
                    return new DataTypeMap(defaultValue);
                }
            } else {
                return null;
            }
        } else {
            return new DataTypeMap((Map<String, Object>) value);
        }
    }

    /**
     * @param key
     * @return key's value if value is null return null.
     */
    public List<?> getList(String key) {
        return getList(key, null);
    }

    /**
     * if values is String with tokenizer <code> \r\t\n,</code> split it to list.
     * 
     * @param key
     * @param defaultValue
     * @return  key's value if value is null return defaultValue.
     */
    public List<?> getList(String key, List<?> defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof List<?>)
            return (List<?>) value;
        List<String> result = new ArrayList<String>();
        for (StringTokenizer st = new StringTokenizer(value.toString().trim(),
            " \r\t\n,"); st.hasMoreTokens(); result.add(st.nextToken().toString().trim()))
            ;
        return result;
    }

    /**
     * put comma separated objects into a List
     * 
     * @param key
     * @return
     */
    public List<?> getCommaSeparatedList(String key) {
        return getCommaSeparatedList(key, null);
    }

    /**
     * put comma separated objects into a List
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public List<?> getCommaSeparatedList(String key, List<?> defaultValue) {
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
    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, new Boolean(defaultValue)).booleanValue();
    }

    /**
     * Provided a flexible way to get boolean value
     * 
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
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
    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Boolean)
            return (Boolean) value;
        String s = value.toString().toLowerCase().trim();
        if (s.equals("true") || s.equals("yes"))
            return new Boolean(true);
        if (s.equals("false") || s.equals("no"))
            return new Boolean(false);
        else
            return defaultValue;
    }

    /**
     * @param key
     * @return
     */
    public Byte getByte(String key) {
        return getByte(key, ((Byte) (null)));
    }

    public char getChar(String key) {
        return getChar(key, (Character) null);
    }

    public Date getDate(String key) {
        return getDate(key, null);
    }

    public Date getDate(String key, Date defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Date)
            return (Date) value;
        else {
            return null;
        }
    }

    public char getChar(String key, Character defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Character)
            return (Character) value;
        else if (value.toString().trim().length() > 1) {
            return value.toString().trim().charAt(0);
        } else {
            return (char) 0;
        }

    }
    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public byte getByte(String key, byte defaultValue) {
        return getByte(key, new Byte(defaultValue)).byteValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Byte getByte(String key, Byte defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Byte)
            return (Byte) value;
        else
            return new Byte(value.toString().trim());
    }

    public short getShort(String key, short defaultValue) {
        return getShort(key, new Short(defaultValue)).shortValue();
    }

    /**
     * @param key
     * @return
     */
    public Short getShort(String key) {
        return getShort(key, ((Short) (null)));
    }

    /**
     * get short type object if the key equal null return defaultValue
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Short getShort(String key, Short defaultValue) {
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
    public Integer getInt(String key) {
        return getInteger(key, ((Integer) (0)));
    }

    /**
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        return getInteger(key, ((Integer) (null)));
    }

    public int getInt(String key, int defaultValue) {
        return getInteger(key, new Integer(defaultValue)).intValue();
    }

    public int getInteger(String key, int defaultValue) {
        return getInteger(key, new Integer(defaultValue)).intValue();
    }

    public Integer getInteger(String key, Integer defaultValue) {
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
    public Long getLong(String key) {
        return getLong(key, ((Long) (null)));
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(String key, long defaultValue) {
        return getLong(key, new Long(defaultValue)).longValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getLong(String key, Long defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Long)
            return (Long) value;
        else
            return new Long(value.toString().trim());
    }

    public Float getFloat(String key) {
        return getFloat(key, ((Float) (null)));
    }

    public float getFloat(String key, float defaultValue) {
        return getFloat(key, new Float(defaultValue)).floatValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Float getFloat(String key, Float defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Float)
            return (Float) value;
        else
            return new Float(value.toString().trim());
    }

    public Double getDouble(String key) {
        return getDouble(key, ((Double) (null)));
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(String key, double defaultValue) {
        return getDouble(key, new Double(defaultValue)).doubleValue();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public Double getDouble(String key, Double defaultValue) {
        Object value = get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Double)
            return (Double) value;
        else
            return new Double(value.toString().trim());
    }

    protected Map<String, Object> getMap() {
        return map;
    }

    // -----------------------------------------------------------------------
    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> mapToCopy) {
        map.putAll(mapToCopy);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return map.equals(object);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
