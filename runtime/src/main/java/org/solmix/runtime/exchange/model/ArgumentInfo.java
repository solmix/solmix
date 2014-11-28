/**
 * Copyright (c) 2014 The Solmix Project
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
package org.solmix.runtime.exchange.model;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月26日
 */

public class ArgumentInfo extends InfoPropertiesSupport {

    private  InfoID argumentId;
    private final AbstractMessageInfo messageInfo ;
    private Class<?> typeClass;
    private int index;
    /**
     * @param argumentId
     * @param abstractMessageInfo 
     */
    public ArgumentInfo(InfoID argumentId, AbstractMessageInfo minfo) {
       this.argumentId=argumentId;
       this.messageInfo=minfo;
    }
    public InfoID getID() {
        return argumentId;
    }

    public void setID(InfoID iD) {
        this.argumentId = iD;
    }

    public AbstractMessageInfo getMessageInfo(){
        return messageInfo;
    }
    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    @Override
    public String toString() {
        return new StringBuilder().append("[ArgumentInfo ID=")
            .append(getID())
            .append("]").toString();
    }
    
    @Override
    public int hashCode() {
        return argumentId == null ? -1 : argumentId.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArgumentInfo)) {
            return false;
        }
        ArgumentInfo oi = (ArgumentInfo)o;
        return equals(argumentId, oi.argumentId) 
            && equals(typeClass, oi.typeClass);
    }
}
