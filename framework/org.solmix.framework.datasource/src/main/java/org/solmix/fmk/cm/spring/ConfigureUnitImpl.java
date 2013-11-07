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
package org.solmix.fmk.cm.spring;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.solmix.api.cm.ConfigureUnit;
import org.solmix.commons.collections.DataTypeMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年11月6日
 */

public class ConfigureUnitImpl implements ConfigureUnit
{
    
    private  Properties properties;
    private final String pid;
    private DataTypeMap internal;
    
    private final SpringConfigureUnitManager manager;
    
    public ConfigureUnitImpl(String pid,Properties prop,SpringConfigureUnitManager manager){
        this.properties=prop;
        this.pid=pid;
        this.manager=manager;
    }
 
    @Override
    public DataTypeMap getProperties() {
        if(internal==null){
            internal=new DataTypeMap();
            if (properties != null) {
                Enumeration<Object> en = properties.keys();
                while (en.hasMoreElements()) {
                    Object key = en.nextElement();
                    internal.put(key.toString(), properties.get(key));
                }
            }
        }
       
        return internal;
    }

  
    @Override
    public void delete() throws IOException {
        properties=null;

    }

    @Override
    public void update() throws IOException {
      //XXX
      //manager.update(this,properties);
       throw new java.lang.RuntimeException("this implemention not support update()");

    }

 
    @Override
    public void update(Dictionary<String, ?> properties) throws IOException {
        if (properties != null) {
            Enumeration<String> en = properties.keys();
            while (en.hasMoreElements()) {
                String key = en.nextElement();
                getProperties().put(key, properties.get(key));
            }
        }

    }

  
    @Override
    public String getPid() {
        return pid;
    }

}
