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

import org.solmix.web.server.handlers.LockScreenHandler;
import org.solmix.web.server.handlers.LogoutHandler;
import org.solmix.web.server.handlers.MenuConfigHandler;
import org.solmix.web.shared.action.LockScreenAction;
import org.solmix.web.shared.action.LogoutAction;
import org.solmix.web.shared.action.MenuConfigAction;

import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.server.guice.HandlerModule;
import com.gwtplatform.dispatch.server.guice.actionvalidator.DefaultActionValidator;



/**
 * 
 * @author solomon
 * @version $Id$  2011-5-17
 */
public class ServerModule extends HandlerModule
{

  public MenuConfigHandler getMenuConfigHandler(){
      return new MenuConfigHandler();
   }
   
   public ActionValidator getDefaultActionValidator() {
     return new DefaultActionValidator();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.server.guice.HandlerModule#configureHandlers()
    */
   @Override
   protected void configureHandlers()
   {
     bindHandler(MenuConfigAction.class, MenuConfigHandler.class);
     bindHandler(LogoutAction.class, LogoutHandler.class);
     bindHandler(LockScreenAction.class, LockScreenHandler.class);
   }

}
