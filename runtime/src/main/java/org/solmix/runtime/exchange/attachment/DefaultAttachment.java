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
package org.solmix.runtime.exchange.attachment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;

import org.solmix.runtime.exchange.Attachment;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月12日
 */

public class DefaultAttachment implements Attachment
{
    private DataHandler dataHandler;
    private String id;
    private Map<String, String> headers = new HashMap<String, String>();
    public DefaultAttachment(String id){
        this.id=id;
    }
    public DefaultAttachment(String id,DataHandler handler){
        this.id=id;
        this.dataHandler=handler;
    }
    @Override
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    
    @Override
    public String getId() {
        return id;
    }
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
   
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        String value = headers.get(name);
        return value == null ? headers.get(name.toLowerCase()) : value;
    }

    public Iterator<String> getHeaderNames() {
        return headers.keySet().iterator();
    }


}
