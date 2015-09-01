/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.commons.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月4日
 */

public final class SystemPropertyAction implements PrivilegedAction<String>
{

    private static final Logger LOG = LoggerFactory.getLogger(SystemPropertyAction.class);

    private final String property;

    private final String def;

    private SystemPropertyAction(String name)
    {
        property = name;
        def = null;
    }

    private SystemPropertyAction(String name, String d)
    {
        property = name;
        def = d;
    }

    @Override
    public String run() {
        if (def != null) {
            return System.getProperty(property, def);
        }
        return System.getProperty(property);
    }

    public static String getProperty(String name) {
        return AccessController.doPrivileged(new SystemPropertyAction(name));
    }

    public static String getProperty(String name, String def) {
        try {
            return AccessController.doPrivileged(new SystemPropertyAction(name, def));
        } catch (SecurityException ex) {
            LOG.warn("SecurityException raised getting property " + name, ex);
            return def;
        }
    }

    public static String getPropertyOrNull(String name) {
        try {
            return AccessController.doPrivileged(new SystemPropertyAction(name));
        } catch (SecurityException ex) {
            LOG.warn("SecurityException raised getting property " + name, ex);
            return null;
        }
    }
}
