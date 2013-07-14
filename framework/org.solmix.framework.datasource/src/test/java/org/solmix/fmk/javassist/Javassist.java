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
package org.solmix.fmk.javassist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035  2011-2-14 solmix-ds 
 */
public class Javassist
{

   /**
    * @param args
    * @throws NotFoundException
    * @throws CannotCompileException
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws NoSuchFieldException
    * @throws SecurityException
    */
   public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException,
      IllegalAccessException, SecurityException, NoSuchFieldException
   {
      ObjectFactory factory = new ObjectFactory();
      List list = new ArrayList();
      list.add("1");
      TdataSource ds = transfom(list);
      System.out.println(ds + ":");

   }

   public static TdataSource transfom(List value)
   {
      TdataSource tds = null;
      try {
         ClassPool cp = ClassPool.getDefault();
         CtClass cc = cp.get("org.solmix.api.jaxb.TdataSource");
         CtField field = new CtField(cp.get("java.util.List"), "field", cc);
         field.setModifiers(1);
         cc.addField(field);
         tds = (TdataSource) cc.toClass().newInstance();
         Field fild = tds.getClass().getDeclaredField("field");
         List list = new ArrayList();
         list.add("1");
         fild.set(tds, list);
      } catch (SecurityException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (NotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (CannotCompileException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (InstantiationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (NoSuchFieldException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return tds;
   }

}
