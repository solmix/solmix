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
package org.solmix.sgt.client.panel;

import org.solmix.sgt.client.widgets.WidgetFactory;



/**
 * Data constructor for widget.the scope order is module>unit>name.
 * @author solmix.f@gmail.com
 * @version 110035  2011-5-30
 */

public class WidgetEntry{

   private String module;
   private String name;
   private WidgetFactory factory;
   private Object instance;
   /**
    * @return the instance
    */
   public Object getInstance()
   {
      return instance;
   }
   
   /**
    * @param instance the instance to set
    */
   public void setInstance(Object instance)
   {
      this.instance = instance;
   }
public WidgetEntry(String module,String name,WidgetFactory factory){
     this.module=module;
     this.name=name;
     this.factory=factory;
  }
   /**
    * @return the module
    */
   public String getModule()
   {
      return module;
   }
   
   /**
    * @param module the module to set
    */
   public void setModule(String module)
   {
      this.module = module;
   }
   
   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * @return the factory
    */
   public WidgetFactory getFactory()
   {
      return factory;
   }
   
   /**
    * @param factory the factory to set
    */
   public void setFactory(WidgetFactory factory)
   {
      this.factory = factory;
   }
}
