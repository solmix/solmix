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

package org.solmix.fmk.jaas.principal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.solmix.api.security.auth.PrincipalList;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-12
 */

public abstract class AbstractPrincipalList implements PrincipalList
{

    private static final long serialVersionUID = 3019627233907506936L;

    private Collection<String> list;

    private String name;

    protected AbstractPrincipalList()
    {
        this.list = new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.auth.PrincipalList#getName()
     */
    @Override
    public String getName() {
        if (StringUtils.isEmpty(this.name)) {
            return getDefaultName();
        }
        return this.name;
    }

    abstract String getDefaultName();

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.auth.PrincipalList#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.auth.PrincipalList#add(java.lang.String)
     */
    @Override
    public void add(String name) {
        this.list.add(name);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.auth.PrincipalList#getList()
     */
    @Override
    public Collection<String> getList() {
        return this.list;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.auth.PrincipalList#has(java.lang.String)
     */
    @Override
    public boolean has(String name) {
        Iterator<String> listIterator = this.list.iterator();
        while (listIterator.hasNext()) {
            String roleName = (String) listIterator.next();
            if (StringUtils.equalsIgnoreCase(name, roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", getName()).append("list", this.list).toString();
    }
}
