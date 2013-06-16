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

package com.solmix.sgt.client;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-5
 */

public enum EviewType
{
    HIDDEN("hidden") , SHOW_ALL("all");

    private String v;

    public static final String P_VIEW_TYPE = "_vt";

    EviewType(String v)
    {
        this.v = v;
    }

    public String value() {
        return v;
    }

    public static EviewType fromValue(String v) {
        for (EviewType c : EviewType.values()) {
            if (c.v.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
