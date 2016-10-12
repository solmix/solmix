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
 * Provides a point of extensibility in the paging behavior.
 * If you supply a PagerProcessor when you get a Pager,
 * then that processor will be called to process each element
 * as the pager moves it from the source collection to the
 * destination collection.
 */
public interface PagerProcessorExt extends PagerProcessor {

    /**
     * Get the event handler for this pager. May return null to indicate
     * that no event handler should be used.
     */
    public PagerEventHandler getEventHandler ();

    /**
     * Determines if null values are included in the Pager's results.
     * @return If this method returns true, then when the processElement
     * method returns null, that element will not be included in the results.
     * If this methods returns false, then nulls may be added to the result
     * page.
     */
    public boolean skipNulls ();

    /**
     * Process an element as the pager moves it from the source 
     * collection to the destination collection. This version
     * allows an additional argument to be passed along.
     * @param o1 The object to process.
     * @param o2 Additional data required to processElement.
     * @return The processed object.
     */
    public Object processElement ( Object o1, Object o2 );

}
