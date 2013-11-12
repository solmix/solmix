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

package org.solmix.web.internal;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.Filter;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-9
 */

public class FilterChainHelper
{

    SortedSet<FilterListEntry> filterList;

    Filter[] filters;

    FilterChainHelper()
    {
    }

    public synchronized Filter addFilter(Filter filter, Long filterId, int order) {
        filters = null;
        if (filterList == null) {
            filterList = new TreeSet<FilterListEntry>();
        }
        filterList.add(new FilterListEntry(filter, filterId, order));
        return filter;
    }

    public synchronized Filter[] removeAllFilters() {
        // will be returned after cleaning the lists
        Filter[] removedFilters = getFilters();

        filters = null;
        filterList = null;

        return removedFilters;
    }

    public synchronized Filter removeFilter(Filter filter) {
        if (filterList != null) {
            filters = null;
            for (Iterator<FilterListEntry> fi = filterList.iterator(); fi.hasNext();) {
                FilterListEntry test = fi.next();
                if (test.getFilter().equals(filter)) {
                    fi.remove();
                    return test.getFilter();
                }
            }
        }

        // no removed ComponentFilter
        return null;
    }

    public synchronized boolean removeFilterById(Object filterId) {
        if (filterList != null) {
            filters = null;
            for (Iterator<FilterListEntry> fi = filterList.iterator(); fi.hasNext();) {
                FilterListEntry test = fi.next();
                if (test.getFitlerId() == filterId || (test.getFitlerId() != null && test.getFitlerId().equals(filterId))) {
                    fi.remove();
                    return true;
                }
            }
        }

        // no removed ComponentFilter
        return false;
    }

    /**
     * Returns the list of <code>Filter</code>s added to this instance or <code>null</code> if no filters have been
     * added.
     */
    public synchronized Filter[] getFilters() {
        if (filters == null) {
            if (filterList != null && !filterList.isEmpty()) {
                Filter[] tmp = new Filter[filterList.size()];
                int i = 0;
                for (FilterListEntry entry : filterList) {
                    tmp[i] = entry.getFilter();
                    i++;
                }
                filters = tmp;
            }
        }
        return filters;
    }

    /**
     * Returns the list of <code>FilterListEntry</code>s added to this instance or <code>null</code> if no filters have
     * been added.
     */
    public synchronized FilterListEntry[] getFilterListEntries() {
        FilterListEntry[] result = null;
        if (filterList != null && !filterList.isEmpty()) {
            result = new FilterListEntry[filterList.size()];
            filterList.toArray(result);
        }
        return result;
    }

    public static class FilterListEntry implements Comparable<FilterListEntry>
    {

        private final Filter filter;

        private final Long filterId;

        private final int order;

        FilterListEntry(Filter filter, Long filterId, int order)
        {
            this.filter = filter;
            this.filterId = filterId;
            this.order = order;
        }

        public Filter getFilter() {
            return filter;
        }

        public Long getFitlerId() {
            return filterId;
        }

        public int getOrder() {
            return order;
        }

        /**
         * Note: this class has a natural ordering that is inconsistent with equals.
         */
        public int compareTo(FilterListEntry other) {
            if (this == other || equals(other)) {
                return 0;
            }

            if (order < other.order) {
                return -1;
            } else if (order > other.order) {
                return 1;
            }

            // if the filterId is comparable and the other is of the same class
            if (filterId != null && other.filterId != null) {
                int comp = filterId.compareTo(other.filterId);
                if (comp != 0) {
                    return comp;
                }
            }

            // this is inserted, obj is existing key
            return 1; // insert after current key
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof FilterListEntry) {
                FilterListEntry other = (FilterListEntry) obj;
                return getFilter().equals(other.getFilter());
            }

            return false;
        }
    }
}
