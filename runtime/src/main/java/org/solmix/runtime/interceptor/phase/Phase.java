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
package org.solmix.runtime.interceptor.phase;


/**
 * 定义拦截链中不同的步骤.
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月12日
 */

public class Phase implements Comparable<Object>
{

    private String name;
    private int priority;
    
    public Phase() {
    }
    
    public Phase(String n, int p) {
        this.name = n;
        this.priority = p;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int p) {
        this.priority = p;
    }
    
    @Override
    public int hashCode() {
        return priority;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Phase)) {
            return false;
        }
        Phase p = (Phase)o;
        
        return p.priority == priority
            && p.name.equals(name);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Phase) {
            Phase p = (Phase)o;
            
            if (priority == p.priority) {
                return name.compareTo(p.name); 
            }
            return priority - p.priority;
        }
        return 1;
    }
    
    @Override
    public String toString() {
        return "Phase(" + getName() + ")";
    }

}
