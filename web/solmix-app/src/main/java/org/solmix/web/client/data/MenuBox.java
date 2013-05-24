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
package org.solmix.web.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.WidgetFactory;
import org.solmix.web.client.admin.view.AdminMainView;
import org.solmix.web.client.admin.view.UserInfoView;
import org.solmix.web.client.basic.view.CSMdyrmrStatView;
import org.solmix.web.client.chart.BarChartView;
import org.solmix.web.client.comet.AtmosphereCometView;
import org.solmix.web.client.sandbox.SimpleExportView;
import org.solmix.web.client.sandbox.view.MenuConfigView;
import org.solmix.web.client.sandbox.view.SandBoxMainView;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-17
 */

public class MenuBox
{
   private static String ROOT="root";
   private static Map<String, WidgetFactory> panelMap;
   private static List<WidgetEntry> container = new ArrayList<WidgetEntry>();
   static{
      addPanel(NameTokens.SandBox,"main",new SandBoxMainView.Factory());
      addPanel(NameTokens.SandBox,"menu_config",new MenuConfigView.Factory());
      addPanel(NameTokens.SandBox,"export_simple",new SimpleExportView.Factory());
      addPanel(NameTokens.SandBox,"atmosphere_comet",new AtmosphereCometView.Factory());
      addPanel(NameTokens.SandBox,"bar_chart",new BarChartView.Factory());
      addPanel(NameTokens.Admin,"main",new AdminMainView.Factory());
      addPanel(NameTokens.Admin,"user_manager",new UserInfoView.Factory());
      addPanel(NameTokens.Basic,"main",new AdminMainView.Factory());
      addPanel(NameTokens.Basic,"csm_stat",new CSMdyrmrStatView.Factory());
   }
 
  public static void addPanelEntry(WidgetEntry entry){
     container.add(entry);
  }
  public static void addPanel(String module,String name,WidgetFactory factory){
     addPanelEntry( new WidgetEntry(module,name,factory));
  }
  public static WidgetFactory getFactory(String module,String name){
     if(module==null)
        module=ROOT;
     if(container!=null){
        for(WidgetEntry entry : container){
           if(module.equals(entry.getModule())&&name.equals(entry.getName()))
              return entry.getFactory();
        }
        
     }
     return null;
  }
  public static WidgetFactory getFactory(String name){
     return getFactory(ROOT,name);
  }
}
