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
package com.solmix.eventservice.security;

import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-2
 */

public class EventAdminSecurityDecorator implements EventAdmin
{
    // The bundle used to determine appropriate permissions
    private final Bundle f_bundle;

    // The decorated service instance
    private final EventAdmin f_eventAdmin;

    /**
     * The constructor of this decorator. The given bundle and permission factory
     * will be used to determine appropriate permissions for any call to
     * <tt>postEvent()</tt> or <tt>sendEvent()</tt>, respectively. This method then
     * in turn throw a <tt>SecurityException</tt> in case the given bundle doesn't
     * pass the check.
     *
     * @param bundle The calling bundle used to determine appropriate permissions
     * @param admin The decorated service instance
     */
    public EventAdminSecurityDecorator(final Bundle bundle, final EventAdmin admin)
    {
        checkNull(bundle, "Bundle");
        checkNull(admin, "Admin");

        f_bundle = bundle;

        f_eventAdmin = admin;
    }

    /**
     * This method checks whether the given (i.e., calling) bundle has
     * appropriate permissions to post an event to the targeted topic. A
     * <tt>SecurityException</tt> is thrown in case it has not. Otherwise, the
     * event is posted using this decorator's service instance.
     *
     * @param event The event that should be posted
     *
     * @see org.osgi.service.event.EventAdmin#postEvent(org.osgi.service.event.Event)
     */
    public void postEvent(final Event event)
    {
        checkPermission(event.getTopic());

        f_eventAdmin.postEvent(event);
    }

    /**
     * This method checks whether the given (i.e., calling) bundle has
     * appropriate permissions to send an event to the targeted topic. A
     * <tt>SecurityException</tt> is thrown in case it has not. Otherwise,
     * the event is posted using this decorator's service instance.
     *
     * @param event The event that should be send
     *
     * @see org.osgi.service.event.EventAdmin#sendEvent(org.osgi.service.event.Event)
     */
    public void sendEvent(final Event event)
    {
        checkPermission(event.getTopic());

        f_eventAdmin.sendEvent(event);
    }

    /**
     * Overrides <tt>hashCode()</tt> and returns the hash code of the decorated
     * service instance.
     *
     * @return The hash code of the decorated service instance
     *
     * @see java.lang.Object#hashCode()
     * @see org.osgi.service.event.EventAdmin
     */
    public int hashCode()
    {
        return f_eventAdmin.hashCode();
    }

    /**
     * Overrides <tt>equals()</tt> and delegates the call to the decorated service
     * instance. In case that o is an instance of this class it passes o's service
     * instance instead of o.
     *
     * @param o The object to compare with this decorator's service instance
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @see org.osgi.service.event.EventAdmin
     */
    public boolean equals(final Object o)
    {
        if(o instanceof EventAdminSecurityDecorator)
        {
            return f_eventAdmin.equals(((EventAdminSecurityDecorator) o).f_eventAdmin);
        }

        return f_eventAdmin.equals(o);
    }

    /*
     * This is a utility method that will throw a <tt>SecurityExcepiton</tt> in case
     * that the given bundle (i.e, the caller) has not appropriate permissions to
     * publish to this topic. This method uses Bundle.hasPermission() and the given
     * permission factory to determine this.
     */
    private void checkPermission(final String topic)
    {
        if(!f_bundle.hasPermission(PermissionsUtil.createPublishPermission(topic)))
        {
            throw new SecurityException("Bundle[" + f_bundle +
                "] has no PUBLISH permission for topic [" + topic + "]");
        }
    }

    /*
     * This is a utility method that will throw a <tt>NullPointerException</tt>
     * in case that the given object is null. The message will be of the form name +
     * may not be null.
     */
    private void checkNull(final Object object, final String name)
    {
        if(null == object)
        {
            throw new NullPointerException(name + " may not be null");
        }
    }
}
