/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.launch.base.shared;

import java.io.File;

/**
 * <code>Servlet</code>或者其他<code>Main Class</code> 通过实现这个接口来实现OSGI的加载
 * 
 * @author ffz
 * @version 0.0.1 2012-3-16
 * @since 0.0.4
 */

public interface Notifiable
{

    /**
     * Called when the OSGi framework has been stopped
     */
    void stopped();

    void updated(File tmpFile);

}
