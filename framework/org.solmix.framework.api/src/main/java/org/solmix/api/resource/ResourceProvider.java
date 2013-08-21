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

package org.solmix.api.resource;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

/**
 * Used for resource register.
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.4 2012-4-12
 * @since 0.0.4
 */

public interface ResourceProvider
{

    Resource getResource(ResourceResolver resourceResolver, HttpServletRequest request, String path);

    Resource getResource(ResourceResolver resourceResolver, String path);

    Iterator<Resource> listChildren(Resource parent);
}
