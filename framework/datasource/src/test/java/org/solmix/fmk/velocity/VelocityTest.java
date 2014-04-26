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
package org.solmix.fmk.velocity;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.util.DateUtil;


/**
 * 
 * @author Administrator
 * @version 110035  2011-9-15
 */

public class VelocityTest
{

    /**
     * @param args
     * @throws SlxException 
     */
    @Test
    public  void main() throws SlxException {
        Map map  = new HashMap();
        map.put("DateUtil", new DateUtil());
        String s ="#set($ceria=\"201109\") \n$DateUtil.getFirstDayofMouth($ceria,\"yyyyMM\",\"yyyyMMdd\")";
       String str = Velocity.evaluateAsString(s, map);
       Assert.assertEquals("20110901", str);

    }
    @Test
    public  void math() throws SlxException {
        Map map  = new HashMap();
        map.put("DateUtil", new DateUtil());
        String s ="#set($ceria=\"201109\")  \n$DateUtil.getFirstDayofMouth($ceria,\"yyyyMM\",\"yyyyMMdd\")";
       String str = Velocity.evaluateAsString(s, map);
       Assert.assertEquals("20110901", str);

    }
}
