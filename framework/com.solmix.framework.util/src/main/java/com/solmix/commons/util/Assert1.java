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

package com.solmix.commons.util;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author solomon
 * @version 110035 2011-3-26
 */

public class Assert1
{

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
    public static boolean isNullOrEmpty(List<?> list) {
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

    public static boolean assertTrue(Boolean obj) {
        if (obj == null)
            return false;
        return obj.booleanValue();
    }

    public static <T> boolean assertEqual(T actual, T expect) {
        return actual == null ? (expect == null ? true : false) : actual.equals(expect);
    }

    /**
     * If not equal return true.else return false.
     * 
     * @param <T>
     * @param actual
     * @param expect
     * @return
     */
    public static <T> boolean assertNotEqual(T actual, T expect) {
        return actual == null ? (expect == null ? false : true) : !actual.equals(expect);
    }

    public static boolean assertTrue(String booleanValue) {
        if ("true".equalsIgnoreCase(booleanValue))
            return true;
        else
            return false;
    }

}
