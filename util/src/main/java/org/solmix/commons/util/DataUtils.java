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

package org.solmix.commons.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.collections.CaseInsensitiveHashMap;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.io.SlxFile;

/**
 * @version 110043
 */
@SuppressWarnings("rawtypes")
public final class DataUtils
{


    public static final Object EMPTY_ARRAY[] = new Object[0];

    private static final Logger log = LoggerFactory.getLogger(DataUtils.class.getName());

//    private static Perl5Util globalPerl = new Perl5Util();

    public static boolean useMimeTypesJS = false;

    public static DataTypeMap mimeTypesJS = null;

    /**
     * 根据List 中的key去出Map中该key对应的值，重新放入一个Map中
     * 
     * @param origMap
     * @param keys
     * @return
     */
    public static <V,T>Map<V,T> subsetMap(Map<V,T> origMap, List<V> keys) {
        if (origMap == null || keys == null)
            return null;
        Map<V,T> newMap = new HashMap<V,T>();
        for (V key : keys) {
            if (origMap.containsKey(key)) {
                newMap.put(key, origMap.get(key));
            }
        }
        return newMap;
    }

    public static void incrementIntInMap(Map map, Object key) {
        addToIntInMap(map, key, 1);
    }

    /**
     * put the value of List two which not contains in one into List one.
     * 
     * @param one
     * @param two
     */

    public static void addDisjunctionToSet(List one, List two) {
        if (one == null || two == null)
            return;
        for (Object o : two) {
            if (!one.contains(o)) {
                one.add(o);
            }
        }
    }

    public static void addToIntInMap(Map map, Object key, int addition) {
        map.put(key, new Integer(getIntInMap(map, key) + addition));
    }

