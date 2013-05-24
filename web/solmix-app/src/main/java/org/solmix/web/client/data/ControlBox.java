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
import org.solmix.web.client.widgets.controls.PanelContextMenu;

import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-31
 */

public class ControlBox
{

   private static String ROOT = "root";

   private static String ADMIN = NameTokens.Admin;

   public static String SEPARATOR = "Separator";

   private static List<WidgetEntry> container = new ArrayList<WidgetEntry>();
   static
   {
      addControlEntry(ROOT, "panel_context_menu", new PanelContextMenu.Factory());
   }

   /**
    * Add a new control-widget entry into control-box.
    * 
    * @param entry
    */
   public static void addControlEntry(WidgetEntry entry)
   {
      container.add(entry);
   }

   /**
    * Add a new control-widget entry into control-box.
    * 
    * @param module module name
    * @param name control-widget name
    * @param factory control-widget factory class
    */
   public static void addControlEntry(String module, String name, WidgetFactory factory)
   {
      addControlEntry(new WidgetEntry(module, name, factory));
   }

   public static Canvas[] getControl(String... controlNames)
   {
      if (controlNames == null)
         return null;
      Canvas[] _return = new Canvas[controlNames.length];
      int i = 0;
      for (String name : controlNames)
      {
         WidgetFactory factory = getFactory(name);
         _return[i] = factory.create();
         i++;
      }
      return _return;
   }

   public static Canvas getControl(String controlName, Object... paramaters)
   {
      return getControl(ROOT, controlName, paramaters);

   }

   /**
    * @param module which the control-widget belong to.
    * @param controlName
    * @param paramaters parameters need to create this control-widget.
    * @return
    */
   public static Canvas getControl(String module, String controlName, Object[] parameters)
   {
      WidgetFactory factory = getFactory(module, controlName);
      factory.setParameters(parameters);
      return factory.create();

   }
   public static void addToTarget(Canvas containerTarget,String module,String controlName,Object[] parameters){
       WidgetFactory factory = getFactory(module, controlName);
       factory.setParameters(parameters);
       factory.setContainer(containerTarget);
       factory.create();
   }
   public static void addToTarget(Canvas containerTarget,String controlName,Object[] parameters){
       addToTarget(containerTarget,ROOT,controlName,parameters);
   }
   /**
    * @param controlMaps module:controlName
    * @return
    */
   public static Canvas[] getControl(Map<String, String> controlMaps)
   {
      if (controlMaps == null)
         return null;
      Canvas[] _return = new Canvas[controlMaps.size()];
      int i = 0;
      for (String moduleName : controlMaps.keySet())
      {
         String name = controlMaps.get(moduleName);
         WidgetFactory factory = getFactory(moduleName, name);
         _return[i] = factory.create();
         i++;
      }
      return _return;
   }

   /**
    * @param name
    * @return
    */
   public static WidgetFactory getFactory(String name)
   {
      return getFactory(ROOT, name);
   }

   /**
    * @param module
    * @param name
    * @return
    */
   public static WidgetFactory getFactory(String module, String name)
   {
      if (module == null)
         module = ROOT;
      if (container != null)
      {
         for (WidgetEntry entry : container)
         {
            if (module.equals(entry.getModule()) && name.equals(entry.getName()))
               return entry.getFactory();
         }

      }
      return null;

   }
}
