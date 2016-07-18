/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.runtime.support;

import java.util.HashMap;
import java.util.Map;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.extension.ExtensionContainer;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-4
 */

public class ContainerFactoryImpl extends ContainerFactory
{

    
    public Container createContainer(ContainerListener... listeners) {
        return createContainer(new HashMap<Class<?>, Object>(),listeners);
    }
    @Override
    public Container createContainer() {
        return createContainer((ContainerListener[])null);
    }
    public Container createContainer(Map<Class<?>, Object> e,ContainerListener... listeners) {
        return createContainer(e, new HashMap<String, Object>(),listeners);
    }
    
    public Container createContainer(Map<Class<?>, Object> e, Map<String, Object> properties,ContainerListener... listeners) {
        ExtensionContainer container = new ExtensionContainer(e, properties);
        possiblySetDefaultContainer(container);
        if(listeners!=null&&listeners.length>0){
        	for(ContainerListener listener:listeners){
        		container.addListener(listener);
        	}
        }
        initializeContainer(container);
        container.initialize();
        return container;
    }
}
