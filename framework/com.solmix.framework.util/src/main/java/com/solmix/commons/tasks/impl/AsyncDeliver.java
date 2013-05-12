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

package com.solmix.commons.tasks.impl;

import java.util.ArrayList;
import java.util.List;

import com.solmix.commons.tasks.CommandTask;
import com.solmix.commons.tasks.TaskDeliver;
import com.solmix.commons.tasks.TaskThreadPool;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-24
 */

public class AsyncDeliver implements TaskDeliver
{

    private TaskThreadPool taskPool;

    public AsyncDeliver(final TaskThreadPool taskPool)
    {
        this.taskPool = taskPool;
    }

    @Override
    public void execute(List<CommandTask> tasks) {
        if (tasks == null)
            return;
        EventExecuter executer = null;
        for (CommandTask subTasks : tasks) {
            executer = new EventExecuter(subTasks);
            taskPool.executeTask(executer);
        }
    }

    protected List<List<CommandTask>> splitBySize(List<CommandTask> tasks, int size) {
        List<List<CommandTask>> _return = new ArrayList<List<CommandTask>>();
        List<CommandTask> subList = new ArrayList<CommandTask>();
        for (int i = 0; i < tasks.size(); i++) {

            if ((i != 0 && i % size == 0)) {
                _return.add(subList);
                subList = new ArrayList<CommandTask>();
            }
            subList.add(tasks.get(i));
            if (i == (tasks.size() - 1)) {
                _return.add(subList);
            }
        }
        return _return;

    }

    private final class EventExecuter implements Runnable
    {

        private CommandTask _task;

        public EventExecuter(CommandTask task)
        {
            _task = task;
        }

        @Override
        public void run() {
            _task.execute();
        }

    }

}
