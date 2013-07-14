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

import org.apache.commons.pool.KeyedPoolableObjectFactory;

import org.solmix.api.datasource.DSRequest;

/**
 * Add function {@link #makeUnpooledObject(Object)}
 * 
 * @since 0.0.1
 * @author @author solmix.f@gmail.com
 * @version 110035
 */
public abstract class SlxKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory, IPoolableObjectFactory
{

    public int numActivateObjectCalls;

    public int numDestroyObjectCalls;

    public int numMakeObjectCalls;

    public int numPassivateObjectCalls;

    public int numValidateObjectCalls;

    protected Object pool;

    public SlxKeyedPoolableObjectFactory()
    {
        numActivateObjectCalls = 0;
        numDestroyObjectCalls = 0;
        numMakeObjectCalls = 0;
        numPassivateObjectCalls = 0;
        numValidateObjectCalls = 0;
        pool = null;
    }

    /**
     * Supported get no pooled Object.
     * 
     * @param obj key Object. it would be a String key Object for usually.
     * @return return type determined by implementation's type.
     * @throws Exception
     */
    public abstract Object makeUnpooledObject(Object key) throws Exception;

    /**
     * Supported get no pooled Object.Only for datasource Object.
     * 
     * @param obj key Object. it would be a String key Object for usually.
     * @return return type determined by implementation's type.
     * @throws Exception
     */
    public abstract Object makeUnpooledObject(Object key, DSRequest request) throws Exception;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.IPoolableObjectFactory#setPool(java.lang.Object)
     */
    @Override
    public void setPool(Object obj) {
        this.pool = obj;

    }

}
