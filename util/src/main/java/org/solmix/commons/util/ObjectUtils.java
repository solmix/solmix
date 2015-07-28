/**
 * Copyright (c) 2015 The Solmix Project
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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月21日
 */

public class ObjectUtils
{

    /** 代表null值的占位对象。 */
    public static final Object NULL_PLACEHOLDER = new NullPlaceholder();

    /** 空字符串。 */
    public static final String EMPTY_STRING = "";

    /** 空的<code>byte</code>数组。 */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];

    /** 空的<code>short</code>数组。 */
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];

    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

    /** 空的<code>int</code>数组。 */
    public static final int[] EMPTY_INT_ARRAY = new int[0];

    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

    /** 空的<code>long</code>数组。 */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];

    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

    /** 空的<code>float</code>数组。 */
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];

    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];

    /** 空的<code>double</code>数组。 */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];

    /** 空的<code>char</code>数组。 */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];

    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    /** 空的<code>boolean</code>数组。 */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];

    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];

    // object arrays

    /** 空的<code>Object</code>数组。 */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** 空的<code>Class</code>数组。 */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    /** 空的<code>String</code>数组。 */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final static class NullPlaceholder implements Serializable
    {

        private static final long serialVersionUID = 7092611880189329093L;

        @Override
        public String toString() {
            return "null";
        }

        private Object readResolve() {
            return NULL_PLACEHOLDER;
        }
    }
    
    public static class NullObject{
        
    }

    /** 是否为<code>null</code>、空字符串、或空数组。 */
    public static boolean isEmptyObject(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return StringUtils.isEmpty((String) object);
        } else if (object.getClass().isArray()) {
            return ArrayUtils.isEmptyArray(object);
        } else {
            return false;
        }
    }

    // ==========================================================================
    // 默认值函数。
    //
    // 当对象为null时，将对象转换成指定的默认对象。
    // ==========================================================================

    /**
     * 如果对象为<code>null</code>，则返回指定默认对象，否则返回对象本身。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param object 要测试的对象
     * @param defaultValue 默认值
     * @return 对象本身或默认对象
     */
    public static <T, S extends T> T defaultIfNull(T object, S defaultValue) {
        return object == null ? defaultValue : object;
    }

    // ==========================================================================
    // 比较函数。
    //
    // 以下方法用来比较两个对象的值或类型是否相同。
    // ==========================================================================

    /**
     * 比较两个对象是否完全相等。
     * <p>
     * 此方法可以正确地比较多维数组。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.equals(null, null)                  = true
     * ObjectUtil.equals(null, "")                    = false
     * ObjectUtil.equals("", null)                    = false
     * ObjectUtil.equals("", "")                      = true
     * ObjectUtil.equals(Boolean.TRUE, null)          = false
     * ObjectUtil.equals(Boolean.TRUE, "true")        = false
     * ObjectUtil.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtil.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     * <p/>
     * </p>
     *
     * @param object1 对象1
     * @param object2 对象2
     * @return 如果相等, 则返回<code>true</code>
     */
    public static boolean isEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }

        if (object1 == null || object2 == null) {
            return false;
        }

        if (!object1.getClass().equals(object2.getClass())) {
            return false;
        }

        if (object1 instanceof Object[]) {
            return Arrays.deepEquals((Object[]) object1, (Object[]) object2);
        } else if (object1 instanceof int[]) {
            return Arrays.equals((int[]) object1, (int[]) object2);
        } else if (object1 instanceof long[]) {
            return Arrays.equals((long[]) object1, (long[]) object2);
        } else if (object1 instanceof short[]) {
            return Arrays.equals((short[]) object1, (short[]) object2);
        } else if (object1 instanceof byte[]) {
            return Arrays.equals((byte[]) object1, (byte[]) object2);
        } else if (object1 instanceof double[]) {
            return Arrays.equals((double[]) object1, (double[]) object2);
        } else if (object1 instanceof float[]) {
            return Arrays.equals((float[]) object1, (float[]) object2);
        } else if (object1 instanceof char[]) {
            return Arrays.equals((char[]) object1, (char[]) object2);
        } else if (object1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) object1, (boolean[]) object2);
        } else {
            return object1.equals(object2);
        }
    }

    /**
     * 检查两个对象是否属于相同类型。<code>null</code>将被看作任意类型。
     *
     * @param object1 对象1
     * @param object2 对象2
     * @return 如果两个对象有相同的类型，则返回<code>true</code>
     */
    public static boolean isSameType(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return true;
        }

        return object1.getClass().equals(object2.getClass());
    }

    // ==========================================================================
    // Hash code函数。
    //
    // 以下方法用来取得对象的hash code。
    // ==========================================================================

    /**
     * 取得对象的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 此方法可以正确地处理多维数组。
     * </p>
     *
     * @param object 对象
     * @return hash值
     */
    public static int hashCode(Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Object[]) {
            return Arrays.deepHashCode((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.hashCode((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.hashCode((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.hashCode((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.hashCode((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.hashCode((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.hashCode((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.hashCode((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) object);
        } else {
            return object.hashCode();
        }
    }

    // ==========================================================================
    // 取得对象的identity。
    // ==========================================================================

    /**
     * 取得对象的原始的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 该方法使用<code>System.identityHashCode</code>来取得hash值，该值不受对象本身的 <code>hashCode</code>方法的影响。
     * </p>
     *
     * @param object 对象
     * @return hash值
     */
    public static int identityHashCode(Object object) {
        return object == null ? 0 : System.identityHashCode(object);
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.identityToString(null)          = null
     * ObjectUtil.identityToString("")            = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE)  = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0])    = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0]) = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object 对象
     * @return 对象的identity，如果对象是<code>null</code>，则返回<code>null</code>
     */
    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.identityToString(null, "NULL")            = "NULL"
     * ObjectUtil.identityToString("", "NULL")              = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE, "NULL")    = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0], "NULL")      = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0], "NULL")   = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object 对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的identity，如果对象是<code>null</code>，则返回指定字符串
     */
    public static String identityToString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出——追加到
     * <code>Appendable</code>中。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.appendIdentityToString(buf, null)          = null
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append("int[]@7fa")
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append("java.lang.Object[]@7fa")
     * </pre>
     *
     * @param buffer <code>Appendable</code>对象
     * @param object 对象
     * @return <code>Appendable</code>对象，如果对象为<code>null</code>，则输出 <code>"null"</code>
     */
    public static <A extends Appendable> A appendIdentityToString(A buffer, Object object) {
        Assert.assertNotNull(buffer, "appendable");

        try {
            if (object == null) {
                buffer.append("null");
            } else {
                buffer.append(object.getClass().toString());
                buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return buffer;
    }

    // ==========================================================================
    // toString方法。
    // ==========================================================================

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回空字符串 <code>""</code>。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.toString(null)         = ""
     * ObjectUtil.toString("")           = ""
     * ObjectUtil.toString("bat")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE) = "true"
     * ObjectUtil.toString([1, 2, 3])    = "[1, 2, 3]"
     * </pre>
     *
     * @param object 对象
     * @return 对象的<code>toString()</code>的返回值，或空字符串<code>""</code>
     */
    public static String toString(Object object) {
        return toString(object, "");
    }

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回指定字符串。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.toString(null, null)           = null
     * ObjectUtil.toString(null, "null")         = "null"
     * ObjectUtil.toString("", "null")           = ""
     * ObjectUtil.toString("bat", "null")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE, "null") = "true"
     * ObjectUtil.toString([1, 2, 3], "null")    = "[1, 2, 3]"
     * </pre>
     *
     * @param object 对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的<code>toString()</code>的返回值，或指定字符串
     */
    public static String toString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        } else if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        } else {
            return object.toString();
        }
    }

    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            if (o1 instanceof Object[] && o2 instanceof Object[]) {
                return Arrays.equals((Object[]) o1, (Object[]) o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[]) {
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[]) {
                return Arrays.equals((char[]) o1, (char[]) o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[]) {
                return Arrays.equals((double[]) o1, (double[]) o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[]) {
                return Arrays.equals((float[]) o1, (float[]) o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[]) {
                return Arrays.equals((int[]) o1, (int[]) o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[]) {
                return Arrays.equals((long[]) o1, (long[]) o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[]) {
                return Arrays.equals((short[]) o1, (short[]) o2);
            }
        }
        return false;
    }
}
