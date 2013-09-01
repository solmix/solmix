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

import java.util.concurrent.atomic.AtomicLong;

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

    public AtomicLong numActivateObjectCalls;

    public AtomicLong numDestroyObjectCalls;

    public AtomicLong numMakeObjectCalls;

    public AtomicLong numPassivateObjectCalls;

    public AtomicLong numValidateObjectCalls;

    protected volatile Object pool;

    public SlxKeyedPoolableObjectFactory()
    {
        numActivateObjectCalls = new AtomicLong();
        numDestroyObjectCalls = new AtomicLong();
        numMakeObjectCalls= new AtomicLong();
        numPassivateObjectCalls = new AtomicLong();
        numValidateObjectCalls = new AtomicLong();
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
