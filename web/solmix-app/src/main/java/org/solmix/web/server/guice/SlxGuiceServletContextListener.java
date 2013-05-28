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
package org.solmix.web.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;



/**
 * 
 * @author solomon
 * @version $Id$  2011-6-12
 */

public class SlxGuiceServletContextListener extends GuiceServletContextListener
{

   /**
    * {@inheritDoc}
    * 
    * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
    */
   @Override
   protected Injector getInjector()
   {
        return Guice.createInjector(new ServerModule(), new DispatchServletModule());
   }

}