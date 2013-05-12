/*
 * ========THE SOLMIX PROJECT=====================================
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
 * framework module enum.this uesed to decleare components yet.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-25 solmix-api
 */
public enum Tmodule implements ValueEnum
{
    // 0
    BASIC("basic") ,
    // 1000-1499
    DATASOURCE("datasource") ,
    // 1500-1999
    ENGINE("engine") ,
    // 2000 -2499 OSGI
    // 2500-2599
    POOL("pool") ,
    // 2600-2999
    VM("vm"),
    // 3
    XML("xml") ,
    // 4
    RPC("rpc") ,
    // 5
    CONFIG("configuration") ,
    // 5500
    SQL("sql") ,
    // 5500-5999
    JPA("jpa") ,
    // 6
    REPO("datasouce_repository") ,
    // 7
    JS("JavaScript/JSON") ,
    // 8
    SERVLET("servlet") ,
    // 9
    APP("appbase") ,
    EXTEND("extend-service");

    private String value;

    Tmodule(String value)
    {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.types.ValueEnum#getValue()
     */
    @Override
    public String value() {
        return value;
    }
}
