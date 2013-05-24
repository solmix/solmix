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

package org.solmix.web.client.widgets;

import org.solmix.web.client.WidgetFactory;

import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author Administrator
 * @version $Id$ 2011-8-7
 */

public abstract class AbstractFactory implements WidgetFactory
{

   protected Object[] parameters;
   protected Canvas container;

   protected String id;

   protected Canvas instance;
   protected String desc="";

   public void setParameters(Object... paramaters)
   {
      this.parameters = paramaters;
   }
  public void setContainer(Canvas containerTarget){
       container=containerTarget;
   }
   public String getID()
   {
      if (instance == null)
         instance = this.create();
      if (instance != null)
         id = instance.getID();
      return id;
   }

   public String getDescription()
   {
      return desc;
   }
   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.WidgetFactory#getName()
    */
   @Override
   public String getName() {
      String clzName= this.getClass().getName();
     return clzName.substring(0, clzName.lastIndexOf("$"));
   }
}
