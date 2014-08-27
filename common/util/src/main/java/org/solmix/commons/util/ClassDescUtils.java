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

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月26日
 */

public class ClassDescUtils
{

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    public static String getTypeDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getTypeDesc(c));
        return sb.toString();
    }

    public static String getTypeDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }
        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t))
                ret.append(JVM_VOID);
            else if ("boolean".equals(t))
                ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t))
                ret.append(JVM_BYTE);
            else if ("char".equals(t))
                ret.append(JVM_CHAR);
            else if ("double".equals(t))
                ret.append(JVM_DOUBLE);
            else if ("float".equals(t))
                ret.append(JVM_FLOAT);
            else if ("int".equals(t))
                ret.append(JVM_INT);
            else if ("long".equals(t))
                ret.append(JVM_LONG);
            else if ("short".equals(t))
                ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    /**
     * @param desc
     * @return
     */
    public static Class<?>[] getType(String desc) {
        // TODO Auto-generated method stub
        return null;
    }
}
