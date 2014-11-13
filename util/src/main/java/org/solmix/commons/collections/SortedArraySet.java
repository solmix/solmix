/**
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

package org.solmix.commons.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现了Set的大多数方法,使用数组存储,遍历效率高,但是添加费事.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月14日
 */

public final class SortedArraySet<T> implements SortedSet<T> {

    final AtomicReference<T[]> data = new AtomicReference<T[]>();

    @Override
    public void clear() {
        data.set(null);
    }

    @Override
    public boolean isEmpty() {
        T[] tmp = data.get();
        return tmp == null || tmp.length == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new SASIterator<T>(data.get());
    }

    @Override
    public int size() {
        T[] tmp = data.get();
        return tmp == null ? 0 : tmp.length;
    }

    @SuppressWarnings("unchecked")
    private T[] newArray(int size) {
        return (T[]) new Object[size];
    }

    @Override
    public boolean add(T o) {
        if (!contains(o)) {
            T[] tmp = data.get();
            T[] tmp2;
            if (tmp == null) {
                tmp2 = newArray(1);
                tmp2[0] = o;
            } else {
                tmp2 = newArray(tmp.length + 1);
                System.arraycopy(tmp, 0, tmp2, 0, tmp.length);
                tmp2[tmp2.length - 1] = o;
                Arrays.sort(tmp2);
            }

            if (!data.compareAndSet(tmp, tmp2)) {
                return add(o);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean val = false;
        for (T t : c) {
            val |= add(t);
        }
        return val;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean val = false;
        for (Object t : c) {
            val |= contains(t);
        }
        return val;
    }

    @Override
    public boolean contains(Object o) {
        T[] tmp = data.get();
        if (tmp == null) {
            return false;
        }
        return Arrays.binarySearch(tmp, o) >= 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean val = false;
        for (Object t : c) {
            val |= remove(t);
        }
        return val;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean val = false;
        for (T t : this) {
            if (!c.contains(t)) {
                val |= remove(t);
            }
        }
        return val;
    }

    @Override
    public boolean remove(Object o) {
        T[] tmp = data.get();

        if (tmp == null) {
            return false;
        }
        int idx = Arrays.binarySearch(tmp, o);
        if (idx != -1) {
            if (tmp.length == 1 && !data.compareAndSet(tmp, null)) {
                return remove(o);
            }
            T[] tmp2 = newArray(tmp.length - 1);
            System.arraycopy(tmp, 0, tmp2, 0, idx);
            System.arraycopy(tmp, idx + 1, tmp2, idx, tmp.length - 1 - idx);
            if (!data.compareAndSet(tmp, tmp2)) {
                return remove(o);
            }
            return true;
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        T[] tmp = data.get();
        if (tmp == null) {
            return new Object[0];
        }
        T[] tmp2 = newArray(tmp.length);
        System.arraycopy(tmp, 0, tmp2, 0, tmp.length);
        return tmp2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X[] toArray(X[] a) {
        T[] tmp = data.get();
        if (tmp == null) {
            if (a.length != 0) {
                return (X[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), 0);
            }
            return a;
        }

        if (a.length < tmp.length) {
            a = (X[]) java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), tmp.length);
        }
        System.arraycopy(tmp, 0, a, 0, tmp.length);
        if (a.length > tmp.length) {
            a[tmp.length] = null;
        }
        return a;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (!(o instanceof SortedArraySet)) {
            return false;
        }
        SortedArraySet<T> as = (SortedArraySet<T>) o;
        return Arrays.equals(data.get(), as.data.get());
    }

    @Override
    public String toString() {
        return Arrays.toString(data.get());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data.get());
    }

    private class SASIterator<X> implements Iterator<X> {

        final X[] data;

        int idx;

        public SASIterator(X[] d) {
            data = d;
        }

        @Override
        public boolean hasNext() {
            return data != null && idx != data.length;
        }

        @Override
        public X next() {
            if (data == null || idx == data.length) {
                throw new NoSuchElementException();
            }
            return data[idx++];
        }

        @Override
        public void remove() {
            if (idx > 0) {
                SortedArraySet.this.remove(data[idx - 1]);
            }
        }
    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public T first() {
        T[] tmp = data.get();
        if (tmp == null || tmp.length == 0) {
            return null;
        }
        return tmp[0];
    }

    @Override
    public T last() {
        T[] tmp = data.get();
        if (tmp == null || tmp.length == 0) {
            return null;
        }
        return tmp[tmp.length - 1];
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }
}
