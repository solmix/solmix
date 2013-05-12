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

package com.solmix.commons.tasks;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-24
 */

public class TaskSet
{

    private boolean prcessed;

    private List<CommandTask> tasks;

    public boolean isPrcessed() {
        return prcessed;
    }

    public void setPrcessed(boolean prcessed) {
        this.prcessed = prcessed;
    }

    TaskSet(List<CommandTask> tasks)
    {
        this.tasks = tasks;
    }

    public List<CommandTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(List<CommandTask> tasks) {
        this.tasks = tasks;
    }
}
