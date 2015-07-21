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

package org.solmix.runtime.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleListener;

/**
 * <b>if in osgi environment:</b>
 * <p>
 * The ResourceBundleManager manages resource bundle instance per Bundle. It contains a local cache,for bundles,but when
 * a bundle is being uninsatalled, its resources stored in the cache are cleaned up.
 * <p>
 * <b>if not:</b>
 * <p>
 * use the normal java ResourceBundle.
 * 
 * @author Administrator
 * @version 110035 2011-3-15
 */

public interface ResourceBundleManager extends BundleListener
{

    String BYTE_ENCODING = "UTF-8";

    Locale DEFAULT_LOCALE = Locale.ENGLISH;

    ResourceBundle getResourceBundle(Bundle provider, Locale locale);

    ResourceBundle getResourceBundle(Locale locale);

    void dispose();

    Locale getDefaultLocale();

}
