/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.runtime.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月3日
 */

public class EventArguments implements Map<String, Object>
{

    /**
     * The properties for an event. Keys are strings and values are objects. The
     * object is unmodifiable.
     */
    private final Map<String, Object>   properties;

    /**
     * Create an EventProperties from the specified properties.
     * 
     * <p>
     * The specified properties will be copied into this EventProperties.
     * Properties whose key is not of type {@code String} will be ignored. A
     * property with the key &quot;event.topics&quot; will be ignored.
     * 
     * @param properties The properties to use for this EventProperties object
     *        (may be {@code null}).
     */
    public EventArguments(Map<String, ?> properties) {
          int size = (properties == null) ? 0 : properties.size();
          Map<String, Object> p = new HashMap<String, Object>(size);
          if (size > 0) {
                for (Object key : (Set<?>) properties.keySet()) {
                      if ((key instanceof String) && !IEvent.EVENT_TOPIC.equals(key)) {
                            Object value = properties.get(key);
                            p.put((String) key, value);
                      }
                }
          }
          // safely publish the map
          this.properties = Collections.unmodifiableMap(p);
    }

    /**
     * Create an EventProperties from the specified dictionary.
     * 
     * <p>
     * The specified properties will be copied into this EventProperties.
     * Properties whose key is not of type {@code String} will be ignored. A
     * property with the key &quot;event.topics&quot; will be ignored.
     * 
     * @param properties The properties to use for this EventProperties object
     *        (may be {@code null}).
     */
    public EventArguments(Dictionary<String, ?> properties) {
          int size = (properties == null) ? 0 : properties.size();
          Map<String, Object> p = new HashMap<String, Object>(size);
          if (size > 0) {
                for (Enumeration<?> e = properties.keys(); e.hasMoreElements();) {
                      Object key = e.nextElement();
                      if ((key instanceof String) && !IEvent.EVENT_TOPIC.equals(key)) {
                            Object value = properties.get(key);
                            p.put((String) key, value);
                      }
                }
          }
          // safely publish the map
          this.properties = Collections.unmodifiableMap(p);
    }

    /**
     * This method throws {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException if called.
     */
    public void clear() {
          properties.clear();
    }

    /**
     * Indicates if the specified property is present.
     * 
     * @param name The property name.
     * @return {@code true} If the property is present, {@code false} otherwise.
     */
    public boolean containsKey(Object name) {
          return properties.containsKey(name);
    }

    /**
     * Indicates if the specified value is present.
     * 
     * @param value The property value.
     * @return {@code true} If the value is present, {@code false} otherwise.
     */
    public boolean containsValue(Object value) {
          return properties.containsValue(value);
    }

    /**
     * Return the property entries.
     * 
     * @return A set containing the property name/value pairs.
     */
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
          return properties.entrySet();
    }

    /**
     * Return the value of the specified property.
     * 
     * @param name The name of the specified property.
     * @return The value of the specified property.
     */
    public Object get(Object name) {
          return properties.get(name);
    }

    /**
     * Indicate if this properties is empty.
     * 
     * @return {@code true} If this properties is empty, {@code false}
     *         otherwise.
     */
    public boolean isEmpty() {
          return properties.isEmpty();
    }

    /**
     * Return the names of the properties.
     * 
     * @return The names of the properties.
     */
    public Set<String> keySet() {
          return properties.keySet();
    }

    /**
     * This method throws {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException if called.
     */
    public Object put(String key, Object value) {
          return properties.put(key, value);
    }

    /**
     * This method throws {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException if called.
     */
    public void putAll(Map<? extends String, ? extends Object> map) {
          properties.putAll(map);
    }

    /**
     * This method throws {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException if called.
     */
    public Object remove(Object key) {
          return properties.remove(key);
    }

    /**
     * Return the number of properties.
     * 
     * @return The number of properties.
     */
    public int size() {
          return properties.size();
    }

    /**
     * Return the properties values.
     * 
     * @return The values of the properties.
     */
    public Collection<Object> values() {
          return properties.values();
    }

    /**
     * Compares this {@code EventProperties} object to another object.
     * 
     * <p>
     * The properties are compared using the {@code java.util.Map.equals()}
     * rules which includes identity comparison for array values.
     * 
     * @param object The {@code EventProperties} object to be compared.
     * @return {@code true} if {@code object} is a {@code EventProperties} and
     *         is equal to this object; {@code false} otherwise.
     */
    public boolean equals(Object object) {
          if (this == object) {
                return true;
          }
          if (!(object instanceof EventArguments)) {
                return false;
          }
          EventArguments other = (EventArguments) object;
          return properties.equals(other.properties);
    }

    /**
     * Returns a hash code value for this object.
     * 
     * @return An integer which is a hash code value for this object.
     */
    public int hashCode() {
          return properties.hashCode();
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return The string representation of this object.
     */
    public String toString() {
          return properties.toString();
    }

}
