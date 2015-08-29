/**
 * Copyright (c) 2014 The Solmix Project
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
package org.solmix.runtime.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月27日
 */

public class IdentityBundleListener implements SynchronousBundleListener {

    private final long id;
    
    public IdentityBundleListener(long bundleId) {
        this.id = bundleId;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        // TODO Auto-generated method stub

    }
    /**
     * @param context
     */
    public void regiterExistingNamespce(BundleContext context) {
        // TODO Auto-generated method stub
        
    }
   
    public void close() {
        
    }

}
