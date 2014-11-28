/**
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General  License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General  License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.runtime.identity;

import java.io.Serializable;

import org.solmix.runtime.Extension;
import org.solmix.runtime.adapter.Adaptable;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年9月14日
 */
@Extension
public interface Namespace extends Adaptable, Serializable {

    public static final String SCHEME_SEPARATOR = ":";

    Class<?>[][] getSupportedParameterTypes();

    String[] getSupportedSchemes();

    String getScheme();

    ID createID(Object[] parameters) throws IDCreateException;

    String getDescription();

    String getName();
}
