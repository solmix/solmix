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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.1 2014年8月26日
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

    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method) {
        return getSimpleMethodSignature(method, false, false, false, false);
    }

    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method, boolean withClassName) {
        return getSimpleMethodSignature(method, false, false, withClassName, false);
    }
    public static String getSimpleClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getSimpleClassName(clazz.getName());
    }
    
    public static String getSimpleClassName(String javaClassName) {
        return getSimpleClassName(javaClassName, true);
    }
    /** 取得简洁的method描述。 */
    public static String getSimpleMethodSignature(Method method, boolean withModifiers, boolean withReturnType,
                                                  boolean withClassName, boolean withExceptionType) {
        if (method == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        if (withModifiers) {
            buf.append(Modifier.toString(method.getModifiers())).append(' ');
        }

        if (withReturnType) {
            buf.append(getSimpleClassName(method.getReturnType())).append(' ');
        }

        if (withClassName) {
            buf.append(getSimpleClassName(method.getDeclaringClass())).append('.');
        }

        buf.append(method.getName()).append('(');

        Class<?>[] paramTypes = method.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            buf.append(getSimpleClassName(paramType));

            if (i < paramTypes.length - 1) {
                buf.append(", ");
            }
        }

        buf.append(')');

        if (withExceptionType) {
            Class<?>[] exceptionTypes = method.getExceptionTypes();

            if (!ArrayUtils.isEmptyArray(exceptionTypes)) {
                buf.append(" throws ");

                for (int i = 0; i < exceptionTypes.length; i++) {
                    Class<?> exceptionType = exceptionTypes[i];

                    buf.append(getSimpleClassName(exceptionType));

                    if (i < exceptionTypes.length - 1) {
                        buf.append(", ");
                    }
                }
            }
        }

        return buf.toString();
    }
    
    /**
     * 取得类名，不包括package名。
     * <p>
     * 此方法可以正确显示数组和内联类的名称。 例如：
     * <p/>
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
     * </pre>
     * <p>
     * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
     * </p>
     *
     * @param javaClassName 要查看的类名
     * @return 简单类名，如果类名为空，则返回 <code>null</code>
     */
    public static String getSimpleClassName(String javaClassName, boolean proccesInnerClass) {
        String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

        if (friendlyClassName == null) {
            return javaClassName;
        }

        if (proccesInnerClass) {
            char[] chars = friendlyClassName.toCharArray();
            int beginIndex = 0;

            for (int i = chars.length - 1; i >= 0; i--) {
                if (chars[i] == '.') {
                    beginIndex = i + 1;
                    break;
                } else if (chars[i] == '$') {
                    chars[i] = '.';
                }
            }

            return new String(chars, beginIndex, chars.length - beginIndex);
        } else {
            return friendlyClassName.substring(friendlyClassName.lastIndexOf(".") + 1);
        }
    }
    
    /**
     * 将Java类名转换成友好类名。
     *
     * @param javaClassName     Java类名
     * @param processInnerClass 是否将内联类分隔符 <code>'$'</code> 转换成 <code>'.'</code>
     * @return 友好的类名。如果参数非法或空，则返回<code>null</code>。
     */
    private static String toFriendlyClassName(String javaClassName, boolean processInnerClass, String defaultIfInvalid) {
        String name = StringUtils.trimToNull(javaClassName);

        if (name == null) {
            return defaultIfInvalid;
        }

        if (processInnerClass) {
            name = name.replace('$', '.');
        }

        int length = name.length();
        int dimension = 0;

        // 取得数组的维数，如果不是数组，维数为0
        for (int i = 0; i < length; i++, dimension++) {
            if (name.charAt(i) != '[') {
                break;
            }
        }

        // 如果不是数组，则直接返回
        if (dimension == 0) {
            return name;
        }

        // 确保类名合法
        if (length <= dimension) {
            return defaultIfInvalid; // 非法类名
        }

        // 处理数组
        StringBuilder componentTypeName = new StringBuilder();

        switch (name.charAt(dimension)) {
            case 'Z':
                componentTypeName.append("boolean");
                break;

            case 'B':
                componentTypeName.append("byte");
                break;

            case 'C':
                componentTypeName.append("char");
                break;

            case 'D':
                componentTypeName.append("double");
                break;

            case 'F':
                componentTypeName.append("float");
                break;

            case 'I':
                componentTypeName.append("int");
                break;

            case 'J':
                componentTypeName.append("long");
                break;

            case 'S':
                componentTypeName.append("short");
                break;

            case 'L':
                if (name.charAt(length - 1) != ';' || length <= dimension + 2) {
                    return defaultIfInvalid; // 非法类名
                }

                componentTypeName.append(name.substring(dimension + 1, length - 1));
                break;

            default:
                return defaultIfInvalid; // 非法类名
        }

        for (int i = 0; i < dimension; i++) {
            componentTypeName.append("[]");
        }

        return componentTypeName.toString();
    }
}
