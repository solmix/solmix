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
package org.solmix.web.client.im;

import org.atmosphere.gwt.client.AtmosphereClient;


/**
 * 
 * @author solmix
 * @version $Id$  2011-11-18
 */

public class IMWindowFactory
{
    AtmosphereClient m_client;
    CometManager m_manager;
    public IMWindowFactory(AtmosphereClient client,CometManager manager){
        m_client=client;
        m_manager = manager;
    }
    public BaseChartWindow createUserWindow(String userName,long userId){
        BaseChartWindow _return  = new BaseChartWindow(m_client);
        _return.targetUserId=userId;
        _return.targetUserName=userName;
        _return.setTitle("与  "+userName+" 聊天");
        m_manager.registerHander(_return);
       
        return _return;
        
    }

}
