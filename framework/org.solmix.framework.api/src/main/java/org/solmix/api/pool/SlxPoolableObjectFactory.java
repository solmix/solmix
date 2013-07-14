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

package org.solmix.api.pool;

import org.apache.commons.pool.PoolableObjectFactory;

/**
 * @author solmix.f@gmail.com
 * @version 110035
 */
public abstract class SlxPoolableObjectFactory implements PoolableObjectFactory, IPoolableObjectFactory
{

    public SlxPoolableObjectFactory()
    {
        numActivateObjectCalls = 0;
        numDestroyObjectCalls = 0;
        numMakeObjectCalls = 0;
        numPassivateObjectCalls = 0;
        numValidateObjectCalls = 0;
        poolDisabled = false;
        pool = null;
    }

    public void setPool(Object pool) {
        this.pool = pool;
    }

    /**
     * Supported get no pooled Object.
     * 
     * @return
     * @throws Exception
     */
    public abstract Object makeUnpooledObject() throws Exception;

    public int numActivateObjectCalls;

    public int numDestroyObjectCalls;

    public int numMakeObjectCalls;

    public int numPassivateObjectCalls;

    public int numValidateObjectCalls;

    public boolean poolDisabled;

    protected Object pool;

}
