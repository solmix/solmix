/*
 * Copyright 2012 The Solmix Project
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
package org.solmix.fmk.docs;

import org.solmix.api.datasource.DSRequest;

/**
 * This is the Document for IDACall process.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-3 solmix-ds
 */
public class IDACall_doc
{

   /**
    * When a IDACall receive wrapped into {@link org.solmix.fmk.upload.RequestContext},then call OSGIService
    * {@link org.solmix.api.call.DataSourceCallFactory DataSourceCallFactory}.the responsibility of this service is creating
    * {@link org.solmix.api.call.DSCall DSCall}. the main function of DSCall is send DSRequest and
    * DSResponse. In this way,DSRequest is initial from request data ,and DSResponse is produce by
    * {@link org.solmix.api.datasource.DataSource#execute(DSRequest)}.
    * 
    */
   public void doc()
   {

   }
}
