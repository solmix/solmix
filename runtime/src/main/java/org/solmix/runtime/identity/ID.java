/*
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

import java.io.Serializable;
import java.security.Principal;

/**
 * 用于统一管理所有资源的标识<br>
 * ID创建后就不可更改．
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public interface ID extends Serializable, Comparable<Object>, Principal {

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    /**
     * Get the unique name of this identity.
     * 
     * @return String unique name for this identity. Will not be null, and must
     *         be a unique String within the Namespace returned by
     *         getNamespace()
     */
    @Override
    String getName();

    /**
     * Get the Namespace instance associated with this identity
     * 
     * @return Namespace the Namespace corresponding to this identity. Will not
     *         return null.
     */
    Namespace getNamespace();

    /**
     * <a href="http://en.wikipedia.org/wiki/Query_string">Query_string</a> Get
     * this ID instance in query String form. Will not return null. just like
     * key=value&key1=value1;
     * 
     * @return String that is query representation of this ID
     */
    String toQueryString();
}
