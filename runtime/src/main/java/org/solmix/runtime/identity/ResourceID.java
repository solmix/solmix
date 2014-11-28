/**
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.identity;

import java.net.URI;

/**
 * 资源标识(Resource ID),通常用于标识文件(files),目录(directories),URL等
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public interface ResourceID extends ID {

    /**
     * 将该资源ID转化为URI
     * 
     * @return
     */
    URI toURI();
}
