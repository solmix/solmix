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
package org.solmix.fmk.context.blueprint;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.solmix.api.bean.BeanConfigurer;
import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.fmk.cm.osgi.OsgiConfigureUnitManager;
import org.solmix.fmk.context.ext.SolmixSystemContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-5
 */

public class BlueprintSystemContext extends SolmixSystemContext
{
    BundleContext bundleContext;
    BlueprintContainer blueprintContainer;
    
    /**
     * @param bundleContext the bundleContext to set
     */
    public void setBundleContext(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        ClassLoader bundleClassLoader =
            AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return new BundleDelegatingClassLoader(bundleContext.getBundle(), 
                                                           this.getClass().getClassLoader());
                }
            });
        super.setBean(bundleClassLoader, ClassLoader.class);
        super.setBean(bundleContext, BundleContext.class);
        super.setBean(new OsgiConfigureUnitManager(bundleContext), ConfigureUnitManager.class);
    }
    
    /**
     * @param blueprintContainer the blueprintContainer to set
     */
    public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
        this.blueprintContainer = blueprintContainer;
        setBean(new BlueprintConfigurer(blueprintContainer), BeanConfigurer.class);
        setBean(new BlueprintBeanProvider(getBean(ConfiguredBeanProvider.class), blueprintContainer, bundleContext),
            ConfiguredBeanProvider.class);
        
    }
    @Override
    public String getId() {
        if (id == null) {
            id = bundleContext.getBundle().getSymbolicName() + "-" 
                + DEFAULT_CONTEXT_ID + Integer.toString(this.hashCode());
        }
        return id;
    }
}