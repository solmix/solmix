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
package org.solmix.web.wicket.authentication;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;


/**
 * 
 * @author solomon
 * @version $Id$  2011-6-12
 */

public class AuthenticatedWebApplication extends WebApplication
{

   /**
    * {@inheritDoc}
    * 
    * @see org.apache.wicket.Application#getHomePage()
    */
   @Override
   public Class<? extends Page> getHomePage()
   {
      // TODO Auto-generated method stub
      return LoginPage.class;
   }

}