    public static int getIntInMap(Map map, Object key) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        Object value = map.get(key);
        if (value == null)
            return 0;
        if (!(value instanceof Number))
            throw new IllegalArgumentException("Value in map is not a java.lang.Number");
        else
            return ((Number) value).intValue();
    }

    public static <T>List<T> enumToList(Iterator<T> i) {
        if (i == null)
            return null;
        List<T> list = new ArrayList<T>();
        for (; i.hasNext(); list.add(i.next()))
            ;
        return list;
    }

    public static boolean isIdentifier(String str) {
        if (str == null || str.length() < 2)
            return false;
        if (!Character.isJavaIdentifierStart(str.charAt(0)))
            return false;
        for (int i = 1; i < str.length(); i++)
            if (!Character.isJavaIdentifierPart(str.charAt(i)))
                return false;

        return true;
    }

    public static Map identityMap(List list) {
        if (list == null)
            return null;
        Map map = new HashMap();
        for (Object o : list) {
            map.put(o, o);
        }
        return map;
    }


    /**
     * Return Boolean .
     * 
     * @param value would be Boolean, Number,String.
     * @return
     */
    public static Boolean asBooleanObject(Object value) {
        if (value == null)
            return null;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return new Boolean(((Number) value).intValue() > 0);
        
        String s = value.toString().toLowerCase().trim();
        if (s.equals("true") || s.equals("yes"))
            return new Boolean(true);
        if (s.equals("false") || s.equals("no"))
            return new Boolean(false);
        else
            return null;
    }

    public static Double asDouble(Object obj) {
        if (obj instanceof Double)
            return ((Double) obj).doubleValue();
        else {
                return (new Double(obj.toString())).doubleValue();
            }
    }

    /**
     * 翻转key = value为value =key
     * 
     * @param <V>
     * @param <K>
     * 
     * @param origMap
     * @return
     */
    public static Map<Object, Object> reverseMap(Map<Object, Object> origMap) {
        if (origMap == null)
            return null;
        Map<Object, Object> reverseMap = new HashMap<Object, Object>();
        for (Object key : origMap.keySet()) {
            putMultiple(reverseMap, origMap.get(key), key);
        }
        return reverseMap;
    }

    /**
     * 把sourceList 添加到targetList中去，如果soureList是List类型，就把List中的全体加入targetList
     * 
     * @param targetList
     * @param sourceList
     * @return
     */
    public static List addAsList(List targetList, Object sourceList) {
        if (sourceList == null)
            return targetList;
        if (!(sourceList instanceof List)) {
            targetList.add(sourceList);
            return targetList;
        } else {
            return addAll(targetList, (List) sourceList);
        }
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public static Object combineAsLists(Object one, Object two) {
        if (one == null)
            return two;
        if (two == null) {
            return one;
        } else {
            List combinedList = new ArrayList();
            addAsList(combinedList, one);
            addAsList(combinedList, two);
            return combinedList;
        }
    }

    /**
     * @param <K>
     * @param <V>
     * @param one
     * @param two
     * @return
     */
    public static <K, V> List<K> mapIntersectionKeys(Map<K, V> one, Map<K, V> two) {
        List<K> result = new ArrayList<K>();
        if (one == null || two == null)
            return result;
        Map<K, V> iterMap = one;
        Map<K, V> compMap = two;
        if (two.size() < one.size()) {
            iterMap = two;
            compMap = one;
        }
        for (K key : iterMap.keySet()) {
            if (compMap.get(key) != null) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * @param <K>
     * @param <V>
     * @param one
     * @param two
     * @return
     */
    public static <K, V> List<V> mapIntersectionValues(Map<K, V> one, Map<K, V> two) {
        List<V> result = new ArrayList<V>();
        if (one == null || two == null)
            return result;
        Map<K, V> iterMap = one;
        Map<K, V> compMap = two;
        if (two.size() < one.size()) {
            iterMap = two;
            compMap = one;
        }
        for (Object key : iterMap.keySet()) {
            if (compMap.get(key) != null) {
                result.add(compMap.get(key));
            }
        }
        return result;
    }

    /**
     * generation new Map from a Collection of Map by specify the property of the Map in Collection
     * 
     * @param objects
     * @param propertyName
     * @return
     */
    public static Map indexOnProperty(Collection objects, String propertyName) {
        return makeIndex(objects, propertyName);
    }

    /**
     * generation new Map from a Collection of Map by specify the property of the Map in Collection
     * 
     * @param objects
     * @param propertyName
     * @return
     */
    public static Map makeIndex(Collection objects, String propertyName) {
        if (objects == null)
            return null;
        Map index = new LinkedHashMap();
        for (Object object : objects) {
            if (object instanceof Map) {
                Map propertyMap = (Map) object;
                Object propertyValue = propertyMap.get(propertyName);
                if (propertyValue != null)
                    index.put(propertyValue, propertyMap);
            }
        }
        return index;
    }

    /**
     * source中的全部内容加入target
     * 
     * @param target
     * @param source
     * @return
     */
    public static List addAll(List target, List source) {
        if (source == null)
            return target;
        if (target == null) {
            return null;
        } else {
            Iterator elems = source.iterator();
            return addAll(target, elems);
        }
    }

    public static List addAll(List target, Iterator source) {
        if (source == null)
            return target;
        for (; source.hasNext(); target.add(source.next()))
            ;
        return target;
    }

    public static String hashValue(String plaintext, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(plaintext.getBytes());
        byte cipherbytes[] = md.digest();
        BigInteger work = new BigInteger(1, cipherbytes);
        String ciphertext = work.toString(16);
        if (algorithm.equals("MD5"))
            ciphertext = (new StringBuilder()).append("00000000000000000000000000000000".substring(ciphertext.length())).append(ciphertext).toString();
        else if (algorithm.equals("SHA"))
            ciphertext = (new StringBuilder()).append("0000000000000000000000000000000000000000".substring(ciphertext.length())).append(ciphertext).toString();
        return ciphertext;
    }

    /**
     * Get subset form {@link #getPrefixed(String, Map)} by prefix.
     * 
     * @param prefix
     * @param data
     * @return return the subset ,but remove the prefix
     */
    public static Map<String, Object> getSubtreePrefixed(String prefix, Map<String, Object> data) {
        if (prefix == null || data == null)
            return null;
        if (prefix.length() == 0)
            return data;
        Map<String, Object> result = new HashMap<String, Object>();
        for (Object o : data.keySet()) {
            if (o instanceof String) {

                String key = (String) o;
                if (key.startsWith(prefix + "."))
                    result.put(key.substring(prefix.length() + 1), data.get(key));
            }
        }
        return result;
    }

    public static String formatFileSize(long fileSize) {
        String suffix = null;
        if (fileSize < 1024L)
            suffix = "B";
        else if (fileSize < 0x100000L) {
            fileSize = Math.round(fileSize / 1024L);
            suffix = "KB";
        } else {
            fileSize = Math.round((fileSize / 0x100000L) * 100L) / 100;
            suffix = "MB";
        }
        return (new StringBuilder()).append(fileSize).append("&nbsp;").append(suffix).toString();
    }

    public static boolean getBoolean(Map map, Object key) {
        Object value = map.get(key);
        return asBoolean(value);
    }

    @SuppressWarnings("rawtypes")
    public static Integer getInteger(Dictionary p, Object key, Integer defaultValue) {
        Object value = p.get(key);
        if (value == null)
            return defaultValue;
        if (value instanceof Integer)
            return (Integer) value;
        else
            return new Integer(value.toString().trim());
    }

    public static String getString(Dictionary p, Object key, String defaultValue) {
        Object value = p.get(key);
        if (value == null)
            return defaultValue;
        else
            return value.toString();
    }

    public static Boolean getBoolean(Dictionary p, Object key, Boolean defaultValue) {
        Object value = p.get(key);
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


    public static Long getLong(Map map, Object key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Long)
            return ((Long) value).longValue();
        else {
            try {
                return (new Long(value.toString())).longValue();
            } catch (NumberFormatException e) {
                log.debug("TypeChangeError:", e);
                return null;
            }
        }
    }

    public static Double getDouble(Map map, Object key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Double)
            return ((Double) value);
        else {
            try {
                return (new Double(value.toString()));
            } catch (NumberFormatException e) {
                log.debug("TypeChangeError:", e);
                return null;
            }
        }
    }

    /**
     * convert a object value to boolean value.
     * 
     * @param value
     * @return
     */
    public static boolean asBoolean(Object value) {
        Boolean b=asBooleanObject(value);
        return b==null?false:b.booleanValue();
    }

    /**
     * 传入对象是List就转化为List返回，否则就新建一个List把该对象加入这个List再返回
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> makeListIfSingle(T obj) {
        if (obj == null)
            return null;
        else if (obj instanceof List<?>){
            return (List<T>) obj;
        }else if(obj.getClass().isArray()){
           List<T> alist= new ArrayList<T>();
           int l= Array.getLength(obj);
           for(int i=0;i<l;i++){
               alist.add((T)Array.get(obj, i));
           }
           return alist;
            
        }else
            return makeList(obj);
    }

    /**
     * 实例有个List对象并添加element值
     * 
     * @param <T>
     * @param element
     * @return
     */
    public static <T> List<T> makeList(T element) {
        List<T> result = new ArrayList<T>();
        if (element == null) {
            return result;
        } else {
            result.add(element);
            return result;
        }
    }

    /**
     * re-mapping rows to new map.
     * @param <V>
     * @param <T>
     * 
     * @param rows List of maps.
     * @param remap
     * @param keepNonRemapped
     * @return
     */
    public static List<Map<Object,Object>> remapRows(List<Map<Object,Object>> rows, Map<String, String> remap, boolean keepNonRemapped) {
        if (remap == null)
            return rows;
        List<Map<Object,Object>> newRows = new ArrayList<Map<Object,Object>>();
        for (Map<Object,Object> oldRow : rows) {
            Map<Object,Object> newRow = remapRow(oldRow, remap, keepNonRemapped);
            if (newRow.size() > 0)
                newRows.add(newRow);
        }
        return newRows;
    }

    public static  Map<Object, Object> remapRow(Map<Object,Object> row, Map<String, String> remap, boolean keepNonRemapped) {
        if (remap == null)
            return row;
        Map<Object,Object> newRow = new HashMap<Object,Object>();
        for (Object oldKey : row.keySet()) {
            Object newKey = remap.get(oldKey);
            Object data = row.get(oldKey);
            if (newKey == null) {
                if (keepNonRemapped)
                    newRow.put(oldKey, data);
            } else {
                newRow.put(newKey, data);
            }
        }
        return newRow;
    }

    /**
     * 将value放入key中，如果key已经存在了，就把value作为一个List 放入key中
     * 
     * @param map
     * @param key
     * @param value
     * @return
     */
    public static Map<Object, Object> putMultiple(Map<Object, Object> map, Object key, Object value) {
        Object existingValue = map.get(key);
        if (existingValue == null)
            map.put(key, value);
        else if (existingValue instanceof List)
            ((List) existingValue).add(value);
        else
            map.put(key, buildList(existingValue, value));
        return map;
    }

    /**
     * 在map中加入key = value键值，如果已经存在key，就合并到一个List中 <b>note:</b>The difference from
     * {@link #putMultiple(Map, Object, Object)} is that putMultiple add the key=value if the key's value is List object
     * the direct insert into ,not build new list to contain the List and the the new value.
     * 
     * @param map
     * @param key
     * @param value
     * @return
     */
    public static Map putCombinedList(Map map, Object key, Object value) {
        if (key == null)
            throw new IllegalArgumentException("putCombinedList passed null key");
        Object existingValue = map.get(key);
        Object combinedList = combineAsLists(existingValue, value);
        if (combinedList != null)
            map.put(key, combinedList);
        return map;
    }

    /**
     * @param list
     * @return Object array
     */

    public static String[] listToStringArray(Collection<String> list) {
        if (list == null) {
            return null;
        } else {
            String valueArr[] = new String[list.size()];
            list.toArray(valueArr);
            return valueArr;
        }
    }

    public static <T> T[] arrayAdd(T[] target, T... source) {
        if (target == null)
            return null;
        List<T> res = new ArrayList<T>();
        for (T tar : target)
            res.add(tar);
        for (T sor : source)
            res.add(sor);
        return res.toArray(target);

    }

    public static List<Object> arrayToList(Object arr[], int from, int length) {
        if (arr == null)
            return null;
        List<Object> list = new ArrayList<Object>();
        for (int i = from; i < length; i++)
            list.add(arr[i]);

        return list;
    }

    public static List<Object> arrayToList(Object arr[]) {
        if (arr == null)
            return null;
        else
            return arrayToList(arr, 0, arr.length);
    }

    /**
     * Convert List<String> to Array[]
     * 
     * @param list
     * @return String array
     */
    public static Object[] listToArray(List<?> list) {
        if (list == null) {
            return null;
        } else {
            Object valueArr[] = new Object[list.size()];
            list.toArray(valueArr);
            return valueArr;
        }
    }

    /**
     * put comma separated objects into a List
     * 
     * @param value
     * @return
     */
    public static List<String> commaSeparatedStringToList(String value) {
        if (value == null || value.trim().equals(""))
            return null;
        List<String> result = new ArrayList<String>();
        for (StringTokenizer st = new StringTokenizer(value, ","); st.hasMoreTokens(); result.add(st.nextToken().toString().trim()))
            ;
        return result;
    }

    /**
     * return target array contains source values.
     * 
     * @param <T>
     * @param source
     * @param target
     * @return
     */
    public static <T> List<T> arrayContains(T[] source, T[] target) {
        List<T> res = new ArrayList<T>();
        for (T t : target) {
            for (T s : source) {
                if (t.equals(s))
                    res.add(s);
            }
        }
        return res;

    }

    /**
     * @param source
     * @param targer
     * @param nullMode when true merge null value of target,else merge all.
     * @throws Exception 
     */
    public static void beanMerge(Object source, Object target, boolean nullMode) throws Exception {
        if(source==null)
            throw new IllegalArgumentException("source is null");
        if(target==null)
            throw new IllegalArgumentException("target is null");
        Field[] _sf = source.getClass().getDeclaredFields();
        Field[] _tf = target.getClass().getDeclaredFields();
        Map<String, Object> _samefield = new HashMap<String, Object>();
        for (Field sf : _sf)
            for (Field tf : _tf)
                if (sf.getName() == tf.getName() && sf.getType().equals(tf.getType()))
                    _samefield.put(tf.getName(), tf.getType());
       setProperties( getProperties(source, _samefield.keySet(), true),target);
    }

    /**
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> mapMerge(Map<K, V> source, Map<K, V> target) {
        if (target == null)
            return null;
        if (source == null)
            return target;
        for (K key : source.keySet()) {
            target.put(key, source.get(key));
        }
        return target;
    }

    public static Map getPrefixed(String prefix, Map<String, Object> data) {
        if (prefix == null || data == null)
            return null;
        if (prefix.length() == 0)
            return data;
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : data.keySet()) {
            if (key.toString().startsWith((new StringBuilder()).append(prefix).append(".").toString()))
                result.put(key, data.get(key));
        }
        return result;
    }

    /**
     * @param lists
     * @return
     */
    public static List<Object> buildList(List<Object>... lists) {
        List<Object> result = new ArrayList<Object>();
        for (List<Object> list : lists) {
            result.add(list);
        }
        return result;
    }

    /**
     * Build array object into List
     * 
     * @param lists
     * @return
     */
    public static List<Object> buildList(Object... lists) {
        List<Object> result = new ArrayList<Object>();
        for (Object list : lists) {
            result.add(list);
        }
        return result;
    }

    public static Map buildMap(Object key, Object value) {
        return buildMap(key, value, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * @param key
     * @param value
     * @param key2
     * @param value2
     * @return
     */
    public static Map buildMap(Object key, Object value, Object key2, Object value2) {
        return buildMap(key, value, key2, value2, null, null, null, null, null, null, null, null);
    }

    public static Map buildMap(Object key, Object value, Object key2, Object value2, Object key3, Object value3) {
        return buildMap(key, value, key2, value2, key3, value3, null, null, null, null, null, null);
    }

    public static Map buildMap(Object key, Object value, Object key2, Object value2, Object key3, Object value3, Object key4, Object value4) {
        return buildMap(key, value, key2, value2, key3, value3, key4, value4, null, null, null, null);
    }

    public static Map buildMap(Object key, Object value, Object key2, Object value2, Object key3, Object value3, Object key4, Object value4,
        Object key5, Object value5) {
        return buildMap(key, value, key2, value2, key3, value3, key4, value4, key5, value5, null, null);
    }

    public static Map buildMap(Object key, Object value, Object key2, Object value2, Object key3, Object value3, Object key4, Object value4,
        Object key5, Object value5, Object key6, Object value6) {
        Map result = new HashMap();
        if (key != null)
            result.put(key, value);
        if (key2 != null)
            result.put(key2, value2);
        if (key3 != null)
            result.put(key3, value3);
        if (key4 != null)
            result.put(key4, value4);
        if (key5 != null)
            result.put(key5, value5);
        if (key6 != null)
            result.put(key6, value6);
        return result;
    }

    public static Map buildMap(Object key, Object value, Object key2, Object value2, Object key3, Object value3, Object key4, Object value4,
        Object key5, Object value5, Object key6, Object value6, Object key7, Object value7) {
        Map result = new HashMap();
        if (key != null)
            result.put(key, value);
        if (key2 != null)
            result.put(key2, value2);
        if (key3 != null)
            result.put(key3, value3);
        if (key4 != null)
            result.put(key4, value4);
        if (key5 != null)
            result.put(key5, value5);
        if (key6 != null)
            result.put(key6, value6);
        if (key7 != null)
            result.put(key7, value7);
        return result;
    }

    /**
     * when the uri String start with "file:","jar:","http:","https:" return true.
     * 
     * @param uri
     * @return
     */
    public static boolean isURI(String uri) {
        return uri.startsWith("file:") || uri.startsWith("jar:") || uri.startsWith("http:") || uri.startsWith("https:");
    }

    /**
     * check whether the file is exits.
     * 
     * @param filename a filename or a path to file
     * @param file
     * @return whether the file exits.
     * @throws IOException
     */
    public static boolean caseSensitiveFileExists(String filename, File file) throws IOException {
        if (file.exists()) {
            if (filename == null)
                return true;
            filename = SlxFile.canonicalizePath(filename);
            int slashIndex = filename.lastIndexOf("/");
            if (slashIndex != -1)
                filename = filename.substring(slashIndex + 1);
            return filename.equals(file.getCanonicalFile().getName());
        } else {
            return false;
        }
    }

    public static String prettyPrint(String str) {
        try {
            String prefix = "";
            String cov = "\n";
            StringBuffer sb = new StringBuffer(str);
            for (int i = 0; i < sb.length(); i++) {
                String ins = cov + prefix;
                if (i != 0 && sb.charAt(i) == '{') {
                    prefix += "  ";
                    ins = cov + prefix;
                    sb.insert(i, ins);
                    sb.insert(i + 1 + ins.length(), ins);
                    i = i + ins.length() + ins.length();
                } else if (sb.charAt(i) == '}') {
                    sb.insert(i - 1 + 1, ins);
                    i = i + ins.length();
                    prefix = prefix.length() < 2 ? prefix : prefix.substring(2);
                } else if (sb.charAt(i) == ',') {
                    sb.insert(i + 1, ins);
                    i = i + ins.length();
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * Print {@link java.lang.Throwable} as string
     * 
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        try {
            sw.close();
        } catch (Exception e) {
        }
        return sw.toString();
    }

    /**
     * return bean's propertyDescriptor.
     * 
     * @param bean
     * @return
     * @throws Exception
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Object bean) throws Exception {
        if (bean == null)
            return null;
        else
            return getPropertyDescriptors(bean.getClass());
    }

    public static Map<String, Object> getProperties(Object bean) throws Exception {
        return getProperties(bean, (Collection<String>) null);
    }

    public static Map<String, Object> getProperties(Object bean, boolean omitNullValue) throws Exception {
        return getProperties(bean, (Collection<String>) null, omitNullValue);
    }

    /**
     * Return Property Type of given class.
     * 
     * @param c bean class .
     * @param propertyName property of the bean class
     * @return property type class
     * @throws IntrospectionException
     */
    public static Class<?> getPropertyType(Class<?> c, String propertyName) throws IntrospectionException {
        if (c == null)
            throw new NullPointerException("Class parameter can not be null");
        if (propertyName == null)
            throw new NullPointerException("Property name parameter can not be null");
        if ("".equals(propertyName.trim()))
            throw new IntrospectionException("Property name parameter can not be empty");
        String path[] = propertyName.split("\\.");
        Class<?> propertyType = null;
        Field fields[] = c.getDeclaredFields();
        int i = 0;
        int length = fields != null ? fields.length : 0;
        do {
            if (i >= length)
                break;
            Field field = fields[i];
            if (path[0].equals(field.getName())) {
                propertyType = field.getType();
                break;
            }
            i++;
        } while (true);

        if (propertyType == null) {
            Method methods[] = c.getMethods();
            Method arr$[] = methods;
            int len$ = arr$.length;
            int i$ = 0;
            do {
                if (i$ >= len$)
                    break;
                Method method = arr$[i$];
                if (path[0].equals(getterName(method))) {
                    propertyType = method.getReturnType();
                    break;
                }
                i$++;
            } while (true);
        }
        if (propertyType == null) {
            fields = getClassFields(c);
            length = fields.length;
            i = 0;
            do {
                if (i >= length)
                    break;
                Field field = fields[i];
                if (path[0].equals(field.getName())) {
                    propertyType = field.getType();
                    break;
                }
                i++;
            } while (true);
        }
        if (propertyType == null) {
            Method methods[] = getClassMethods(c);
            Method arr$[] = methods;
            int len$ = arr$.length;
            int i$ = 0;
            do {
                if (i$ >= len$)
                    break;
                Method method = arr$[i$];
                if (path[0].equals(getterName(method))) {
                    propertyType = method.getReturnType();
                    break;
                }
                i$++;
            } while (true);
        }
        if (propertyType == null)
            throw new IntrospectionException(
                (new StringBuilder()).append("Property \"").append(path[0]).append("\" is not found in class \"").append(c.getName()).append("\"").toString());
        if (path.length > 1) {
            String subName = "";
            for (i = 1; i < path.length; i++) {
                if (!"".equals(subName))
                    subName = (new StringBuilder()).append(subName).append(".").toString();
                subName = (new StringBuilder()).append(subName).append(path[i]).toString();
            }

            if (!propertyType.isPrimitive())
                return getPropertyType(propertyType, subName);
            else
                throw new IntrospectionException(
                    (new StringBuilder()).append("Found primitive property \"").append(path[0]).append("\" in class \"").append(c.getName()).append(
                        "\". It can not have subproperty \"").append(subName).append("\"").toString());
        } else {
            return propertyType;
        }
    }

    public static String getterName(Method method) {
        if (method.getParameterTypes().length > 0)
            return null;
        String methodName = method.getName();
        if (methodName.startsWith("get"))
            methodName = methodName.substring(3);
        else if (methodName.startsWith("is"))
            methodName = methodName.substring(2);
        else
            return null;
        if (methodName.length() > 0) {
            methodName = (new StringBuilder()).append(methodName.substring(0, 1).toLowerCase()).append(methodName.substring(1)).toString();
            return methodName;
        } else {
            return null;
        }
    }

    public static Field[] getClassFields(Class c) {
        if (c == null) {
            return new Field[0];
        } else {
            Field declaredFields[] = c.getDeclaredFields();
            Field superclassFields[] = getClassFields(c.getSuperclass());
            Field fields[] = new Field[declaredFields.length + superclassFields.length];
            System.arraycopy(declaredFields, 0, fields, 0, declaredFields.length);
            System.arraycopy(superclassFields, 0, fields, declaredFields.length, superclassFields.length);
            return fields;
        }
    }

    public static Method[] getClassMethods(Class c) {
        if (c == null) {
            return new Method[0];
        } else {
            Method declaredMethods[] = c.getDeclaredMethods();
            Method superclassMethods[] = getClassMethods(c.getSuperclass());
            Method methods[] = new Method[declaredMethods.length + superclassMethods.length];
            System.arraycopy(declaredMethods, 0, methods, 0, declaredMethods.length);
            System.arraycopy(superclassMethods, 0, methods, declaredMethods.length, superclassMethods.length);
            return methods;
        }
    }

    /**
     * 通过java反射机制，直接从pojo样式的java对象中获取字段值。
     * 
     * @param bean
     * @param propertyName
     * @return
     */
    public static Object getProperty(Object bean, String propertyName) {
        if (bean == null || propertyName == null)
            return null;
        List<String> collection = new ArrayList<String>(1);
        collection.add(propertyName);
        Object __return = null;
        try {
            Map<String, Object> values = getProperties(bean, collection);
            __return = values.get(propertyName);
        } catch (Exception e) {
            log.error("" + e);
        }
        return __return;
    }

    /**
     * Get property value of the objects.fist found in object[0],if no found ,move to object[1],and so on.
     * 
     * @param propertyName
     * @param objects
     * @return
     */
    public static Object getProperty(String propertyName, Object... objects) {
        if (isNullOrEmpty(objects))
            return null;
        Object __return = null;
        for (int i = 0; i < objects.length; i++) {
            __return = getProperty(objects[i], propertyName);
            if (__return != null) {
                break;
            }
        }
        return __return;
    }

    public static Map<String, Object> getProperties(Object bean, Collection<String> propsToKeep) throws Exception {
        return getProperties(bean, propsToKeep, false);
    }

    public static Map<String, Object> getProperties(Object bean, Collection<String> propsToKeep, boolean omitNullValue) throws Exception {
        if (bean == null)
            return null;
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor propertyDescriptors[] = beanInfo.getPropertyDescriptors();
        if (propertyDescriptors == null)
            return Collections.emptyMap();
        Map<String, Object> propertyMap = new HashMap<String, Object>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if (propertyDescriptor == null)
                continue;
            String propertyName = propertyDescriptor.getName();
            if (propsToKeep != null && !propsToKeep.contains(propertyName))
                continue;
            if("class".equals(propertyName))
                continue;
            Method getter = propertyDescriptor.getReadMethod();
            if (getter == null 
                && propertyDescriptor.getPropertyType()!=null&&
                Boolean.class.isAssignableFrom(propertyDescriptor.getPropertyType().getClass())) {
                String methodName = new StringBuilder().append("is")
                    .append(propertyName.substring(0, 1).toUpperCase())
                    .append( propertyName.substring(1)).toString();
                try {
                    getter = bean.getClass().getMethod(methodName, new Class[0]);
                } catch (Exception e) {//Ignore method nofound.
                }
            }
            if (getter == null || !Modifier.isPublic(getter.getModifiers()))
                continue;
            Object value;
            try {
                value = getter.invoke(bean, EMPTY_ARRAY);
            } catch (Throwable t) {
                String error = getStackTrace(t);
                log.debug((new StringBuilder()).append("Bean inspection: invocation of ").append(bean.getClass().getName()).append(".").append(
                    getter.getName()).append("() while trying to obtain").append(" property '").append(propertyName).append("' threw an exception: ").append(
                    error).append("\nSetting value to the error string and continuing").toString());
                value = t.toString();
            }
            if (value == null && omitNullValue) {
            } else {
                propertyMap.put(propertyName, value);
            }
        }

        return propertyMap;
    }

    /**
     * return bean's propertyDescriptor.
     * 
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class beanClass) throws Exception {
        if (beanClass == null)
            return null;
        Map<String, PropertyDescriptor> properties = new Hashtable<String, PropertyDescriptor>();
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor propertyDescriptors[] = beanInfo.getPropertyDescriptors();
        if (propertyDescriptors == null)
            return properties;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor != null) {
                String propertyName = propertyDescriptor.getName();
                if(!"class".equals(propertyName))
                properties.put(propertyName, propertyDescriptor);
            }
        }
        return properties;
    }

    /**
     * directly inject map value to pojo's style bean.
     * 
     * @param propertyMap
     * @param bean
     * @return
     * @throws Exception
     */
    public static Object setProperties(Map propertyMap, Object bean) throws Exception {
        return setProperties(propertyMap, bean, true);
    }

    /**
     * directly inject map value to pojo's style bean.
     * 
     * @param propertyMap
     * @param bean
     * @param CaseSensitive if true property matched case sensitive.if false then case insensitive.
     * @return
     * @throws Exception
     */
    public static Object setProperties(Map propertyMap, Object bean, boolean caseSensitive) throws Exception {
        if (bean == null) {
            log.error("Null bean passed to setProperties, returning null", new Exception());
            return null;
        }
        if (propertyMap == null) {
            if(log.isDebugEnabled()){
                log.debug((new StringBuilder()).append("Null propertyMap passed to setProperties for bean of type: ")
                    .append(bean.getClass().getName()).append(" returning bean unmodified.").toString());
            }
            return bean;
        }
        Map properties = null;
        Map<String, PropertyDescriptor> senstive = getPropertyDescriptors(bean);
        if (!caseSensitive) {
            CaseInsensitiveHashMap insenstive = new CaseInsensitiveHashMap();
            insenstive.putAll(senstive);
            properties = insenstive;
        } else {
            properties = senstive;
        }
        Map<String, String> badProperties = null;
        for (Object o : propertyMap.keySet()) {
            String propertyName = (String) o;
            Object value = propertyMap.get(propertyName);
            PropertyDescriptor property = (PropertyDescriptor) properties.get(propertyName);
            if (property == null) {
                if (badProperties == null)
                    badProperties = new HashMap<String, String>();
                badProperties.put(propertyName, "No such property");
                continue;
            }
            Method writeMethod = property.getWriteMethod();
            if (writeMethod == null || !Modifier.isPublic(writeMethod.getModifiers())) {
                if (badProperties == null)
                    badProperties = new HashMap<String, String>();
                badProperties.put(propertyName, "No accessible setter method");
                continue;
            }
            Object arguments[] = null;
            try {
                arguments = createMethodArguments(writeMethod, value, propertyName);
            } catch (IllegalArgumentException e) {
                if (badProperties == null)
                    badProperties = new HashMap<String,String>();
                badProperties.put(propertyName, (new StringBuilder()).append("Exception invoking setter method: ").append(e).toString());
                continue;
            }
            writeMethod.invoke(bean, arguments);
        }
        if (badProperties != null && log.isDebugEnabled()) {
            StringBuffer __info = new StringBuffer();
            for (String str : badProperties.keySet())
                __info.append("[" + str + " : " + badProperties.get(str) + "] ");
            log.debug((new StringBuilder()).append("setProperties: couldn't set:\n").append(__info.toString()).toString());
        }
        return bean;
    }

    /**
     * copy the properties of source object to target object.
     * 
     * @param source
     * @param target
     * @param properties
     * @throws Exception
     */
    public static Object copyProperties(Object source, Object target, Collection<String> properties) throws Exception {
        if (target == null) {
            log.error("Null bean passed to setProperties, returning null", new Exception());
            return null;
        }
        if (source == null)
            return target;
        if (properties == null) {
            log.debug("no property to copy,return the target object directly");
            return target;
        }
        Map values = getProperties(source, properties);
        setProperties(values, target);
        return target;
    }

    public static Object copyProperties(Object source, Object target) throws Exception {
        Map<String, PropertyDescriptor> sourceProp = getPropertyDescriptors(source);
        Map<String, PropertyDescriptor> targetProp = getPropertyDescriptors(target);

        List<String> keys = mapIntersectionKeys(sourceProp, targetProp);
        Map values = getProperties(source, keys);
        setProperties(values, target);
        return target;

    }

    /**
     * @param writeMethod
     * @param value
     * @param propertyName
     * @return
     * @throws Exception
     */
    private static Object[] createMethodArguments(Method method, Object value, String fieldName) throws Exception {
        return (new Object[] { createSetterArgument(method, value, fieldName) });
    }

    public static boolean contains(String str, String substr) {
        return str.indexOf(substr) != -1;
    }

    public static <T> boolean contains(T[] str, T target) {
        if (str == null || str.length < 1)
            return false;
        if (target == null) {
            for (int i = 0; i < str.length; i++) {
                if (str[i] == null)
                    return true;
            }
        } else {
            for (int i = 0; i < str.length; i++) {
                if (target.equals(str[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Object coerceProperty(String propertyName, Object value, Class beanClass) throws Exception {
        Object coercedValue = null;
        Map<String, PropertyDescriptor> properties = getPropertyDescriptors(beanClass);
        PropertyDescriptor pd = properties.get(propertyName);
        Method setter = pd.getWriteMethod();
        coercedValue = createSetterArgument(setter, value, propertyName);
        return coercedValue;
    }

    protected static Object createSetterArgument(Method method, Object value, String fieldName) throws Exception {
        Class<?> types[] = method.getParameterTypes();
        Class<?> paramType = types[0];
        if (value == null) {
            if (paramType.isPrimitive()) {
                value = TransformUtils.transformType(paramType, "");
            }
        } else if (!paramType.isAssignableFrom(value.getClass())) {
            if (paramType == String.class)
                return value.toString();
            value = TransformUtils.transformType(paramType, value);
        }
        return value;
    }

  

    /**
     * @param <T>
     * @param value
     * @param targetType
     * @return
     */
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
            if (log.isWarnEnabled())
                log.warn("was not found the enum String" + value + "for targetType " + targetType.getName());
        }

        return targetType.cast(theEnum);
    }

    public static String mimeTypeForFileName(String fileName) throws Exception {
        return mimeTypeForExtension(extensionForFileName(fileName));
    }

    public static String mimeTypeForExtension(String extension) throws Exception {
        if (extension == null)
            return null;
        String mimeType = null;
        if (mimeType == null) {
            if (useMimeTypesJS)
                mimeType = mimeTypesJS.getString(extension);
        }
        if (mimeType != null)
            mimeType = mimeType.toLowerCase();
        return mimeType;
    }

    /**
     * Return file's type . For example file name is 'example.xml' reutrn xml
     * 
     * @param fileName
     * @return
     */
    public static String extensionForFileName(String fileName) {
        if (fileName == null)
            return null;
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return null;
        } else {
            String extension = fileName.substring(lastDotIndex + 1);
            return extension;
        }
    }
    /**
     * 将原始路径转化为绝对路径
     * 
     * @param path
     * @return
     */
    public static String makePathAbsolute(String path) {
        if (!pathIsRelative(path)) {
            return path;
        }
        return path;
    }

    public static boolean pathIsRelative(String path) {
        return path != null && !path.startsWith("/") && !path.startsWith("\\") && !(new File(path)).isAbsolute() 
            && path.length() > 1 && (path.charAt(1) != ':' || path.charAt(2) != '/' && path.charAt(2) != '\\');
    }

    public static Object getSingle(Object toFetchFrom) {
        if (toFetchFrom instanceof List) {
            if (((List) toFetchFrom).size() == 1)
                return ((List) toFetchFrom).get(0);
        } else if ((toFetchFrom instanceof Map) && ((Map) toFetchFrom).size() == 1) {
            Iterator e = ((Map) toFetchFrom).keySet().iterator();
            return e.next();
        }
        return null;
    }

    /**
     * @param toSplit
     * @param virtual2MemberMap
     * @return
     */
    public static Map mapIntersection(Map primary, Map secondary) {
        if (primary == null && secondary == null)
            return null;
        if (primary == null)
            return secondary;
        if (secondary == null)
            return primary;
        Map result = new HashMap();
        for (Object key : primary.keySet()) {
            if (secondary.get(key) != null) {
                Object value = primary.get(key);
                if (value != null)
                    result.put(key, value);
            }
        }
        return result;
    }

    public static Map mapUnion(Map one, Map two) {
        return orderedMapUnion(one, two);
    }

    /**
     * 把两个Map(集合)放到一起。
     * 
     * @param primary
     * @param secondary
     * @return
     */
    public static Map orderedMapUnion(Map primary, Map secondary) {
        if (primary == null && secondary == null)
            return new HashMap();
        if (primary == null)
            return new HashMap(secondary);
        if (secondary == null)
            return new HashMap(primary);
        Map newMap = new HashMap();
        Object key;
        for (Iterator keysEnum = secondary.keySet().iterator(); keysEnum.hasNext(); newMap.put(key, secondary.get(key)))
            key = keysEnum.next();

        for (Iterator keysEnum = primary.keySet().iterator(); keysEnum.hasNext(); newMap.put(key, primary.get(key)))
            key = keysEnum.next();

        return newMap;
    }

    public static Map<String, Object> annotationFilter(Object bean, Class annotationClass) throws Exception {
        if (bean == null)
            return null;
        // if ( bean.getClass().isAnnotation() != true )
        // return null;
        Field[] _fields = bean.getClass().getDeclaredFields();
        if (_fields == null)
            return null;
        Map<String, Object> res = new HashMap<String, Object>();
        for (Field f : _fields) {
            if (f.getAnnotation(annotationClass) != null) {
                Map<String, PropertyDescriptor> properties = getPropertyDescriptors(bean);
                String property = f.getName();
                PropertyDescriptor pd = properties.get(property);
                Method method = pd.getReadMethod();
                if (method != null)
                    try {
                        Object value = method.invoke(bean);
                        if (value != null)
                            res.put(property, value);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
            }

        }
        return res;
    }

    public static Object getValueByFieldName(String fieldName, Object bean) throws Exception {
        if (bean == null)
            return null;
        Map<String, PropertyDescriptor> properties = getPropertyDescriptors(bean);
        if (properties == null)
            return null;
        Object value = null;
        for (String key : properties.keySet()) {
            if (key.equals(fieldName.trim())) {
                PropertyDescriptor pd = properties.get(key);
                Method method = pd.getReadMethod();
                if (method != null)
                    try {
                        value = method.invoke(bean);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
            }
        }
        return value;
    }

    public static Map<String, Object> getMapFromBean(Object bean) throws Exception {

        if (bean == null)
            return null;
        Map<String, PropertyDescriptor> properties = getPropertyDescriptors(bean);
        if (properties == null)
            return null;
        Map<String, Object> _return = new HashMap<String, Object>();
        for (String key : properties.keySet()) {
            PropertyDescriptor pd = properties.get(key);
            Method method = pd.getReadMethod();
            if (method != null)
                try {
                    Object value = method.invoke(bean);
                    if (value != null)
                        _return.put(key, value);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        return _return;
    }

    public static <K, V> List<Map<K, V>> mapToList(Map<K, V> map) {
        if (map == null)
            return null;
        List<Map<K, V>> list = new ArrayList<Map<K, V>>();
        for (K key : map.keySet()) {
            Map<K, V> m = new HashMap<K, V>();
            m.put(key, map.get(key));
            list.add(m);
        }
        return list;
    }

    public static URL resourceFromClassLoader(Class clz, String name) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL resource = null;
        if (cl != null)
            resource = cl.getResource(name);
        if (resource == null) {
            cl = clz.getClassLoader();
            resource = cl.getResource(name);
        }
        return resource;
    }

    /**
     * @param values
     * @param arrayList
     * @return
     */
    public static Map divideMap(Map sourceMap, List retainKeys) {
        if (sourceMap == null || retainKeys == null)
            return null;
        Map removedKeys = null;
        Iterator origKeys = sourceMap.keySet().iterator();
        do {
            if (!origKeys.hasNext())
                break;
            Object key = origKeys.next();
            if (!retainKeys.contains(key)) {
                if (removedKeys == null)
                    removedKeys = new HashMap();
                removedKeys.put(key, sourceMap.get(key));
                origKeys.remove();
            }
        } while (true);
        return removedKeys;
    }

    /**
     * @param submittedPrimaryKeys
     * @return
     */
    public static List keysAsList(Map map) {
        if (map == null)
            return null;
        else
            return enumToList(map.keySet().iterator());
    }

    /**
     * @param primaryKeys
     * @param keysAsList
     * @return
     */
    public static List setDisjunction(List one, List two) {
        if (two == null)
            return new ArrayList(one);
        List result = new ArrayList();
        if (one == null)
            return result;
        Iterator oneElems = one.iterator();
        do {
            if (!oneElems.hasNext())
                break;
            Object oneElem = oneElems.next();
            if (!two.contains(oneElem))
                result.add(oneElem);
        } while (true);
        return result;
    }

    /**
     * if source value not in target ,add it ,but if contained not update.
     * 
     * @param <V>
     * @param source
     * @param target
     */
    public static <V> List<V> listMerge(List<V> source, List<V> target) {
        if (source == null && target != null)
            return target;
        if (target == null && source != null)
            return source;
        if (target == null && source == null)
            return null;
        for (V value : source) {
            if (!target.contains(value))
                target.add(value);
        }
        return target;
    }

    /**
     * 对name中的特殊字符串做处理，使其转化为可用的Title名称.
     * 
     * @param Name
     */
    public static String deriveTileFromName(String name) {
        StringBuffer title = new StringBuffer();
        String _tmp = name.replace("_", " ");
        _tmp = _tmp.trim();
        if (_tmp.equals(_tmp.toUpperCase()) || _tmp.equals(_tmp.toLowerCase())) {
            _tmp = _tmp.toLowerCase();
            boolean capNext = true;
            for (int i = 0; i < _tmp.length(); i++) {
                String letter = _tmp.substring(i, i + 1);
                if (capNext) {
                    letter = letter.toUpperCase();
                    capNext = false;
                }
                if (" ".equals(letter))
                    capNext = true;
                title.append(letter);
            }
        } else {
            title.append(_tmp);
        }
        return title.toString();
    }

    static public boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().length() < 1);
    }

    static public boolean isNotNullAndEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    static public boolean isNotNullAndEmpty(StringBuffer sb) {
        return !isNullOrEmpty(sb);
    }

    /**
     * @param clause
     * @return
     */
    public static boolean isNullOrEmpty(StringBuffer sb) {

        return !(sb != null && sb.length() > 0);
    }

    /**
     * @param type
     * @return
     */
    public static boolean typeIsNumeric(String type) {
        return "number".equals(type) || "float".equals(type) || "decimal".equals(type) || "double".equals(type) || "int".equals(type)
            || "intEnum".equals(type) || "integer".equals(type) || "sequence".equals(type);
    }

    public static boolean typeIsDate(String type) {
        return "date".equals(type) || "time".equals(type) || "datetime".equals(type);
    }

    public static boolean typeIsBoolean(String type) {
        return "boolean".equals(type);
    }

    public static boolean typeIsDecimal(String type) {
        return "float".equals(type) || "decimal".equals(type) || "double".equals(type);
    }

    /**
     * @param files
     * @return
     */
    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.size() < 1;
    }

    public static <T> boolean isNullOrEmpty(T[] list) {
        return list == null || list.length < 1;
    }

    /**
     * @param dsToFree
     * @return
     */
    public static boolean isNotNullAndEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * @param <T>
     * @param filterProperties
     * @return
     */
    public static <T> boolean isNotNullAndEmpty(T[] filterProperties) {
        return filterProperties != null && filterProperties.length > 0;
    }

    /**
     * @param cmap
     * @return
     */
    public static boolean isNotNullAndEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

  

    public static <T> boolean isEqual(T actual, T expect) {
        return actual == null ? (expect == null ? true : false) : actual.equals(expect);
    }

    /**
     * use {@link #isEqual(Object, Object)}
     * 
     * @param actual
     * @param expect
     * @return
     */
    public static <T> boolean isNotEqual(T actual, T expect) {
        return actual == null ? (expect == null ? false : true) : !actual.equals(expect);
    }
    
    public static byte[] reversal(byte[] value) {
        if (value == null)
            return null;
        byte[] r = new byte[value.length];
        int len = value.length;
        for (int i = 0; i < len; i++) {
            r[i] = value[len - i - 1];
        }
        return r;
    }

    public static byte[] reversalCopy(byte[] target, byte[] source) {
        if (source == null)
            return target;
        int len = source.length;
        for (int i = 0; i < len && i < target.length; i++) {
            target[i] = source[len - i - 1];
        }
        return target;
    }

    /**
     * 
    /**
     * <p>Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     *
     * <pre>
     * DataUtils.removeEnd(null, *)      = null
     * DataUtils.removeEnd("", *)        = ""
     * DataUtils.removeEnd(*, null)      = *
     * DataUtils.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * DataUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
     * DataUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * DataUtils.removeEnd("abc", "")    = "abc"
     * </pre>
     * @param str
     * @param remove
     * @return
     */
    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * DataUtils.isEmpty(null)      = true
     * DataUtils.isEmpty("")        = true
     * DataUtils.isEmpty(" ")       = false
     * DataUtils.isEmpty("bob")     = false
     * DataUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    public static String getTemplateValue(String value) {
        return getTemplateValue(value,null);
    }

    public static String getTemplateValue(String value, Hashtable<String, String> staticProp) {
        if (value.indexOf("$") < 0) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        int prev = 0;
        // assert value!=nil
        int pos;
        while ((pos = value.indexOf("$", prev)) >= 0) {
            if (pos > 0) {
                sb.append(value.substring(prev, pos));
            }
            if (pos == (value.length() - 1)) {
                sb.append('$');
                prev = pos + 1;
            } else if (value.charAt(pos + 1) != '{') {
                sb.append('$');
                prev = pos + 1; // XXX
            } else {
                int endName = value.indexOf('}', pos);
                if (endName < 0) {
                    sb.append(value.substring(pos));
                    prev = value.length();
                    continue;
                }
                String n = value.substring(pos + 2, endName);
                String v = null;
                if (staticProp != null) {
                    v = staticProp.get(n);
                } else {
                    v = System.getProperty(n);
                }
                if (v == null)
                    v = "${" + n + "}";

                sb.append(v);
                prev = endName + 1;
            }
        }
        if (prev < value.length())
            sb.append(value.substring(prev));
        return sb.toString();
    }

    /**
     * @param rawData
     * @return
     */
    public static boolean isArray(Object bean) {
        if(bean==null)
            return false;
        if(List.class.isAssignableFrom(bean.getClass()))
            return true;
        else if(bean.getClass().isArray())
            return true;
        return false;
    }
    
    /**
     * @param pairs
     * @return
     */
    public static Map<String, String> toStringMap(String... pairs) {
        Map<String, String> parameters = new HashMap<String, String>();
        if (pairs.length > 0) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("pairs must be even.");
            }
            for (int i = 0; i < pairs.length; i = i + 2) {
                parameters.put(pairs[i], pairs[i + 1]);
            }
        }
        return parameters;
    }
}
