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
package org.solmix.eventservice;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-10-1
 * @param <K>
 * @param <V>
 */

public interface Cache<K, V>
{

    /**
     * 
     */
    void clear();

    /**
     * @return
     */
    int size();

    /**
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * @param key
     * @param value
     */
    void add(K key, V value);

    /**
     * @param key
     * @return
     */
    V get(K key);

}
