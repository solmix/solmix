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

package org.solmix.web.shared.action;

import org.solmix.web.shared.result.MenuConfigResult;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-18
 */

public class MenuConfigAction implements Action<MenuConfigResult>
{

   private String moduleName;
   MenuConfigAction()
   {

   }

   public MenuConfigAction(String moduleName)
   {
     this.moduleName=moduleName;
   }
   

   
   /**
    * @return the moduleName
    */
   public String getModuleName()
   {
      return moduleName;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.shared.Action#getServiceName()
    */
   public String getServiceName()
   {

      return UnsecuredActionImpl.DEFAULT_SERVICE_NAME;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.shared.Action#isSecured()
    */
   public boolean isSecured()
   {
      return false;
   }

}
