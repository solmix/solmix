/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.exec;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月19日
 */

public class Escape
{
    private static char[] enlargeArray(char[] in){
        char[] res;

        res = new char[in.length * 2];
        System.arraycopy(in, 0, res, 0, in.length);
        return res;
    }

    /**
     * Escape a string by quoting the magical elements 
     * (such as whitespace, quotes, slashes, etc.)
     */
    public static String escape(String in){
        char[] inChars, outChars, resChars;
        int numOut;

        inChars  = new char[in.length()];
        outChars = new char[inChars.length];
        in.getChars(0, inChars.length, inChars, 0);
        numOut = 0;

        for(int i=0; i<inChars.length; i++){
            if(outChars.length - numOut < 5){
                outChars = enlargeArray(outChars);
            }
            
            if(Character.isWhitespace(inChars[i]) ||
               inChars[i] == '\\' ||
               inChars[i] == '\'' ||
               inChars[i] == '\"' ||
               inChars[i] == '&'  ||
               inChars[i] == ';')
            {
                outChars[numOut++] = '\\';
                outChars[numOut++] = inChars[i];
            } else {
                outChars[numOut++] = inChars[i];
            }
        }

        return new String(outChars, 0, numOut);
    }

    public static void main(String[] args){
        System.out.println(Escape.escape("foo bar"));
        System.out.println(Escape.escape("\\\"foo' bar\""));
    }
}
