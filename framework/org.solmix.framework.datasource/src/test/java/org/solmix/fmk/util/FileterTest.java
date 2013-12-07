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
package org.solmix.fmk.util;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.solmix.fmk.i18n.FilterEnumeration;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-3-16
 */

public class FileterTest
{

   public static void main(String[] args)
   {
      FileterTest.class.getResource("");
      Enumeration e = new FilterEnumeration("CLASSPATH:/org/solmix/fmk/util", "*.class");
      while (e.hasMoreElements())
      {
         System.out.println(e.nextElement().toString());
      }
      int planet = 100;
      String event = "[ only for test]";
      String result = MessageFormat.format( "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.", planet, new Date(), event );
      System.out.println( result );
       List<String> l = new ArrayList<String>();
       l.add("1");
       l.add("2");
       l.add("3");
       l.add("4");
       l.add("5");
       l.add("6");
       List<String> l2 = new ArrayList<String>();
       l2.addAll(l);
       for(String k:l){
           if(k.equals("1")){
               l2.remove(k);
           }
       }
       System.out.println(l2.toString());
   }

   @Test
   public void filterTest()
   {
      Enumeration e = new FilterEnumeration("CLASSPATH:/org/solmix/fmk/util", "FileterTest.class");

      while (e.hasMoreElements())
      {
         Assert.assertEquals("FileterTest.class", new File(((URL) e.nextElement()).getFile()).getName());
      }
   }
}
