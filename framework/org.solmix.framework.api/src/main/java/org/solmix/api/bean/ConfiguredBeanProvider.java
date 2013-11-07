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

import java.util.Collection;
import java.util.List;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-3
 */

public interface ConfiguredBeanProvider
{
    /**
     * Gets the names of all the configured beans of the specific type.  Does
     * not cause them to be loaded.
     * @param type
     * @return List of all the bean names for the given type
     */
    List<String> getBeanNamesOfType(Class<?> type);
    
    
    /**
     * Gets the bean of the given name and type
     * @param name
     * @param type
     * @return the bean
     */
    <T> T getBeanOfType(String name, Class<T> type);
    
    /**
     * Gets all the configured beans of the specific types.  Causes them
     * all to be loaded. 
     * @param type
     * @return The collection of all the configured beans of the given type
     */
    <T> Collection<? extends T> getBeansOfType(Class<T> type);

    
    /**
     * Iterates through the beans of the given type, calling the listener
     * to determine if it should be loaded or not. 
     * @param type
     * @param listener
     * @return true if beans of the type were loaded
     */
    <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener);
    
    public interface BeanLoaderListener<T> {
        /**
         * Return true to have the loader go ahead and load the bean.  If false, 
         * the loader will just skip to the next bean
         * @param name
         * @param type
         * @return true if the bean should be loaded 
         */
        boolean loadBean(String name, Class<? extends T> type);

        /**
         * Return true if the bean that was loaded meets the requirements at
         * which point, the loader will stop loading additional beans of the
         * given type
         * @param name
         * @param bean
         * @return true if the bean meets the requirements of the listener
         */
        boolean beanLoaded(String name, T bean);
    }

    boolean hasBeanOfName(String name);
}
