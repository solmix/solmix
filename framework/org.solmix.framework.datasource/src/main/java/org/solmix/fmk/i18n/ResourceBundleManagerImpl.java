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

package org.solmix.fmk.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.i18n.ResourceBundleManager;

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
public class ResourceBundleManagerImpl implements ResourceBundleManager
{

    private static final Logger log = LoggerFactory.getLogger(ResourceBundleManagerImpl.class.getName());

    private final BundleContext bundleContext;

    private final Map<Object, ResourceBundleCache> resourceBundleCaches;

    private final ResourceBundleCache defaultCache;

    private String defaultLocale;

    private SystemContext sc;

    public ResourceBundleManagerImpl(SystemContext sc)
    {
        this(sc, null);

    }

    @Resource
    public void setSystemContext(SystemContext sc) {
        this.sc = sc;
        if (sc != null) {
            sc.setBean(this, ResourceBundleManager.class);
        }
    }

    public ResourceBundleManagerImpl(SystemContext sc, final BundleContext bundleContext)
    {
        setSystemContext(sc);
        if (bundleContext == null && sc != null) {
            BundleContext scBundleContext = sc.getBean(BundleContext.class);
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

    /**
     * @param defaultLocale the defaultLocale to set
     */
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
     */
    @Override
    public final void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTED) {
            Long key = new Long(event.getBundle().getBundleId());
            synchronized (resourceBundleCaches) {
                resourceBundleCaches.remove(key);
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.i18n.ResourceBundleManager#dispose()
     */
    @Override
    public void dispose() {
        if (bundleContext != null)
            bundleContext.removeBundleListener(this);
        else
            log.warn("cannot use diapose() method with no osgi environment.");

    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see org.solmix.api.i18n.ResourceBundleManager#getResourceBundle(org.osgi.framework.Bundle, java.util.Locale)
     */
    @Override
    public ResourceBundle getResourceBundle(Bundle provider, Locale locale) throws SlxException {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.i18n.ResourceBundleManager#getResourceBundle(java.util.Locale)
     */
    @Override
    public ResourceBundle getResourceBundle(Locale locale) throws SlxException {
        return getResourceBundle(null, locale);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.i18n.ResourceBundleManager#getDefaultLocale()
     */
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
