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
package org.solmix.web.servlets;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.solmix.SlxConstants;
import org.slf4j.Logger;
import org.solmix.fmk.util.OSGIHelper;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-6-5
 */

public abstract class BaseFilter  implements Filter
{

//   protected static ConfigRealm config;

   protected static String webRoot;
   protected ServletContext servletContext;

   protected Logger log;
   /**
    * {@inheritDoc}
    * 
    * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    */
   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {
      //osgi environment or not
      if (!SlxConstants.isOSGI()) {
         if (OSGIHelper.servletContext == null)
         OSGIHelper.servletContext = filterConfig.getServletContext();
      }
      servletContext = filterConfig.getServletContext();
      String filterName = filterConfig.getFilterName();
      String className = getClass().getName();
      String logPrefix = null;
      if (!className.endsWith(filterName))
         logPrefix = filterName;
      log = new Logger(className, logPrefix);
//      config = Util.getCMService();
      
   }
}
