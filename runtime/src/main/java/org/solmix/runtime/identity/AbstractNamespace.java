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

package org.solmix.runtime.identity;

import org.solmix.runtime.Container;
import org.solmix.runtime.Containers;
import org.solmix.runtime.adapter.AdapterManager;

/**
 * 命名空间,用于管理不同的{@link ID}实例,每个命名空间必须有一个非空且唯一的名称
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public abstract class AbstractNamespace implements Namespace {

    private static final long serialVersionUID = -6586873128172797414L;


    private String name;

    private String description;

    private int hashCode;

    private boolean isInitialized = false;

    public AbstractNamespace() {
        // public null constructor
    }

    public final boolean initialize(String name, String desc) {
        if (name == null)
            throw new java.lang.IllegalArgumentException(
                "Namespace<init> name cannot be null");
        if (!isInitialized) {
            this.name = name;
            this.description = desc;
            this.hashCode = name.hashCode();
            this.isInitialized = true;
            return true;
        }
        return false;
    }

    public AbstractNamespace(String name, String desc)
    {
        initialize(name, desc);
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer("Namespace[");
        b.append("name=").append(name).append(";");
        b.append("scheme=").append(getScheme()).append(";");
        b.append("description=").append(description).append("]");
        return b.toString();
    }

    protected String getInitStringFromQueryString(Object[] args) {
        if (args == null || args.length < 1 || args[0] == null)
            return null;
        if (args[0] instanceof String) {
            final String arg = (String) args[0];
            if (arg.startsWith(getScheme() + SCHEME_SEPARATOR)) {
                final int index = arg.indexOf(SCHEME_SEPARATOR);
                if (index >= arg.length())
                    return null;
                return arg.substring(index + 1);
            }
        }
        return null;
    }

    @Override
    public Class<?>[][] getSupportedParameterTypes() {
        return new Class[][] { {} };
    }

    @Override
    public String[] getSupportedSchemes() {
        return new String[0];
    }

    @Override
    public abstract String getScheme();

    @Override
    public abstract ID createID(Object[] parameters) throws IDCreateException;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    protected String toQueryString(BaseID id) {
        return id.namespaceToQueryString();
    }

    protected int getHashCodeForID(BaseID id) {
        return id.namespaceHashCode();
    }

    protected int getCompareToForObject(BaseID first, BaseID second) {
        return first.namespaceCompareTo(second);
    }

    protected String getNameForID(BaseID id) {
        return id.namespaceGetName();
    }

    protected boolean testIDEquals(BaseID first, BaseID second) {
        // First check that namespaces are the same and non-null
        Namespace sn = second.getNamespace();
        if (sn == null || !this.equals(sn))
            return false;
        return first.namespaceEquals(second);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Namespace))
            return false;
        return ((AbstractNamespace) other).name.equals(name);
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if(type.isInstance(this))
            return type.cast(this);
        Container sc= Containers.get();
        if(sc==null)
            return null;
        AdapterManager apm= sc.getExtension(AdapterManager.class);
        if(apm==null)
            return null;
       return apm.getAdapter(this, type);

    }
}
