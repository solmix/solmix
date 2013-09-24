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
package org.solmix.web.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.solmix.SlxConstants;
import org.slf4j.Logger;
import org.solmix.fmk.servlet.ServletTools;
import org.solmix.fmk.util.OSGIHelper;


/**
 * The parent of Servlets.used this servlet to initial services.
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035  2011-2-11 gwt 
 */
public class BaseServlet extends HttpServlet
{
   /**
    * General serial ID
    */
   private static final long serialVersionUID = 7154520115343294756L;

//   protected static ConfigRealm config;

   protected static String webRoot;

   protected Logger log;

   public BaseServlet()
   {

   }

   @Override
   public void init(ServletConfig servletConfig) throws ServletException
   {
      super.init(servletConfig);
      //osgi environment or not
      if (!SlxConstants.isOSGI()) {
         if (OSGIHelper.servletContext == null)
         OSGIHelper.servletContext = servletConfig.getServletContext();
      }
      String servletName = servletConfig.getServletName();
      String className = getClass().getName();
      String logPrefix = null;
      if (!className.endsWith(servletName))
         logPrefix = servletName;
      log = new Logger(className, logPrefix);
//      config = Activator.getDefault().getCMService();
      // ServletTools.applyConfigToServletOrFilter(this, servletConfig);
   }

   /**
    * Handle response top level Throwable
    * 
    * @param response
    * @param t
    */
   public void handleError(HttpServletResponse response, Throwable t)
   {
      handleError(response, (new StringBuilder()).append(getClass().getName()).append(" top-level exception")
         .toString(), t);
   }

   /**
    * Handle response top level {@link java.lang.Throwable}
    * 
    * @param response
    * @param errorMessage
    * @param t
    */
   public void handleError(HttpServletResponse response, String errorMessage, Throwable t)
   {
      Logger.observeThread();
      log.error("Top-level servlet error: ", t.getMessage());
      ServletTools.handleServletError(response, errorMessage, t);
   }
}
