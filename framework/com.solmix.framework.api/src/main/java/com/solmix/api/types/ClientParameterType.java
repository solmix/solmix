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

package com.solmix.api.types;

/**
 * 
 * @author ffz
 * @version 110035 2012-3-31
 * @version 0.0.1
 * @since 0.0.4
 */

public enum ClientParameterType
{
    MAP(1) , ARRAY(2) , DEFAULT(0);

    private int value;

    ClientParameterType(int value)
    {
        this.value = value;
    }

    public int type() {
        return this.value;
    }

}
