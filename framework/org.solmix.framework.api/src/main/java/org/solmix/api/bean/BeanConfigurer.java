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

package org.solmix.api.bean;


/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */

public interface BeanConfigurer
{

    String USER_CFG_FILE_PROPERTY_NAME = "solmix.cfg.file";
    String USER_CFG_FILE = "solmix.xml";

    /**
     * set up the Bean's value by using Dependency Injection from the application context
     * @param beanInstance the instance of the bean which needs to be configured
     */
    void configureBean(Object beanInstance);
    
    /**
     * set up the Bean's value by using Dependency Injection from the application context
     * with a proper name. You can use * as the prefix of wildcard name.
     * @param name the name of the bean which needs to be configured
     * @param beanInstance the instance of bean which need to be configured
     */
    void configureBean(String name, Object beanInstance);
}
