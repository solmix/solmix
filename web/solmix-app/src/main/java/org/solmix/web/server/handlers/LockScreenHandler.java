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
package org.solmix.web.server.handlers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.solmix.web.shared.action.LockScreenAction;
import org.solmix.web.shared.action.LockScreenResult;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-31
 */

public class LockScreenHandler  implements ActionHandler<LockScreenAction, LockScreenResult>
{

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#execute(com.gwtplatform.dispatch.shared.Action, com.gwtplatform.dispatch.server.ExecutionContext)
     */
    @Override
    public LockScreenResult execute(LockScreenAction arg0, ExecutionContext arg1) throws ActionException {
        final Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null)
        return new LockScreenResult(true);
        return new LockScreenResult(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#getActionType()
     */
    @Override
    public Class<LockScreenAction> getActionType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#undo(com.gwtplatform.dispatch.shared.Action, com.gwtplatform.dispatch.shared.Result, com.gwtplatform.dispatch.server.ExecutionContext)
     */
    @Override
    public void undo(LockScreenAction arg0, LockScreenResult arg1, ExecutionContext arg2) throws ActionException {
        // TODO Auto-generated method stub
        
    }

}
