/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.SystemContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月9日
 */

public class OsgiSqlSessionFactoryProvider extends  AbstractSqlSessionFactoryProvider
{

    public OsgiSqlSessionFactoryProvider(final SystemContext sc)
    {
        super(sc);
    }

    private BundleContext context;
    
    private final Map<Long,String> configedBundle= new HashMap<Long,String>(4);
    
    /**
     * @return the context
     */
    public BundleContext getContext() {
        return context;
    }

    
    /**
     * @param context the context to set
     */
    public void setContext(BundleContext context) {
        this.context = context;
        context.addBundleListener(new BundleListener() {
            
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STARTED) {
//                    addSqlmapsFromBundle(event.getBundle());
                } else if (event.getType() == BundleEvent.STOPPED) {
                   long id= event.getBundle().getBundleId();
                    if(configedBundle.get(id)!=null){
                        removeSqlSessionFactory(configedBundle.get(id));
                    }
//                    removeSqlMapsFromBundle(event.getBundle());
                }
                
            }
        });
    }


    /**
     * @param string
     */
    protected void removeSqlSessionFactory(String string) {
        _tempCache.remove(string);
    }


    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.mybatis.AbstractSqlSessionFactoryProvider#getConfigAsStream(java.lang.String)
     */
    @Override
    protected InputStream getConfigAsStream(String environment,String configLocation)
        throws IOException {
        Assert.isNotNull(context,"The Osgi BundleContext must be not null.");
        Bundle[] bundles=  context.getBundles();
        for(Bundle bundle:bundles){
          URL url=  bundle.getEntry(configLocation);
          if(url!=null){
              configedBundle.put(bundle.getBundleId(), environment);
              return url.openStream();
          }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     * @throws IOException 
     * 
     * @see org.solmix.mybatis.AbstractSqlSessionFactoryProvider#getMapperResources(java.lang.String)
     */
    @Override
    protected Map<String, InputStream> getMapperResources(String environment,
        String filter) throws IOException {
        Assert.isNotNull(context, "The Osgi BundleContext must be not null.");
        String path, pattern;
        if (filter.indexOf(":") != -1) {
            int prefixEnd = filter.indexOf(":") + 1;
            filter = filter.substring(prefixEnd);
        }
        if (filter.indexOf("/") != -1) {
            int last = filter.lastIndexOf("/");
            path = filter.substring(filter.lastIndexOf("/"));
            pattern = filter.substring(last, filter.length());
            if (path.indexOf("**") != -1) {
                path = filter.substring(filter.lastIndexOf("**"));
            }
        } else {
            path = "/";
            pattern = filter;
        }
        Bundle[] bundles = context.getBundles();
        Map<String, InputStream> _return = new HashMap<String, InputStream>();
        for (Bundle bundle : bundles) {
            Enumeration<URL> urls= bundle.findEntries(path, pattern, true);
            if(urls!=null)
            while(urls.hasMoreElements()){
                if(configedBundle.get(bundle.getBundleId())==null)
                    configedBundle.put(bundle.getBundleId(), environment);
               URL url= urls.nextElement();
                _return.put(url.toString(), url.openStream());
            }
        }
        return _return;
    }

}
