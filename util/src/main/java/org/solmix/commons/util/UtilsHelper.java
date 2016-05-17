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

public class UtilsHelper
{
    static final UtilsHelper HELPER;
    static {
        UtilsHelper theHelper = null;
        try {
            theHelper = new CglibUtilsHelper();
        } catch (Throwable ex) {
            theHelper = new UtilsHelper();
        }
        HELPER = theHelper;
    }
    
    
     static final ArrayUtils             ARRAY_UTILS         = new ArrayUtils();
     static final ClassLoaderUtils       CLASS_LOADER_UTILS  = new ClassLoaderUtils();
     static final ClassDescUtils         CLASS_DESC_UTILS    = new ClassDescUtils();
     static final FileUtils              FILE_UTILS          = new FileUtils();
     static final DataUtils              DATA_UTILS          = new DataUtils();
     static final StringEscapeUtils      STRINGESCAPE_UTILS          = new StringEscapeUtils();
     static final ObjectUtils            OBJECT_UTILS         = new ObjectUtils();
     static final NetUtils               NET_UTILS            = new NetUtils();
     static final StringUtils            STRING_UTILS         = new StringUtils();
     static final Base64Utils            BASE64_UTILS         = new Base64Utils();
     static final TransformUtils         TRANSFOM_UTILS       = new TransformUtils();
   
   

    private static  final Map<String, Object> ALL_UTILS = Collections.unmodifiableMap(ArrayUtils.arrayToMap(new Object[][] {
         { "arrayUtils", ARRAY_UTILS },
         { "classLoaderUtil", CLASS_LOADER_UTILS },
         { "classDescUtil", CLASS_DESC_UTILS },
         { "fileUtils", FILE_UTILS },
         { "dataUtils", DATA_UTILS },
         { "stringEscapeUtils", STRINGESCAPE_UTILS },
         { "objectUtils", OBJECT_UTILS },
         { "netUtils", NET_UTILS },
         { "stringUtils", STRING_UTILS },
         { "base64Util", BASE64_UTILS },
         { "transformUtil", TRANSFOM_UTILS }
    }, String.class, Object.class));

    public static Map<String, Object> getUtils() {
        return HELPER.getUtilsInternal();
    }

    /**
     * 取得包含所有utils的map
     *
     * @return utils map
     */
    public  Map<String, Object> getUtilsInternal() {
        return ALL_UTILS;
    }
}
