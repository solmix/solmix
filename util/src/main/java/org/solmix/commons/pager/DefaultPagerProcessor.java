/*
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
package org.solmix.commons.pager;

/**
 * The default page processor does not process any elements
 * that you're paging.  This is useful if you're only looking
 * to page through an existing collection, and you don't need
 * to perform any transformations on the elements that are
 * found to belong in the resultant page.
 */
public class DefaultPagerProcessor implements PagerProcessor {

    /**
     * Default processor does not process anything, it just
     * returns what was passed in.
     * @param o The object to process.
     * @return The same (completely unmodified) object that was passed in.
     * @see PagerProcessor#processElement
     */
    @Override
    public Object processElement ( Object o ) { return o; }
}
