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

package org.solmix.exchange.model;

import org.solmix.runtime.identity.BaseID;
import org.solmix.runtime.identity.IDFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class NamedID extends BaseID {

    private static final long serialVersionUID = 4677663481983737102L;

    private final String name;

    private final String serviceNamespace;

    private int hash;
    private String identityName;

    public NamedID(NamedID other) {
        this(other.getServiceNamespace(), other.getName());
    }
    
    public NamedID(String space, String name) {
        this((NamedIDNamespace) IDFactory.getDefault().getNamespaceByName(
            NamedIDNamespace.NAME), space, name);
    }

    protected NamedID(NamedIDNamespace n, String space, String name)
    {
        super(n);
        this.name = name;
        this.serviceNamespace = space;
        this.hash = 17;
        this.hash = 37 * hash + name == null ? 0 : name.hashCode();
        this.hash = 37 * hash + space == null ? 0 : space.hashCode();
        if (serviceNamespace.equals("")) {
            identityName = name;
        } else {
            if (serviceNamespace.endsWith("/")) {
                identityName = new StringBuilder().append(serviceNamespace).append(name).toString();
            } else {
                identityName = new StringBuilder().append(serviceNamespace).append("/").append(name).toString();
            }
        }
    }

    @Override
    protected int namespaceCompareTo(BaseID o) {
        if (o == null || !(o instanceof NamedID)) {
            return Integer.MIN_VALUE;
        }
        NamedID other = (NamedID) o;
        int compare = this.serviceNamespace.compareTo(other.getServiceNamespace());
        if (compare == 0) {
            return this.getName().compareTo(other.getName());
        }
        return compare;
    }

    @Override
    protected boolean namespaceEquals(BaseID o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof NamedID)) {
            return false;
        }
        NamedID other = (NamedID) o;
        if (serviceNamespace.equals(other.getServiceNamespace())) {
            return name.equals(other.getName());
        }
        return false;
    }

    @Override
    protected String namespaceGetName() {
      
        return identityName;
    }

    @Override
    protected int namespaceHashCode() {
        return hash;
    }

    /**   */
    @Override
    public String getName() {
        return name;
    }

    /**   */
    public String getServiceNamespace() {
        return serviceNamespace;
    }

    @Override
    public String toString() {
        return namespaceGetName();
    }
    /**
     * NamedID 转化为可用于标记的字符串
     * @return
     */
    public String toIdentityString() {
        return namespaceGetName();
    }
    
    /**
     * 从字符串中生成NamedID
     * @return
     */
    public static NamedID formIdentityString(String identity){
        if(identity==null){
            return null;
        }
        char[] chars=identity.toCharArray();
        int index= identity.indexOf("://");
        if(index<0){
            index=identity.indexOf(":/");
            if(index>0){
                index+=2;
            }
        }else{
            index+=3;
        }
        if(index<0){
            index=0;
        }
        for(;index<chars.length;index++){
            if(chars[index]=='/'){
                break;
            }
        }
        String serviceName = identity.substring(0, index);
        String name =identity.substring(index+1);
        return new NamedID(serviceName, name);
    }
}
