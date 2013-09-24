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

package org.solmix.sgt.client.panel;

import java.util.Map;

import com.smartgwt.client.widgets.Canvas;

/**
 *通过构造函数注入，由于Gwt的GIN在编译时自动注入，所以在构造函数中的代码在运行时已经被GIN静态注入了，所以要实现通过传入参数
 *来更加灵活的实现界面配置，可以使用{@link #setParameters(Map)}注入参数。
 * @author Administrator
 * @version 110035 2013-1-4
 */

public interface PanelFactory
{

    Canvas create();

    /**
     * Get this panel's description
     * 
     * @return
     */
    String getDescription();

    String getViewName();

    String getID();

    void setParameters(Map<String,Object> params);

    void setContainer(Canvas containerTarget);


}
