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

package com.solmix.web.box.client.ui;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class TaskBarShell extends AbstractBoxShell
{

    private final static int LARGE_WIDTH = 160;

    private final static int MIDDLE_WIDTH = 60;

    private final static int SMALL_WIDTH = 48;

    public final int HEIGHT = TaskBar.HEIGHT;

    public final int LIMITE_WIDTH = TaskBar.LIMITE_WIDTH;

    private static TaskBarShell instance;

    protected TaskBar _taskBar;

    private TaskBarShell()
    {
        _taskBar = new TaskBar();
        _taskBar.setStyleName("taskBar");
        initWidget(_taskBar);

    }

    public TaskBar getTaskBar() {

        return _taskBar;
    }

    public static TaskBarShell getInstance() {
        if (instance == null) {
            instance = new TaskBarShell();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.web.box.client.ui.HasRightClickHandler#addRightClickHandler(com.solmix.web.box.client.ui.RightClickHandler)
     */
    @Override
    public void addRightClickHandler(RightClickHandler handler) {
        _taskBar.addRightClickHandler(handler);

    }
}
