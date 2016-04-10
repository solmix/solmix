/**
 * Copyright 2016 The Solmix Project
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class ModCountCopyOnWriteArrayList<T> extends CopyOnWriteArrayList<T> {
    private static final long serialVersionUID = 1783937035760941219L;
    private final AtomicInteger modCount = new AtomicInteger();
    
    public ModCountCopyOnWriteArrayList() {
        super();
    }
    public ModCountCopyOnWriteArrayList(Collection<? extends T> c) {
        super(c);
        if (c instanceof ModCountCopyOnWriteArrayList) {
            modCount.set(((ModCountCopyOnWriteArrayList<?>)c).getModCount());
        }
    }
    
    public int getModCount() {
        return modCount.get();
    }
    
    public void setModCount(int i) {
        modCount.set(i);
    }
    
    @Override
    public void add(int index, T element) {
        super.add(index, element);
        modCount.incrementAndGet();
    }

    @Override
    public boolean add(T element) {
        if (super.add(element)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (super.addAll(c)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (super.addAll(index, c)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public int addAllAbsent(Collection<? extends T> c) {
        int i = super.addAllAbsent(c);
        if (i > 0) {
            modCount.incrementAndGet();
        }
        return i;
    }

    @Override
    public boolean addIfAbsent(T element) {
        if (super.addIfAbsent(element)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        modCount.incrementAndGet();
    }

    @Override
    public T remove(int index) {
        T t = super.remove(index);
        if (t != null) {
            modCount.incrementAndGet();
        }
        return t;
    }

    @Override
    public boolean remove(Object o) {
        if (super.remove(o)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (super.removeAll(c)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (super.retainAll(c)) {
            modCount.incrementAndGet();
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + modCount.get();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ModCountCopyOnWriteArrayList) {
            return super.equals(o) && modCount.get() 
                == ((ModCountCopyOnWriteArrayList<?>)o).getModCount();
        }
        return false;
    }

}

