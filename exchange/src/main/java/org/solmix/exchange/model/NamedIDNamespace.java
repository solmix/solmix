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

import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.ID;
import org.solmix.runtime.identity.IDCreateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class NamedIDNamespace extends AbstractNamespace {
    
    public static final String NAME = NamedIDNamespace.class.getName();
    /**    */
    private static final long serialVersionUID = 2921002660948196043L;

    public NamedIDNamespace() {
        super(NAME, "Exchange Model Namespace");
    }

    @Override
    public String getScheme() {
        return NamedIDNamespace.class.getName();
    }

    @Override
    public ID createID(Object[] parameters) throws IDCreateException {
        if (parameters == null || parameters.length != 2) {
            throw new IDCreateException(
                "ID cannot be null and must be of length 2");
        }
        return new NamedID(this, (String) parameters[0], (String) parameters[1]);
    }

    @Override
    public Class<?>[][] getSupportedParameterTypes() {
        return new Class[][] {{String.class }, {String.class } };
    }

}
