/*
 * Copyright 2015 The Solmix Project
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

import java.util.Collections;
import java.util.Map;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月27日
 */

public class CglibUtilsHelper extends UtilsHelper
{
    CglibUtilsHelper() throws Exception {
        Class.forName("net.sf.cglib.proxy.Enhancer");
        Class.forName("net.sf.cglib.proxy.MethodInterceptor");
        Class.forName("net.sf.cglib.proxy.MethodProxy");
    }
     static final Object                 MATH_UTILS          = createMixin(Math.class);
    private static final Object           MIXIN_UTILS        = createMixin(
        ArrayUtils.class,
        ClassLoaderUtils.class,
        ClassDescUtils.class,
        FileUtils.class,
        DataUtils.class,
        DateUtils.class,
        ObjectUtils.class,
        NetUtils.class,
        StringUtils.class,
        Base64Utils.class,
        TransformUtils.class,
        Math.class);
    private static Object createMixin(Class<?>... classes) {
        StaticFunctionDelegatorBuilder builder = new StaticFunctionDelegatorBuilder(UtilsHelper.class.getClassLoader());

        for (Class<?> clazz : classes) {
            builder.addClass(clazz);
        }

        return builder.toObject();
    }
   
    private static final Map<String, Object> ALL_UTILS = Collections.unmodifiableMap(ArrayUtils.arrayToMap(new Object[][] {
        { "arrayUtils", ARRAY_UTILS },
        { "classLoaderUtil", CLASS_LOADER_UTILS },
        { "classDescUtil", CLASS_DESC_UTILS },
        { "fileUtil", FILE_UTILS },
        { "dataUtils", DATA_UTILS },
        { "stringEscapeUtils", STRINGESCAPE_UTILS },
        { "objectUtil", OBJECT_UTILS },
        { "netUtils", NET_UTILS },
        { "stringUtils", STRING_UTILS },
        { "base64Util", BASE64_UTILS },
        { "transformUtil", TRANSFOM_UTILS },
        { "mathUtil", MATH_UTILS },
        { "utils", MIXIN_UTILS }
   }, String.class, Object.class));
    
    @Override
    public  Map<String, Object> getUtilsInternal() {
        return ALL_UTILS;
    }
}
