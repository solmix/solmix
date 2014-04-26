/*
 * Copyright 2012 The Solmix Project
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
package org.solmix.fmk.js.jackson;

import java.util.LinkedList;

/**
 * outside control the jackson json format.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.2
 * @version 110035 2011-2-16 solmix-ds
 */
public class ShellHandler
{

   /**
    * @return the currentField
    */
   public String getCurrentField()
   {
      return currentField;
   }

   private final LinkedList<LFlag> tailList;

   private final LFlag secondLast;

   private boolean firstField = false;

   /**
    * @return the firstField
    */
   public boolean isFirstField()
   {
      return firstField;
   }

   /**
    * @param firstField
    *           the firstField to set
    */
   public void setFirstField(boolean firstField)
   {
      this.firstField = firstField;
   }

   private Boolean shouldShell = false;

   private String currentField;

   private String previousField;

   /**
    * @return the previousField
    */
   public String getPreviousField()
   {
      return previousField;
   }

   /**
    * @param previousField
    *           the previousField to set
    */
   public void setPreviousField(String previousField)
   {
      this.previousField = previousField;
   }

   /**
    * @return the shouldShell
    */
   public Boolean isShouldShell()
   {

      return shouldShell;
   }

   /**
    * @param shouldShell the shouldShell to set
    */
   public void setShouldShell(boolean shouldShell)
   {
      this.shouldShell = shouldShell;
   }

   public LFlag getLast()
   {
      if (tailList == null || tailList.size() <= 0)
         return null;
      return tailList.getLast();
   }

   public LFlag getFirst()
   {
      if (tailList == null || tailList.size() <= 0)
         return null;
      return tailList.getFirst();
   }
   /**
    * 
    */
   private static final long serialVersionUID = 7891911392577702579L;

   public ShellHandler()
   {
      tailList = new LinkedList<LFlag>();
      secondLast = null;
   }

   public void push(LFlag obj)
   {
      
      if (tailList.size() > 0) {
         if (tailList.getLast().equals(obj))
            return;
      }
      tailList.push(obj);

   }

   /**
    * 
    */
   public void pop(LFlag obj)
   {
      if (tailList.size() > 0) {
         if (tailList.getLast().equals(obj))
            return;
      }
      if (tailList.isEmpty() || tailList == null)
         return;
      tailList.pop();

   }

   public LFlag getSecondLast()
   {
      return secondLast;
   }

   /**
    * @param name
    */
   public void setCurrentField(String name)
   {
      this.currentField = name;

   }

}

