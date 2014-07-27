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
package org.solmix.runtime;

import java.util.Map;

import org.solmix.runtime.extension.ExtensionLoader;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月20日
 */

public interface Container
{
    public static final String DEFAULT_CONTAINER_ID = "solmix";

    public static final String CONTAINER_PROPERTY_NAME = "solmix.Container.system.id";
    
    /**
     * Get the Bean instance of <code>class</code> manager by this system
     * Container
     * 
     * @param beanType
     * @return the bean instance
     */
    <T> T getBean(Class<T> beanType);
    

    <T> void setBean(T bean, Class<T> beanType);
    
    <T> ExtensionLoader<T> getExtensionLoader(Class<T> beanType);
    /**
     * Indicate this system Container have the bean name.
     * 
     * @param name the bean name
     * @return
     */
    boolean hasBeanByName(String name);

    /**
     * Return the SystemContext ID
     * 
     * @return
     */
    String getId();


    /**
     * Open this Container for using.
     */
    void open();
    /**
     * @param name
     * @param value
     */
     void setProperty(String name, Object value);
    /**
     * Get attribute value
     * 
     * @param name to which value is associated to
     * @return attribute value
     */
     Object getProperty(String name);
    
    /**
     * Get an over all map.
     * 
     * @return the map
     */
     Map<String, Object> getProperties();
     
     void setProperties(Map<String, Object> properties);
     
     void addListener(ContainerListener listener);
     
     void removeListener(ContainerListener listener);
     
     /**
      * Close this Container.
      * 
      * @param wait
      */
     void close(boolean wait);
     /**
      * Colse the Container and Release any resource used by this Context (e.g. jcr
      * sessions).
      */
     public void close();
}
