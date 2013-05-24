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
import org.solmix.web.shared.action.LogoutAction;
import org.solmix.web.shared.action.LogoutResult;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * 
 * @author Administrator
 * @version $Id$ 2011-8-8
 */

public class LogoutHandler implements ActionHandler<LogoutAction, LogoutResult>
{

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#execute(com.gwtplatform.dispatch.shared.Action,
     *      com.gwtplatform.dispatch.server.ExecutionContext)
     */
    @Override
    public LogoutResult execute(LogoutAction action, ExecutionContext arg1) throws ActionException {
        if (action.getType() == 1) {
            final Subject currentUser = SecurityUtils.getSubject();
            currentUser.logout();
            return new LogoutResult(true, false);
        } else if (action.getType() == 2) {
            final Subject currentUser = SecurityUtils.getSubject();
            if (currentUser != null)
                return new LogoutResult(false, true);

        }
        return new LogoutResult(false, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#getActionType()
     */
    @Override
    public Class<LogoutAction> getActionType() {
        return LogoutAction.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#undo(com.gwtplatform.dispatch.shared.Action,
     *      com.gwtplatform.dispatch.shared.Result, com.gwtplatform.dispatch.server.ExecutionContext)
     */
    @Override
    public void undo(LogoutAction arg0, LogoutResult arg1, ExecutionContext arg2) throws ActionException {
        // TODO Auto-generated method stub

    }

}
