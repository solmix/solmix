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
package org.solmix.fmk.pool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.solmix.commons.util.DataUtil;


/**
 * 
 * @author Administrator
 * @version 110035  2011-9-13
 */

public class Pool
{

    /**
     * @param args
     * @throws Exception 
     * @throws IllegalStateException 
     * @throws NoSuchElementException 
     */
    public static void main(String[] args) throws NoSuchElementException, IllegalStateException, Exception {
//        TPoolableObjectFactory p = new TPoolableObjectFactory();
//        GenericKeyedObjectPoolFactory  f = new GenericKeyedObjectPoolFactory(p);
//        KeyedObjectPool pool= f.createPool();
////        pool.addObject("12");
//        for(int i=0;i<30;i++){
//            
//           Object o= pool.borrowObject("12");
//           System.out.println(o.toString());
//           if(i%2==0)
//           pool.returnObject("12", o);
//        }
        System.out.println("vaule:'01'".indexOf(":")!=-1?"vaule:'01'".substring(0,"vaule:'01'".indexOf(":")+1):"");
        String a ="csm$C_CUST";
        List<String> b = DataUtil.simpleSplit(a, "$");
        System.out.println(b.get(0)+"-----"+b.get(1));
        Map map = new HashMap();
        DataUtil.makeList(Pool.class);
//        System.out.println(a.indexOf("$"));
    }

}
