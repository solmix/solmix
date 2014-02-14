/*
 * Copyright 2012 The Solmix Project
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
package org.solmix.commons.management;

import java.util.EventObject;

import org.solmix.commons.management.Statistic.UpdateMode;


/**
 * 
 * @author Administrator
 * @version 110035  2011-8-15
 */

public interface ManagementStrategy
{

void manageObject(Object managedObject) throws Exception;
void manageNamedObject(Object managedObject,Object preferredName)throws Exception;
<T> T getManagedObjectName(Object managedObject,String customName,Class<T> nameType) throws Exception;
void unmanageObject (Object managedObject)throws Exception;
void unmanageNamedObject(Object name) throws Exception;
boolean isManaged(Object managableObject,Object name);
void notify(EventObject event)throws Exception;
Statistic createStatistic(String name,Object owner,UpdateMode updateMode);
} 
