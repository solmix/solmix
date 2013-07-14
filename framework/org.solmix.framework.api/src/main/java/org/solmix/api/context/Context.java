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

package org.solmix.api.context;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.solmix.api.exception.SlxException;

/**
 * 
 * solmix framework context interface.Implementing class should never be accessible directly but only via
 * {@link org.solmix.fmk.context.ContextUtil} static methods which work on a local (Thread) copy of the implementation.
 * 
 * @version 0.1 2012-9-26
 * @since 0.1
 */
public interface Context extends Map
{

    public enum Scope
    {
        /**
         * Attribute visibility scope.
         */
        LOCAL(1) ,
        /**
         * Attribute visibility scope Shared by all requests from this session.
         */
        SESSION(2) ,
        /**
         * Attribute visibility scope, its visible to all sessions of this application.
         */
        SYSTEM(3);

        int value;

        Scope(int i)
        {
            value = i;
        }
    }

    /**
     * @param locale
     */
    public void setLocale(Locale locale);

    /**
     * Get the current locale.
     */
    public Locale getLocale();


    ResourceBundle getResourceBundle()throws SlxException;

    /**
     * Set attribute value, scope of the attribute is defined.
     * 
     * @param name is used as a key
     * @param scope , highest level of scope from which this attribute is visible
     */
    public void setAttribute(String name, Object value, Scope scope);

    /**
     * Get attribute value.
     * 
     * @param name to which value is associated to
     * @param scope the scope (request, session, application)
     * @return attribute value
     */
    public Object getAttribute(String name, Scope scope);

    /**
     * Get attribute value without passing a scope. the scopes are searched from bottom up (request, session,
     * application)
     * 
     * @param name to which value is associated to
     * @return attribute value
     */
    public Object getAttribute(String name);

    /**
     * Get a map of a attributes set in the scope.
     * 
     * @param scope
     * @return the map
     */
    public Map<String, Object> getAttributes(Scope scope);

    /**
     * Remove an attribute.
     * 
     * @param name
     * @param scope
     */
    public void removeAttribute(String name, Scope scope);

    /**
     * Get an over all map.
     * 
     * @return the map
     */
    public Map<String, Object> getAttributes();

    /**
     * Release any resource used by this Context (e.g. jcr sessions).
     */
    public void release();

}
