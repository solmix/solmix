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

package org.solmix.runtime;

import java.util.List;
import java.util.Map;

import org.solmix.runtime.extension.ExtensionLoader;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月20日
 */

public interface Container {

    String DEFAULT_CONTAINER_ID = "solmix";

    String CONTAINER_PROPERTY_NAME = "solmix.Container.system.id";

    /**
     * Get the extension instance of <code>class</code> manager by this system
     * Container
     * <p>
     * <li>The type can be a class or interface
     * 
     * @param type
     * @return the extension instance
     */
    <T> T getExtension(Class<T> type);

    <T> void setExtension(T instance, Class<T> type);

    /**
     * The inteface <code>type</code> have more than one extension,used this
     * method
     * <p>
     * <li>type must be a interface
     * <li>type must be annotated by {@link Extension}
     * 
     * @param type
     * @return
     */
    <T> ExtensionLoader<T> getExtensionLoader(Class<T> type);

    /**
     * Indicate this system Container have the bean name.
     * 
     * @param name the bean name
     * @return
     */
    boolean hasExtensionByName(String name);

    /**
     * Return the SystemContext ID
     * 
     * @return
     */
    String getId();

    /**
     * Setting ID for this container.
     * 
     * @param containerID
     */
    void setId(String containerID);

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

    void setContainerListeners(List<ContainerListener> listeners);

    List<ContainerListener> getContainerListeners();

    /**
     * Close this Container.
     * 
     * @param wait
     */
    void close(boolean wait);

    /**
     * Colse the Container and Release any resource used by this Context (e.g.
     * jcr sessions).
     */
    public void close();

    /**
     * @return
     */
    boolean isProduction();

    /**
     * @param production
     */
    void setProduction(boolean production);
}
