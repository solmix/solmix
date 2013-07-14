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
package org.solmix.eventservice.security;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-2
 */

public class SecureEventAdminFactory implements ServiceFactory
{

    // The EventAdmin to secure
    private final EventAdmin m_admin;

    /**
     * The constructor of the factory. The factory will use the given event admin and
     * permission factory to create a new <tt>EventAdminSecurityDecorator</tt>
     * on any call to <tt>getService()</tt>.
     *
     * @param admin The <tt>EventAdmin</tt> service to secure.
     */
    public SecureEventAdminFactory(final EventAdmin admin)
    {
        checkNull(admin, "Admin");

        m_admin = admin;
    }

    /**
     * Returns a new <tt>EventAdminSecurityDecorator</tt> initialized with the
     * given <tt>EventAdmin</tt>. That in turn will check any call to post or
     * send for the appropriate permissions based on the bundle parameter.
     *
     * @param bundle The bundle used to determine the permissions of the caller
     * @param registration The ServiceRegistration that is not used
     *
     * @return The given service instance wrapped by an <tt>EventAdminSecuriryDecorator</tt>
     *
     * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle,
     *      org.osgi.framework.ServiceRegistration)
     */
    public Object getService(final Bundle bundle,
        final ServiceRegistration registration)
    {
        // We don't need to cache this objects since the framework already does this.
        return new EventAdminSecurityDecorator(bundle, m_admin);
    }

    /**
     * This method doesn't do anything at the moment.
     *
     * @param bundle The bundle object that is not used
     * @param registration The ServiceRegistration that is not used
     * @param service The service object that is not used
     *
     * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle,
     *      org.osgi.framework.ServiceRegistration, java.lang.Object)
     */
    public void ungetService(final Bundle bundle,
        final ServiceRegistration registration, final Object service)
    {
        // We don't need to do anything here since we hand-out a new instance with
        // any call to getService hence, it is o.k. to just wait for the next gc.
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
