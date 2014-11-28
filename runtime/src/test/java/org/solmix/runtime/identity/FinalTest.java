/**
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.identity;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年6月22日
 */

public class FinalTest {

    /**
     * @param args
     */
    @Test
    public void main() {
        String a = new String("aaaaaaaaaaaa");
        String b = a;
        System.out.println(b);
        a = new String("bbbbbbb");
        System.out.println(b);
        Map am = new HashMap();
        am.put("AA", "aaaaaaa");
        final Map bm = am;
        System.out.println(bm.get("AA"));
        am.put("AA", "bbbbbbbbbbbb");
        System.out.println(bm.get("AA"));
    }

}
