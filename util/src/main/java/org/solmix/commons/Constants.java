/**
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
package org.solmix.commons;

import java.util.regex.Pattern;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年3月31日
 */

public class Constants
{

    public static final Pattern COMMA_SPLIT_PATTERN                = Pattern
                                                                           .compile("\\s*[,]+\\s*");
    public static final String  GROUP_KEY                          = "group";

    public static final String  PATH_KEY                           = "path";

    public static final String  INTERFACE_KEY                      = "interface";
    
    public static final String  VERSION_KEY                        = "version";

    public static final String  REVISION_KEY                       = "revision";
}
