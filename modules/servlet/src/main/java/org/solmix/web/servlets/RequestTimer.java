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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.solmix.fmk.servlet.RequestContext;

/**
 * Servlet Request Timer
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035  2011-2-12 servlet 
 */
public class RequestTimer
{
   private static Logger log = LoggerFactory.getLogger(RequestTimer.class.getName());

   private final HttpServletRequest request;

   private Date timerStart;

   private Date timerStop;

   public RequestTimer(HttpServletRequest request)
   {
      this.request = request;
      start();
   }

   public void start()
   {
      timerStart = new Date();
   }

   public void stop()
   {
      timerStop = new Date();
      Logger.timing.debug((new StringBuilder()).append("Request: ").append(RequestContext.getRequestPath(request))
         .append(" (start->finish): ").append(timerStop.getTime() - timerStart.getTime()).append("ms").toString());
   }

   public long getFireTime()
   {
      return timerStop.getTime() - timerStart.getTime();
   }
}
