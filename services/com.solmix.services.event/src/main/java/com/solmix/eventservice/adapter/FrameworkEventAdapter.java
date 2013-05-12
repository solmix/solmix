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
package com.solmix.eventservice.adapter;

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import com.solmix.eventservice.EventAdapter;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-2
 */

public class FrameworkEventAdapter implements FrameworkListener, EventAdapter
{

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.eventservice.EventAdapter#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.framework.FrameworkListener#frameworkEvent(org.osgi.framework.FrameworkEvent)
     */
    @Override
    public void frameworkEvent(FrameworkEvent event) {
        // TODO Auto-generated method stub

    }

}
