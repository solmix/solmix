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
package org.solmix.api.datasource;

import org.solmix.api.data.DataSourceData;
import org.solmix.api.exception.SlxException;


/**
 * 
 * @author Administrator
 * @version 110035  2011-6-22
 */

public interface DataSourceGenerator
{
   public static final String INHERIT_KEY = "_inheritsForm";
  DataSource generateDataSource(DataSourceData context)throws SlxException;
   
}
