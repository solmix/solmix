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
package org.solmix.eventservice.filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.solmix.eventservice.Cache;
import org.solmix.eventservice.EventFilter;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-1
 */

public class CacheEventFilter implements EventFilter
{
    private Cache<String,Filter> cache;
    private BundleContext context;
    public CacheEventFilter(final Cache<String,Filter> cache, final BundleContext context)
    {
        if(null == cache)
        {
            throw new NullPointerException("Cache may not be null");
        }

        if(null == context)
        {
            throw new NullPointerException("Context may not be null");
        }

        this.cache = cache;

        this.context = context;
    }

    /**
     * Create a filter for the given filter string or return the TRUE_FILTER in case
     * the string is <tt>null</tt>.
     *
     * @param filter The filter as a string
     * @return The <tt>Filter</tt> of the filter string or the TRUE_FILTER if the
     *      filter string was <tt>null</tt>
     * @throws InvalidSyntaxException if <tt>BundleContext.createFilter()</tt>
     *      throws an <tt>InvalidSyntaxException</tt>
     *
     * @see org.apache.felix.eventadmin.impl.handler.Filters#createFilter(java.lang.String)
     */
    @Override
    public Filter createFilter(String filter)
        throws InvalidSyntaxException
    {
        Filter result = (Filter) ((null != filter) ? cache.get(filter)
            : TRUE_FILTER);

        if (null == result)
        {
            result = context.createFilter(filter);

            cache.add(filter, result);
        }

        return result;
    }
}
