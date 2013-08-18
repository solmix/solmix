/*
 * SOLMIX PROJECT
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

package org.solmix.commons.tasks.impl;

import java.util.List;

import org.solmix.commons.tasks.CommandTask;
import org.solmix.commons.tasks.TaskAdmin;
import org.solmix.commons.tasks.TaskDeliver;
import org.solmix.commons.tasks.TaskSet;
import org.solmix.commons.tasks.TaskThreadPool;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-24
 */

public class ParallelTaskAdmin implements TaskAdmin
{


    private TaskDeliver deliver;

    TaskThreadPool asy_pool;

    private int colePoleSize = 20;

    
    public int getPoolSize() {
        return colePoleSize;
    }

    
    public void setPoolSize(int poolSize) {
        this.colePoleSize = poolSize;
    }

    private String groupThreadName = "TaskThread";

    ParallelTaskAdmin()
    {

    }

    public static TaskAdmin getInstance() {
      
        return new ParallelTaskAdmin();
    }

    @Override
    public void process(List<CommandTask> tasks) {
        initDeliver();
        deliver.execute(tasks);

    }

    public TaskSet process(TaskSet taskset) {
        initDeliver();
        deliver.execute(taskset.getTasks());
        return taskset;

    }

    private void initDeliver() {
        if (deliver == null) {
            // syn_pool= new TaskThreadPool(40,false);
            asy_pool = new TaskThreadPool(colePoleSize, this.getGroupThreadName());
            deliver = new AsyncDeliver(asy_pool);
        }
    }

   
    /**
     * Once the garbage collector frees memory space occupied by the object,
     *  the first call this method.
     * 
     */
    @Override
    protected void finalize() {
        if (asy_pool != null)
            asy_pool.shutdown();
    }

    public String getGroupThreadName() {
        return groupThreadName;
    }

    public void setGroupThreadName(String groupThreadName) {
        this.groupThreadName = groupThreadName;
    }

}
