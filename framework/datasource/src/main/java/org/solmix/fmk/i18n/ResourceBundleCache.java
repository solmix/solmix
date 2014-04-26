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

package org.solmix.fmk.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import org.solmix.SlxConstants;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.SimpleFilter;
import org.solmix.fmk.util.MessageTools;

/**
 * The <code>ResourceBundleCache</code> caches resource bundles per OSGI bundle.
 * 
 * @author Administrator
 * @version 110035 2011-3-15
 */
@SuppressWarnings("unchecked")
public class ResourceBundleCache
{

    private Map<String,Object> resourceBundleEntries;

    private final Bundle bundle;

    private final Map<Locale, ResourceBundle> resourceBundles;

    public ResourceBundleCache()
    {
        this(null);
    }

    public ResourceBundleCache(Bundle bundle)
    {
        this.bundle = bundle;
        this.resourceBundles = new HashMap<Locale, ResourceBundle>();
    }

    ResourceBundle getResourceBundle(Locale locale) throws SlxException {
        ResourceBundle rb=null;
        try {
            rb= getResourceBundleInternal(locale==null?MessageTools.DEFAULT_LOCALE:locale);
            if(rb==null)
                rb=getResourceBundleInternal(MessageTools.DEFAULT_LOCALE);
        } catch (IOException e) {e.printStackTrace();
            throw new SlxException(Tmodule.BASIC,Texception.IO_EXCEPTION,"Exception When load properties");
        }
        return rb;
        
    }

    ResourceBundle getResourceBundleInternal(Locale locale) throws IOException {
        if (locale == null) {
            return null;
        }
        synchronized (resourceBundles) {
            ResourceBundle bundle = (ResourceBundle) resourceBundles.get(locale);
            if (bundle != null) {
                return bundle;
            }
        }
        ResourceBundle parent = getResourceBundleInternal(getParentLocale(locale));
        ResourceBundle bundle = loadResourceBundle(parent, locale);
        synchronized (resourceBundles) {
            resourceBundles.put(locale, bundle);
        }
        return bundle;
    }

    /**
     * @param parent
     * @param locale
     * @return
     * @throws IOException 
     */
    private ResourceBundle loadResourceBundle(ResourceBundle parent, Locale locale) throws IOException {
        String path = "_" + locale.toString();
        final Object source =  getResourceBundleEntries().get(path);
        if(source instanceof URL){
            return new PropertyResourceBundle(parent, (URL)source);
        }else if(source instanceof PropertyResourceBundle){
            return (PropertyResourceBundle)source;
        }else if(source instanceof InputStream){
            return new PropertyResourceBundle(parent, (InputStream)source);
        }
        return null;
    }


    /**
     * @return
     * @throws IOException 
     */
    private synchronized Map<String,Object> getResourceBundleEntries() throws IOException {
        Map<String,Object> _resourceBundleEntries = new HashMap<String,Object>();
        int start = 0;
        if (this.resourceBundleEntries == null) {
            String _file = null;
            if (SlxConstants.isOSGI() && bundle != null) {
                _file = (String) bundle.getHeaders().get(Constants.BUNDLE_LOCALIZATION);

            }
            if (_file == null) {
                _file = Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
            }

            if (_file.startsWith("/")) {
                _file = _file.substring(1);
            }
            int slash = _file.lastIndexOf('/');
            String fileName = _file.substring(slash + 1);
            String path = (slash <= 0) ? "/" : _file.substring(0, slash);
            if (SlxConstants.isOSGI() && bundle != null) {
                Enumeration locales = bundle.findEntries(path, fileName + "*.properties", false);
                if (locales != null) {
                    while (locales.hasMoreElements()) {
                        URL entry = (URL) locales.nextElement();
                        String entryPath = entry.getPath();
                            start = 1 + _file.length();
                        final int end = entryPath.length() - 11;
                        entryPath = entryPath.substring(start, end);

                        if (entryPath.length() == 0) {
                            entryPath = "_" + MessageTools.DEFAULT_LOCALE.toString();
                        }

                        if (!_resourceBundleEntries.containsKey(entryPath)) {
                            _resourceBundleEntries.put(entryPath, entry);
                        }
                    }
                }
            
            } else {
                _resourceBundleEntries = getLocalEntries( path, fileName + "*.properties", false);
                
            }
            // END (locales != null)
            this.resourceBundleEntries = _resourceBundleEntries;
        }// END (this.resourceBundleEntries == null)
        return this.resourceBundleEntries;
    }

    /**
     * @param path
     * @param string
     * @param b
     * @return
     * @throws IOException 
     */
    protected Map<String,Object> getLocalEntries(String path, String filePattern, boolean b) throws IOException {
        Object sm = System.getSecurityManager();
        if (sm != null) {
            // TODO deal security
        }if ( !path.startsWith( "/" ) )
            path = "/" + path;
        
        Map<String,Object> _resourceBundleEntries = new HashMap<String,Object>();
       URL _path = getClass().getResource( path );
       String protocol = _path.getProtocol();
       if ("jar".equals(protocol)) {
           String file = _path.getFile();
           //:file:/=5
           file = file.substring(5, file.indexOf('!'));
               JarFile jf = new JarFile(file);
               Enumeration<JarEntry> jenm = jf.entries();
               try {
                while (jenm.hasMoreElements()) {
                       JarEntry entry =  jenm.nextElement();
                       if (entry.isDirectory())
                           continue;
                       String jar = entry.getName();
                       String fileName=null;
                       if(jar.lastIndexOf('/')==-1)
                           fileName=jar;
                       else
                        fileName = jar.substring(jar.lastIndexOf('/')+1,jar.length());
                       if (SimpleFilter.compareSubstring(SimpleFilter.parseSubstring(filePattern), fileName)) {
                           InputStream is = jf.getInputStream(entry);
                           PropertyResourceBundle prb = new PropertyResourceBundle(null,is);
                          _resourceBundleEntries.put(getEntryPath(fileName), prb);
                       }

                   }
            } finally{
                jf.close();
            }
          
       }else{
           try {
            File file = new File(_path.toURI());
           
            if (!file.isDirectory()) {
                String fileName = file.getName();
                _resourceBundleEntries.put(getEntryPath(fileName), file.toURI().toURL().openStream());

            } else {
                int k_file_idx=0;
                File[] files = file.listFiles();
                while (files != null && k_file_idx < files.length ) {
                    if (SimpleFilter.compareSubstring(SimpleFilter.parseSubstring(filePattern), files[k_file_idx].getName())) {
                        String fileName = files[k_file_idx].getName();
                        _resourceBundleEntries.put(getEntryPath(fileName), files[k_file_idx].toURI().toURL().openStream());
                    }
                    k_file_idx++;
                }
            }
        } catch (URISyntaxException e) {
        }
       }
        return _resourceBundleEntries;
    }

    private String getEntryPath(String fileName){
        String entryPath=null;
        if(fileName.indexOf('_')==-1)
            entryPath="_" + MessageTools.DEFAULT_LOCALE.toString();
        else
        entryPath =fileName.substring(fileName.indexOf('_'),fileName.length()-11);
        return entryPath;
    }
    /**
     * @param locale
     * @return
     */
    private Locale getParentLocale(Locale locale) {
        if (locale.getVariant().length() != 0) {
            return new Locale(locale.getLanguage(), locale.getCountry());
        } else if (locale.getCountry().length() != 0) {
            return new Locale(locale.getLanguage(), "");
        }
        // no parent
        return null;
    }
}
