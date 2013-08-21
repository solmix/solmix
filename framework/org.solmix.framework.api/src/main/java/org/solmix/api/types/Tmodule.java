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

package org.solmix.api.types;

/**
 * framework module enum.this uesed to decleare components yet.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-25 solmix-api
 */
public enum Tmodule implements ValueEnum
{
    /**
     * Basic module.
     * Error code :0~1000
     */
    BASIC("basic") ,
    /**
     * DataSource module.the code module.
     * Error code :1000~1499
     */
    DATASOURCE("datasource") ,
    /**
     * Engine module,include OSGI HTTP bridge and HTTP proxy.
     * Error code :1500~1999
     */
    ENGINE("engine") ,
    // 2000 -2499 OSGI
    /**
     * Pool module,based on commons-pool.
     * Error code :2500~2599
     */
    POOL("pool") ,
    // 2600-2999
    /**
     * Velocity template language module,based on Apache velocity project.
     * Error code :2600~2999
     */
    VM("vm"),
    /**
     * XML module.
     * Error code :start with 3...
     */
    XML("xml") ,
    /**
     * RPC module.
     * Error code :start with 4...
     */
    RPC("rpc") ,
    /**
     * CONFIG module.
     * Error code :start with 5...
     */
    CONFIG("configuration") ,
    /**
     * SQL module.
     * Error code :5500~5999
     */
    SQL("sql") ,
    /**
     * JPA module.
     * Error code :6000-6500
     */
    JPA("jpa") ,
    // 6
    REPO("datasouce-repository") ,
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
     * @see org.solmix.api.types.ValueEnum#getValue()
     */
    @Override
    public String value() {
        return value;
    }
}
