/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.servlets.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-17
 */

public class PatternUtil
{

    /**
     * @param args
     */
    public static void main(String[] args) {
        Pattern p = Pattern.compile("/*");
        Matcher m = p.matcher("/");
        boolean b = m.find();
        System.out.println(b);
        List l = new ArrayList();
        l.add("11");
        l.add("11");
        l.add("11");
        l.add("11");
        l.add("11");
        l.add("11");
        l.add("11");
        for (int i = 0; i < l.size(); i++) {
            l.remove(i);
        }

    }

}
