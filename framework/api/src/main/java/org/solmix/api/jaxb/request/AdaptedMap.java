/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.api.jaxb.request;

import javax.xml.bind.annotation.XmlAnyElement;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-13
 */

public class AdaptedMap
{

    private Object value;

    @XmlAnyElement
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
