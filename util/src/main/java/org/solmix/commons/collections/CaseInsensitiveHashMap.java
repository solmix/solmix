/**
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
package org.solmix.commons.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * A Map that converts all keys to lowercase Strings for case insensitive
 * lookups.  This is needed for the toMap() implementation because 
 * databases don't consistenly handle the casing of column names. 
 * 
 * <p>The keys are stored as they are given [BUG #DBUTILS-34], so we maintain
 * an internal mapping from lowercase keys to the real keys in order to 
 * achieve the case insensitive lookup.
 * 
 * <p>Note: This implementation does not allow <tt>null</tt>
 * for key, whereas {@link HashMap} does, because of the code:
 * <pre>
 * key.toString().toLowerCase()
 * </pre>
 * @author solmix.f@gmail.com
 * @version 110035  2011-8-28
 * @param <V>
 * @param <K>
 */
public  class CaseInsensitiveHashMap extends HashMap<String, Object> {
    
    /**
     * The internal mapping from lowercase keys to the real keys.
     * 
     * <p>
     * Any query operation using the key 
     * ({@link #get(Object)}, {@link #containsKey(Object)})
     * is done in three steps:
     * <ul>
     * <li>convert the parameter key to lower case</li>
     * <li>get the actual key that corresponds to the lower case key</li>
     * <li>query the map with the actual key</li>
     * </ul>
     * </p>
     */
    private final Map<String,String> lowerCaseMap = new HashMap<String,String>();

    /**
     * Required for serialization support.
     * 
     * @see java.io.Serializable
     */ 
    private static final long serialVersionUID = -2848100435296897392L;

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
        return super.containsKey(realKey);
        // Possible optimisation here:
        // Since the lowerCaseMap contains a mapping for all the keys,
        // we could just do this:
        // return lowerCaseMap.containsKey(key.toString().toLowerCase());
    }

    /** {@inheritDoc} */
    @Override
    public Object get(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
        return super.get(realKey);
    }

    /** {@inheritDoc} */
    @Override
    public Object put(String key, Object value) {
        /*
         * In order to keep the map and lowerCaseMap synchronized,
         * we have to remove the old mapping before putting the 
         * new one. Indeed, oldKey and key are not necessaliry equals.
         * (That's why we call super.remove(oldKey) and not just
         * super.put(key, value))
         */
        Object oldKey = lowerCaseMap.put(key.toLowerCase(), key);
        Object oldValue = super.remove(oldKey);
        super.put(key, value);
        return oldValue;
    }

    /** {@inheritDoc} */
    @Override
    public void putAll(Map<? extends String,?> m) {
        for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            this.put(key, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object remove(Object key) {
        Object realKey = lowerCaseMap.remove(key.toString().toLowerCase());
        return super.remove(realKey);
    }
}
