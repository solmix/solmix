/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.commons.util;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-10
 */

public interface UrlPattern extends Serializable
{

    /**
     * Does the patter match the given url?
     * 
     * @param url url to match
     * @return <code>true</code> if the given URL matches the pattern
     */
    boolean match(String url);

    /**
     * Returns the pattern length. Longer patterns have higher priority.
     * 
     * @return pattern length
     */
    int getLength();

    /**
     * Returns the pattern string.
     * 
     * @return pattern string
     */
    String getPatternString();

    /**
     * A pattern which matches any input.
     */
    UrlPattern MATCH_ALL = new MatchAllPattern();

    /**
     * A default implementation with matches any input.
     */
    public static final class MatchAllPattern implements UrlPattern
    {

        /**
         * Stable serialVersionUID.
         */
        private static final long serialVersionUID = 222L;

        /**
         * Instantiates a new MatchAllPattern instance. Use the MATCH_ALL constant and don't create new instances.
         */
        protected MatchAllPattern()
        {
            // protected contructor
        }

        /**
         * @see org.solmix.commons.util.magnolia.cms.util.UrlPattern#match(java.lang.String)
         */
        @Override
        public boolean match(String str) {
            return true;
        }

        /**
         * @see org.solmix.commons.util.magnolia.cms.util.UrlPattern#getLength()
         */
        @Override
        public int getLength() {
            return 1;
        }

        /**
         * @see org.solmix.commons.util.magnolia.cms.util.UrlPattern#getString()
         */
        @Override
        public String getPatternString() {
            return "";
        }
    }
}
