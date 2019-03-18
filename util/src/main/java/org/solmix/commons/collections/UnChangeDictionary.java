/**
 * Copyright (c) 2015 The Solmix Project
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

import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;


/**
 * put和remove操作抛出UnsupportedOperationException
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月20日
 */

public class UnChangeDictionary<K, V> extends Dictionary<K, V> implements
    Map<K, V> {
    private boolean readOnly = false;
    private K[] headers;
    private V[] values;
    private int size = 0;

    /**
     * Create an empty Headers dictionary.
     *
     * @param initialCapacity The initial capacity of this Headers object.
     */
    public UnChangeDictionary(int initialCapacity) {
          super();
          @SuppressWarnings("unchecked")
          K[] k = (K[]) new Object[initialCapacity];
          headers = k;
          @SuppressWarnings("unchecked")
          V[] v = (V[]) new Object[initialCapacity];
          values = v;
    }

    /**
     * Create a Headers dictionary from a Dictionary.
     *
     * @param values The initial dictionary for this Headers object.
     * @exception IllegalArgumentException If a case-variant of the key is
     * in the dictionary parameter.
     */
    public UnChangeDictionary(Dictionary<? extends K, ? extends V> values) {
          this(values.size());
          /* initialize the headers and values */
          Enumeration<? extends K> keys = values.keys();
          while (keys.hasMoreElements()) {
                K key = keys.nextElement();
                set(key, values.get(key));
          }
    }

    /**
     * Case-preserved keys.
     */
    @Override
    public synchronized Enumeration<K> keys() {
          return new ArrayEnumeration<K>(headers, size);
    }

    /**
     * Values.
     */
    @Override
    public synchronized Enumeration<V> elements() {
          return new ArrayEnumeration<V>(values, size);
    }

    private int getIndex(Object key) {
          boolean stringKey = key instanceof String;
          for (int i = 0; i < size; i++) {
                if (stringKey && (headers[i] instanceof String)) {
                      if (((String) headers[i]).equalsIgnoreCase((String) key))
                            return i;
                } else {
                      if (headers[i].equals(key))
                            return i;
                }
          }
          return -1;
    }

    private V remove(int remove) {
          V removed = values[remove];
          for (int i = remove; i < size; i++) {
                if (i == headers.length - 1) {
                      headers[i] = null;
                      values[i] = null;
                } else {
                      headers[i] = headers[i + 1];
                      values[i] = values[i + 1];
                }
          }
          if (remove < size)
                size--;
          return removed;
    }

    private void add(K header, V value) {
          if (size == headers.length) {
                // grow the arrays
                @SuppressWarnings("unchecked")
                K[] nh = (K[]) new Object[headers.length + 10];
                K[] newHeaders = nh;
                @SuppressWarnings("unchecked")
                V[] nv = (V[]) new Object[values.length + 10];
                V[] newValues = nv;
                System.arraycopy(headers, 0, newHeaders, 0, headers.length);
                System.arraycopy(values, 0, newValues, 0, values.length);
                headers = newHeaders;
                values = newValues;
          }
          headers[size] = header;
          values[size] = value;
          size++;
    }

    /**
     * Support case-insensitivity for keys.
     *
     * @param key name.
     */
    @Override
    public synchronized V get(Object key) {
          int i = -1;
          if ((i = getIndex(key)) != -1)
                return values[i];
          return null;
    }

    /**
     * Set a header value or optionally replace it if it already exists.
     *
     * @param key Key name.
     * @param value Value of the key or null to remove key.
     * @param replace A value of true will allow a previous
     * value of the key to be replaced.  A value of false 
     * will cause an IllegalArgumentException to be thrown 
     * if a previous value of the key exists.
     * @return the previous value to which the key was mapped,
     * or null if the key did not have a previous mapping.
     *
     * @exception IllegalArgumentException If a case-variant of the key is
     * already present.
     * @since 3.2
     */
    public synchronized V set(K key, V value, boolean replace) {
          if (readOnly)
                throw new UnsupportedOperationException();
          if (key instanceof String) {
                @SuppressWarnings("unchecked")
                K k = (K) ((String) key).intern();
                key = k;
          }
          int i = getIndex(key);
          if (value == null) { /* remove */
                if (i != -1)
                      return remove(i);
          } else { /* put */
                if (i != -1) { /* duplicate key */
                      if (!replace)
                            throw new IllegalArgumentException("The key "+key+" already exists in another case variation");
                      V oldVal = values[i];
                      values[i] = value;
                      return oldVal;
                }
                add(key, value);
          }
          return null;
    }

    /**
     * Set a header value.
     *
     * @param key Key name.
     * @param value Value of the key or null to remove key.
     * @return the previous value to which the key was mapped,
     * or null if the key did not have a previous mapping.
     *
     * @exception IllegalArgumentException If a case-variant of the key is
     * already present.
     */
    public synchronized V set(K key, V value) {
          return set(key, value, false);
    }

    public synchronized void setReadOnly() {
          readOnly = true;
    }
    
    public synchronized boolean isReadOnly(){
        return readOnly;
    }

    /**
     * Returns the number of entries (distinct keys) in this dictionary.
     *
     * @return  the number of keys in this dictionary.
     */
    @Override
    public synchronized int size() {
          return size;
    }

    /**
     * Tests if this dictionary maps no keys to value. The general contract
     * for the <tt>isEmpty</tt> method is that the result is true if and only
     * if this dictionary contains no entries.
     *
     * @return  <code>true</code> if this dictionary maps no keys to values;
     *          <code>false</code> otherwise.
     */
    @Override
    public synchronized boolean isEmpty() {
          return size == 0;
    }

    /**
     * Always throws UnsupportedOperationException.
     *
     * @param key header name.
     * @param value header value.
     * @throws UnsupportedOperationException
     */
    @Override
    public synchronized V put(K key, V value) {
          if (readOnly)
                throw new UnsupportedOperationException();
          return set(key, value, true);
    }

    /**
     * Always throws UnsupportedOperationException.
     *
     * @param key header name.
     * @throws UnsupportedOperationException
     */
    @Override
    public V remove(Object key) {
          throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
          return values!=null?values.toString():null;
    }

    private static class ArrayEnumeration<E> implements Enumeration<E> {
          private final E[] array;
          int cur = 0;

          public ArrayEnumeration(E[] array, int size) {
                @SuppressWarnings("unchecked")
                E[] a = (E[]) new Object[size];
                this.array = a;
                System.arraycopy(array, 0, this.array, 0, this.array.length);
          }

          @Override
        public boolean hasMoreElements() {
                return cur < array.length;
          }

          @Override
        public E nextElement() {
                return array[cur++];
          }
    }

    @Override
    public synchronized void clear() {
          if (readOnly)
                throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean containsKey(Object key) {
          return getIndex(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
          throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
          throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
          throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> c) {
          throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
          throw new UnsupportedOperationException();
    }

}
