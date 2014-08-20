/*
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

package org.solmix.fmk.servlets.internal;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月20日
 */

public class SQLInjectTest
{

    private final String pattern = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(add|exec|insert|select|delete|update|count|mid|master|truncate|char|declare)\\b)";

    Pattern sqlPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

    @Test
    public void test() {
        String a1="insert into";
        assertTrue(find(a1));
        String a2="param with '";
        assertTrue(find(a2));
        String a3="param with add";
        assertTrue(find(a3));
        String a4="param with -- ";
        assertTrue(find(a4));
        String a5="/**zhus*/";
        assertTrue(find(a5));
        
    }

    protected boolean find(String value) {
        if (sqlPattern.matcher(value).find()) {
            return true;
        }
        return false;
    }
}
