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
package org.solmix.runtime.i18n.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.i18n.ResourceBundleManager;


/**
 * <b>if in osgi environment:</b>
 * <p>
 * The ResourceBundleManager manages resource bundle instance per Bundle. It contains a local cache,for bundles,but when
 * a bundle is being uninsatalled, its resources stored in the cache are cleaned up.
 * <p>
 * <b>if not:</b>
 * <p>
 * use the normal java ResourceBundle.
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月19日
 */

public class DefaultResourceBundleManager implements ResourceBundleManager
{

    private static final Logger log = LoggerFactory.getLogger(DefaultResourceBundleManager.class.getName());

    private final BundleContext bundleContext;

    private final Map<Object, ResourceBundleCache> resourceBundleCaches;

    private final ResourceBundleCache defaultCache;

    private String defaultLocale;

    private Container container;

    public DefaultResourceBundleManager(final Container sc)
    {
        this(sc, null);

    }
    public DefaultResourceBundleManager(final Container sc, final BundleContext bundleContext)
    {
        setContainer(sc);
        if (bundleContext == null && sc != null) {
            BundleContext scBundleContext = sc.getExtension(BundleContext.class);
            this.bundleContext = scBundleContext;
        } else {
            this.bundleContext = bundleContext;
        }
        resourceBundleCaches = new HashMap<Object, ResourceBundleCache>();
        if (this.bundleContext != null) {
            this.defaultCache = new ResourceBundleCache(this.bundleContext.getBundle());
            this.bundleContext.addBundleListener(this);
        } else {
            this.defaultCache = new ResourceBundleCache();
        }
    }

    public void setContainer(final Container sc) {
        this.container = sc;
        if (container != null) {
            container.setExtension(this, ResourceBundleManager.class);
        }
    }

   
    /**
     * @param defaultLocale the defaultLocale to set
     */
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public final void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTED) {
            Long key = new Long(event.getBundle().getBundleId());
            synchronized (resourceBundleCaches) {
                resourceBundleCaches.remove(key);
            }
        }

    }

    @Override
    public void dispose() {
        if (bundleContext != null)
            bundleContext.removeBundleListener(this);
        else
            log.warn("cannot use diapose() method with no osgi environment.");

    }

    @Override
    public ResourceBundle getResourceBundle(Bundle provider, Locale locale)  {
        final ResourceBundle defaultResourceBundle = defaultCache.getResourceBundle(locale);
        if (provider == null || provider.equals(bundleContext.getBundle())) {
            return defaultResourceBundle;
        }
        ResourceBundleCache cache;
        synchronized (resourceBundleCaches) {
            Long key = new Long(provider.getBundleId());
            cache = resourceBundleCaches.get(key);
            if (cache == null) {
                cache = new ResourceBundleCache(provider);
                resourceBundleCaches.put(key, cache);
            }
        }
        final ResourceBundle bundleResourceBundle = cache.getResourceBundle(locale);
        return new CombinedResourceBundle(bundleResourceBundle, defaultResourceBundle, locale);
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale){
        return getResourceBundle(null, locale);
    }

    @Override
    public Locale getDefaultLocale() {
        Locale _return;
        if (defaultLocale == null)
            _return = Locale.getDefault();
        else
            _return = new Locale(defaultLocale);
        return _return;
    }

}
