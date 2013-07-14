/*
 * SOLMIX PROJECT
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

package org.solmix.launch.base.shared;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-16
 */

public class LaunchConstants
{

    public static String LOADERS_PATH = "org.solmix.launch.base";

    public static String JAAS_PATH_LOADER = "jaas-boot";

    public static final String LOADER_JAR_REL_PATH = "org.solmix.launch.base.jar";

    public static String DEFAULT_SOLMIX_LOADERS_JAR = "/resource/lib/" + LOADER_JAR_REL_PATH;

    public static String SOLMIX_BASE = "solmix.base";

    public static String SOLMIX_WEB_ROOT = "solmix.web.root";

    public static String DEFAULT_SOLMIX_SERVLET = "org.solmix.launch.base.web.ServletDelegate";

    public static String DEFAULT_DELEGATE_CLASS = "org.solmix.launch.base.web.HttpSessionListenerDelegate";

    public static final String SOLMIX_HOME = "solmix.home";

}
