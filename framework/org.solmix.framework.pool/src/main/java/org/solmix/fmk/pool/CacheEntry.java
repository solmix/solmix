/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.pool;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.4
 * @since 0.0.1
 */

public class CacheEntry
{

    private Object cachedObject;

    private long timeStamp;

    private long lastStalenessCheck;

    public CacheEntry(Object cache)
    {
        this.cachedObject = cache;
        this.timeStamp = System.currentTimeMillis();
        this.lastStalenessCheck = System.currentTimeMillis();
    }

    public CacheEntry(Object cache, long timeStap)
    {
        this.cachedObject = cache;
        this.timeStamp = timeStap;
        this.lastStalenessCheck = System.currentTimeMillis();
    }

    public CacheEntry(Object cache, long timeStap, long lastStalnessCheck)
    {
        this.cachedObject = cache;
        this.timeStamp = timeStap;
        this.lastStalenessCheck = lastStalnessCheck;
    }

    /**
     * @return the cachedObject
     */
    public Object getCachedObject() {
        return cachedObject;
    }

    /**
     * @param cachedObject the cachedObject to set
     */
    public void setCachedObject(Object cachedObject) {
        this.cachedObject = cachedObject;
    }

    /**
     * @return the timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the lastStalenessCheck
     */
    public long getLastStalenessCheck() {
        return lastStalenessCheck;
    }

    /**
     * @param lastStalenessCheck the lastStalenessCheck to set
     */
    public void setLastStalenessCheck(long lastStalenessCheck) {
        this.lastStalenessCheck = lastStalenessCheck;
    }
}
