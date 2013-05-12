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

package com.solmix.commons.util;

import static com.solmix.commons.util.DataUtil.removeEnd;

import java.util.regex.Pattern;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-10
 */

public final class SimpleUrlPattern implements UrlPattern
{

    /**
     * Stable serialVersionUID.
     */
    private static final long serialVersionUID = 223L;

    /**
     * Any char, newline included.
     */
    public static final String URL_CHAR_PATTERN = "."; //$NON-NLS-1$

    /**
     * Regexp pattern used for the simple keyword <code>*</code>. Matches 0 or more characters.
     */
    public static final String MULTIPLE_CHAR_PATTERN = URL_CHAR_PATTERN + "*"; //$NON-NLS-1$

    /**
     * Regexp pattern used for the simple keyword <code>?</code>. Matches 0 or 1 character.
     */
    public static final String SINGLE_CHAR_PATTERN = URL_CHAR_PATTERN + "?"; //$NON-NLS-1$

    /**
     * Regexp pattern used in match().
     */
    private Pattern pattern;

    /**
     * Pattern length. Longer patterns have higher priority.
     */
    private int length;

    /**
     * internal pattern string.
     */
    private String patternString;

    /**
     * Default constructor used by ContentToBean.
     */
    public SimpleUrlPattern()
    {
    }

    /**
     * Compile a regexp pattern handling <code>*</code> and <code>?</code> chars.
     * 
     * @param string input string
     * @return a RegExp pattern
     */
    public SimpleUrlPattern(String string)
    {
        this.length = removeEnd(string, "*").length();
        this.pattern = Pattern.compile(getEncodedString(string), Pattern.DOTALL);
        this.patternString = string;
    }

    /**
     * Replace all "*" with <code>RegexWildcardPattern.MULTIPLE_CHAR_PATTERN</code>.
     * 
     * @param str input string
     * @return string where all the occurrences of <code>*</code> and <code>?</code> are replaced with a regexp pattern.
     */
    public static String getEncodedString(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] chars = str.toCharArray();
        int i = 0, last = 0;
        while (i < chars.length) {
            char c = chars[i];
            if (c == '*') {
                stringBuffer.append('(');
                stringBuffer.append(chars, last, i - last);
                stringBuffer.append(')');
                stringBuffer.append(MULTIPLE_CHAR_PATTERN);
                last = i + 1;
            } else if (c == '?') {
                stringBuffer.append('(');
                stringBuffer.append(chars, last, i - last);
                stringBuffer.append(')');
                stringBuffer.append(SINGLE_CHAR_PATTERN);
                last = i + 1;
            }
            i++;
        }
        stringBuffer.append(chars, last, i - last);
        return stringBuffer.toString();
    }

    /**
     * @see info.magnolia.cms.util.UrlPattern#match(java.lang.String)
     */
    @Override
    public boolean match(String str) {
        return this.pattern.matcher(str).matches();
    }

    /**
     * @see info.magnolia.cms.util.UrlPattern#getLength()
     */
    @Override
    public int getLength() {
        return this.length;
    }

    /**
     * @see info.magnolia.cms.util.UrlPattern#getString()
     */
    @Override
    public String getPatternString() {
        return patternString;
    }

    /**
     * Mainly used by ContentToBean.
     */
    public void setPatternString(String patternString) {
        this.length = removeEnd(patternString, "*").length();
        this.pattern = Pattern.compile(getEncodedString(patternString), Pattern.DOTALL);
        this.patternString = patternString;
    }

    @Override
    public String toString() {
        // don't use pattern.pattern(), but keep the original string.
        // The "compiled" pattern will display the ugly patterns like MULTIPLE_CHAR_PATTERN instead of simple *
        return "SimpleUrlPattern{" + patternString + '}';
    }

}
