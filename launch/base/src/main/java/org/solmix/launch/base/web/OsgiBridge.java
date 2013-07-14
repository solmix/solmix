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

package org.solmix.launch.base.web;

import java.util.Map;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.solmix.launch.base.internal.Solmix;
import org.solmix.launch.base.shared.Notifiable;

/**
 * 
 * @author ffz
 * @version 0.0.1
 * @since 0.0.4
 */

public class OsgiBridge extends Solmix
{

    /**
     * @param notifiable
     * @param propOverwrite
     * @param servletContext
     * @throws Exception
     */
    public OsgiBridge(Notifiable notifiable, Map<String, String> propOverwrite, ServletContext servletContext) throws Exception
    {
        super(notifiable, propOverwrite);
        servletContext.setAttribute(BundleContext.class.getName(), getBundleContext());
    }

}
