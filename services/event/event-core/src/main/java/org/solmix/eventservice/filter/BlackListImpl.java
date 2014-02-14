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

package org.solmix.eventservice.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.ServiceReference;
import org.solmix.eventservice.BlackList;

/**
 * This class implements a <tt>BlackList</tt> that removes references to unregistered services automatically.
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-1
 */

public class BlackListImpl implements BlackList
{

    private final Set<ServiceReference<?>> blankList = Collections.synchronizedSet(new HashSet<ServiceReference<?>>() {

        public boolean contains(final Object object) {
            for (Iterator<ServiceReference<?>> iter = super.iterator(); iter.hasNext();) {
                final ServiceReference<?> ref =  iter.next();
                if (null == ref.getBundle()) {
                    iter.remove();
                }
            }

            return super.contains(object);
        }
    });

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.BlankList#add(org.osgi.framework.ServiceReference)
     */
    @Override
    public void add(ServiceReference<?> ref) {
        blankList.add(ref);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.BlankList#contains(org.osgi.framework.ServiceReference)
     */
    @Override
    public boolean contains(ServiceReference<?> ref) {
        return blankList.contains(ref);
    }

}
