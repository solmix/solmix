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

import org.osgi.service.event.TopicPermission;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-2
 */

public class PermissionsUtil
{
    /**
     * Creates a <tt>TopicPermission</tt> for the given topic and the type PUBLISH
     * Note that a
     * <tt>java.lang.Object</tt> is returned in case creating a new TopicPermission
     * fails. This assumes that Bundle.hasPermission is used in order to evaluate the
     * created Permission which in turn will return true if security is not supported
     * by the framework. Otherwise, it will return false due to receiving something
     * that is not a subclass of <tt>java.lang.SecurityPermission</tt> hence, this
     * combination ensures that access is granted in case a topic permission could
     * not be created due to missing security support by the framework.
     *
     * @param topic The target topic
     *
     * @return The created permission or a <tt>java.lang.Object</tt> in case the
     *      permission could not be created.
     *
     * @see org.osgi.service.event.TopicPermission
     */
    public static Object createPublishPermission(final String topic)
    {
        Object result;
        try
        {
            result = new org.osgi.service.event.TopicPermission(topic, TopicPermission.PUBLISH);
        } catch (Throwable t)
        {
            // This might happen in case security is not supported
            // Bundle.hasPermission will return true in this case
            // hence topicPermission = new Object() is o.k.

            result = new Object();
        }
        return result;
    }

    /**
     * Creates a <tt>TopicPermission</tt> for the given topic and the type SUBSCRIBE
     * Note that a
     * <tt>java.lang.Object</tt> is returned in case creating a new TopicPermission
     * fails. This assumes that Bundle.hasPermission is used in order to evaluate the
     * created Permission which in turn will return true if security is not supported
     * by the framework. Otherwise, it will return false due to receiving something
     * that is not a subclass of <tt>java.lang.SecurityPermission</tt> hence, this
     * combination ensures that access is granted in case a topic permission could
     * not be created due to missing security support by the framework.
     *
     * @param topic The target topic
     *
     * @return The created permission or a <tt>java.lang.Object</tt> in case the
     *      permission could not be created.
     *
     * @see org.osgi.service.event.TopicPermission
     */
    public static Object createSubscribePermission(final String topic)
    {
        Object result;
        try
        {
            result = new org.osgi.service.event.TopicPermission(topic, TopicPermission.SUBSCRIBE);
        } catch (Throwable t)
        {
            // This might happen in case security is not supported
            // Bundle.hasPermission will return true in this case
            // hence topicPermission = new Object() is o.k.

            result = new Object();
        }
        return result;
    }
}
