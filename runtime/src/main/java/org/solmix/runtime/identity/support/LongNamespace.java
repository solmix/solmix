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

package org.solmix.runtime.identity.support;

import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.ID;
import org.solmix.runtime.identity.IDCreateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class LongNamespace extends AbstractNamespace {

    private static final long serialVersionUID = -1580533392719331665L;

    public LongNamespace() {
        super(LongID.class.getName(), "LongID Namespace");
    }

    @Override
    public String getScheme() {
        return LongID.class.getName();
    }

    @Override
    public Class<?>[][] getSupportedParameterTypes() {
        return new Class[][] {{Long.class } };
    }

    @Override
    public ID createID(Object[] parameters) throws IDCreateException {
        try {
            String init = getInitStringFromQueryString(parameters);
            if (init != null)
                return new LongID(this, Long.decode(init));
            return new LongID(this, (Long) parameters[0]);
        } catch (Exception e) {
            throw new IDCreateException(getName() + " createInstance()", e);
        }
    }

}
