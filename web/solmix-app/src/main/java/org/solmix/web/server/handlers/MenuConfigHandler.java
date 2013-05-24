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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.DSRequestImpl;
import org.solmix.services.datasource.DSRequest;
import org.solmix.services.datasource.DSResponse;
import org.solmix.services.exception.SLXException;
import org.solmix.services.jaxb.Eoperation;
import org.solmix.web.shared.action.MenuConfigAction;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-18
 */

public class MenuConfigHandler implements ActionHandler<MenuConfigAction, MenuConfigResult>
{
   private ServletContext servletContext;
   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#execute(com.gwtplatform.dispatch.shared.Action, com.gwtplatform.dispatch.server.ExecutionContext)
    */
   public MenuConfigResult execute(MenuConfigAction action, ExecutionContext context) throws ActionException
   {
      DSRequest req=new DSRequestImpl("menuConfig",Eoperation.FETCH);
      Map criteria = new HashMap();
      criteria.put("moduleName", action.getModuleName());
      req.getContext().setCriteria(criteria);
      List<MenuConfigBean> res=new ArrayList<MenuConfigBean>();
      try
      {
        DSResponse resp= req.execute();
        List<Map<Object,Object>> data=resp.getRecords();
        if(data!=null){
           for(Object obj:data){
                 Map propMap =(Map)obj;
                 MenuConfigBean bean = new MenuConfigBean();
                 DataUtil.setProperties(propMap, bean);
                 res.add(bean);
           }
        }
//        DataUtil.setProperties(propertyMap, bean)
      } catch (SLXException e)
      {
         e.printStackTrace();
      } catch (Exception e)
      {
         e.printStackTrace();
      }
      
      return  new MenuConfigResult(res);
//      return  new MenuConfigResult(MenuData.getResults());
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#getActionType()
    */
   public Class<MenuConfigAction> getActionType()
   {
      return MenuConfigAction.class;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.dispatch.server.actionhandler.ActionHandler#undo(com.gwtplatform.dispatch.shared.Action, com.gwtplatform.dispatch.shared.Result, com.gwtplatform.dispatch.server.ExecutionContext)
    */
   public void undo(MenuConfigAction arg0, MenuConfigResult arg1, ExecutionContext arg2) throws ActionException
   {
      // TODO Auto-generated method stub
      
   }

}
