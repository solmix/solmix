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

package org.solmix.eventservice;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-1
 */

public interface EventFilter
{

    Filter TRUE_FILTER = new Filter() {

        /**
         * This is a null object that always returns <tt>true</tt>.
         * 
         * @param reference An unused service reference
         * @return <tt>true</tt>
         */
        public boolean match(final ServiceReference/* <?> */reference) {
            return true;
        }

        /**
         * This is a null object that always returns <tt>true</tt>.
         * 
         * @param dictionary An unused dictionary
         * @return <tt>true</tt>
         */
        public boolean match(final Dictionary<String, ?> dictionary) {
            return true;
        }

        /**
         * This is a null object that always returns <tt>true</tt>.
         * 
         * @param dictionary An unused dictionary.
         * @return <tt>true</tt>
         */
        public boolean matchCase(final Dictionary/* <String,?> */dictionary) {
            return true;
        }

        /**
         * This is a null object that always returns <tt>true</tt>.
         * 
         * @param dictionary An unused dictionary.
         * @return <tt>true</tt>
         */
        public boolean matches(Map<String, ?> map) {
            return true;
        }
    };

    /**
     * @param filter
     * @return
     * @throws InvalidSyntaxException
     */
    Filter createFilter(String filter) throws InvalidSyntaxException;
}
