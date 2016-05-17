/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.commons.collections;

import java.util.HashMap;
import java.util.Map;


/**
 * 实现基于class.getName()为主键存放Object实例的功能.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月10日
 */

public class StringTypeMapper extends HashMap<String, Object> implements  StringTypeMap
{

    public StringTypeMapper(){
        
    }
    public StringTypeMapper(Map<String,Object> map){
        super(map);
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> key) {
        return (T)get(key.getName());
    }

    @Override
    public <T> void put(Class<T> key, T value) {
        put(key.getName(), value);
    }
}
