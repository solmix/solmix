/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.runtime.monitor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月15日
 */

public class MonitorInfo
{
    /** 可使用内存. */  
    private long totalMemory;   
       
    /** 剩余内存. */  
    private long freeMemory;   
       
    /** 最大可使用内存. */  
    private long maxMemory;   
       
    /** 操作系统. */  
    private String osName;   
       
    /** 总的物理内存. */  
    private long totalMemorySize;   
       
    /** 剩余的物理内存. */  
    private long freePhysicalMemorySize;   
       
    /** 已使用的物理内存. */  
    private long usedMemory;   
       
    /** 线程总数. */  
    private int totalThread;   
       
    /** cpu使用率. */  
    private double cpuRatio;

    
    /**
     * @return the totalMemory
     */
    public long getTotalMemory() {
        return totalMemory;
    }

    
    /**
     * @param totalMemory the totalMemory to set
     */
    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    
    /**
     * @return the freeMemory
     */
    public long getFreeMemory() {
        return freeMemory;
    }

    
    /**
     * @param freeMemory the freeMemory to set
     */
    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    
    /**
     * @return the maxMemory
     */
    public long getMaxMemory() {
        return maxMemory;
    }

    
    /**
     * @param maxMemory the maxMemory to set
     */
    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    
    /**
     * @return the osName
     */
    public String getOsName() {
        return osName;
    }

    
    /**
     * @param osName the osName to set
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    
    /**
     * @return the totalMemorySize
     */
    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    
    /**
     * @param totalMemorySize the totalMemorySize to set
     */
    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    
    /**
     * @return the freePhysicalMemorySize
     */
    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    
    /**
     * @param freePhysicalMemorySize the freePhysicalMemorySize to set
     */
    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    
    /**
     * @return the usedMemory
     */
    public long getUsedMemory() {
        return usedMemory;
    }

    
    /**
     * @param usedMemory the usedMemory to set
     */
    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    
    /**
     * @return the totalThread
     */
    public int getTotalThread() {
        return totalThread;
    }

    
    /**
     * @param totalThread the totalThread to set
     */
    public void setTotalThread(int totalThread) {
        this.totalThread = totalThread;
    }

    
    /**
     * @return the cpuRatio
     */
    public double getCpuRatio() {
        return cpuRatio;
    }

    
    /**
     * @param cpuRatio the cpuRatio to set
     */
    public void setCpuRatio(double cpuRatio) {
        this.cpuRatio = cpuRatio;
    }  
    
}
