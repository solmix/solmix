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

package org.solmix.fmk.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-16
 */

public class MessageTools
{

    public static final String BYTE_ENCODING = "UTF-8";

    protected ResourceBundle bundle;


    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final Logger log = LoggerFactory.getLogger(MessageTools.class.getName());

    private final Map<Long, Properties> propsCache = new HashMap<Long, Properties>();

    private final Map<Long, Date> previousDates = new HashMap<Long, Date>();

    private final Set<Long> docsToRefresh = new HashSet<Long>();


    /**
     * @param bundle
     * @param virtualContext
     */
    public MessageTools(ResourceBundle bundlet)
    {
        this.bundle = bundle;
    }

    public String get(String key) {
        String translation = getTranslation(key);
        if (translation == null) {
            try {
                translation = this.bundle.getString(key);
            } catch (Exception e) {
                if (log.isWarnEnabled())
                    log.warn("can not finger out resource bundle for key:[" + key + "] used default value:[" + key + "]");
                translation = key;
            }
        }
        return translation;
    }

    public String get(String key, Object[] params) {
        String translation = get(key);
        if (params != null) {
            translation = MessageFormat.format(translation, params);
        }
        return translation;
    }

    protected String getTranslation(String key) {
        String returnValue = null;
        // returnValue = propsCache.get(key);
        // TODO
        return returnValue;
    }
}
