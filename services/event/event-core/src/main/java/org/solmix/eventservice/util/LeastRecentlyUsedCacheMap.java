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
package org.solmix.eventservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.eventservice.Cache;



/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-10-1
 * @param <K>
 * @param <V>
 */

public class LeastRecentlyUsedCacheMap<K, V> implements Cache<K,V>
{
    private int maxSize;
    private final Object appLock = new Object();
    private final Map<K,V> cache;
    private final List<K> history;
    public LeastRecentlyUsedCacheMap(final int maxSize)
    {
        if(0 >= maxSize)
        {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.maxSize = maxSize;
        // We need one more entry then m_maxSize in the cache and a HashMap is
        // expanded when it reaches 3/4 of its size hence, the funny numbers.
        cache = new HashMap<K, V>(maxSize + 1 + ((maxSize + 1) * 3) / 4);
        
        // This is like above but assumes a list is expanded when it reaches 1/2 of
        // its size. Not much harm if this is not the case. 
        history  = new ArrayList<K>(maxSize + 1 + ((maxSize + 1) / 2));  
    }
    /**
     * Add the key-value pair to the cache. The key will be come the most recently
     * used entry. In case max size is (or has been) reached this will remove the
     * least recently used entry in the cache. In case that the cache already 
     * contains this specific key-value pair it LRU counter is updated only.
     * 
     * @param key The key for the value
     * @param value The value for the key
     * 
     * @see org.apache.felix.eventadmin.impl.util.CacheMap#add(java.lang.Object, java.lang.Object)
     */
    @Override
    public void add(final K key, final V value)
    {
        synchronized(appLock)
        {
            final V result = cache.put(key, value);
            
            if(null != result)
            {
                history.remove(key);
            }
            
            history.add(key);
            
            if(maxSize < cache.size())
            {
                cache.remove(history.remove(0));
            }
        }
    }
    @Override
    public V get(final K key)
    {
        synchronized(appLock)
        {
            final V result = cache.get(key);
            
            if(null != result)
            {
                history.remove(key);
                
                history.add(key);
            }
            
            return result;
        }
    }
    /**
     * Remove the entry denoted by key from the cache and return its value.
     * 
     * @param key The key of the entry to be removed
     * 
     * @return The value of the entry removed, <tt>null</tt> if none
     * 
     * @see org.apache.felix.eventadmin.impl.util.CacheMap#remove(java.lang.Object)
     */
    @Override
    public V remove(final K key)
    {
        synchronized(appLock)
        {
            final V result = cache.remove(key);
            
            if(null != result)
            {
                history.remove(key);
            }
            
            return result;
        }
    }

    /**
     * Return the current size of the cache.
     * 
     * @return The number of entries currently in the cache.
     * 
     * @see org.apache.felix.eventadmin.impl.util.CacheMap#size()
     */
    @Override
    public int size()
    {
        synchronized (appLock)
        {
            return cache.size();
        }
    }

    /**
     * Remove all entries from the cache.
     * 
     * @see org.apache.felix.eventadmin.impl.util.CacheMap#clear()
     */
    @Override
    public void clear()
    {
        synchronized (appLock)
        {
            cache.clear();
            
            history.clear();
        }
    }

   
}
