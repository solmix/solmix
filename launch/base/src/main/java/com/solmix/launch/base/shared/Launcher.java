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

package com.solmix.launch.base.shared;

import java.util.Map;

/**
 * The <code>Launcher</code> interface is implements by the delegate classes inside the Launcher package. and used by
 * the actual Main class or servlet to configure and start the framework.
 * 
 * @author ffz
 * @version 0.0.1 2012-3-16
 * @since 0.0.4
 */

public interface Launcher
{

    /**
     * Starts the framework and returns <code>true</code> if successfull.
     */
    public boolean start();

    /**
     * Stops the framework. This method only returns when the framework has actually been stopped. This method may be
     * used by the main class or servlet to initiate a shutdown of the framework.
     */
    public void stop();

    /**
     * Sets the solmix.home to be used for starting the framework. This method must be called with a non-
     * <code>null</code> argument before trying to start the framework.
     */
    public void setSolmixHome(String solmixHome);

    /**
     * The {@link Notifiable} to notify on framework stop or update
     */
    public void setNotifiable(Notifiable notifiable);

    /**
     * The commandline provided from the standalone launch case.
     */
    public void setCommandLine(Map<String, String> args);

}
