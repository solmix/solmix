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

import org.osgi.service.event.EventConstants;
import org.solmix.eventservice.Cache;
import org.solmix.eventservice.TopicFilter;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-1
 */

public class CachedTopicFilter implements TopicFilter
{

    private final Cache<String, String> cache;

    private final char[] keyChars = EventConstants.EVENT_TOPIC.toCharArray();

    private final char[] filterStart;

    public CachedTopicFilter(final Cache<String, String> cache, boolean requireTopic)
    {
        if (null == cache) {
            throw new NullPointerException("Cache may not be null");
        }

        this.cache = cache;

        filterStart = ("(|" + ((requireTopic) ? "" : "(!(" + new String(keyChars) + "=*))") + "("
            + new String(keyChars) + "=\\*)(" + new String(keyChars) + "=").toCharArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.TopicFilter#createFilter()
     */
    @Override
    public String createFilter(String topic) {
        String result = cache.get(topic);

        if (null == result) {
            char[] topicChars = topic.toCharArray();

            final StringBuffer filter = new StringBuffer(topicChars.length * topicChars.length);

            filter.append(filterStart);

            for (int i = 0; i < topicChars.length; i++) {
                if ('/' == topicChars[i]) {
                    filter.append('/').append('\\').append('*').append(')');

                    filter.append('(').append(keyChars).append('=').append(topicChars, 0, i + 1);
                } else {
                    filter.append(topicChars[i]);
                }
            }

            filter.append(')').append(')');

            result = filter.toString();

            cache.add(topic, result);
        }

        return result;
    }

}
