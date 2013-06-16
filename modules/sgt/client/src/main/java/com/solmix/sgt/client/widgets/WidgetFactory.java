/*
 * ========THE SOLMIX PROJECT=====================================
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

package com.solmix.sgt.client.widgets;

import com.smartgwt.client.widgets.Canvas;

/**
 * @author Administrator
 * 
 */
public interface WidgetFactory
{

    /**
     * Create the Canvas,If for control box canvas,may input list parameter.these parameters will initialed in this
     * method.
     * 
     * @return
     */
    Canvas create();

    /**
     * Get the component ID
     * 
     * @return
     */
    String getID();
    String getName();

    /**
     * Get this panel's description
     * 
     * @return
     */
    String getDescription();

    void setParameters(Object... paramaters);
    void setContainer(Canvas containerTarget);

}
