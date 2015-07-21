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

package org.solmix.runtime.i18n.support;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.solmix.commons.util.SimpleFilter;
import org.solmix.runtime.i18n.I18nException;

/**
 * 
 * @author Administrator
 * @version 110035 2011-3-15
 */

public class FilterEnumeration implements Enumeration<Object>
{

   private final URL k_path;

   private final List<String> k_filePattern;

   private boolean k_over = false;

   private int k_file_idx = 0;

   private final List<Object> k_nextEntries = new ArrayList<Object>(2);

   public FilterEnumeration(String path, String filePattern)
   {
      if (path == null)
      {
         throw new IllegalArgumentException("the path of filter entries cannot be null");
      }
      if (path.length() > 0 && (path.toUpperCase().startsWith("CLASSPATH:")))
      {
         path = path.substring(10);
         if ( !path.startsWith( "/" ) )
            path = "/" + path;
      }
      k_path = getClass().getResource( path );
      filePattern = (filePattern == null) ? "*" : filePattern;
      k_filePattern = SimpleFilter.parseSubstring(filePattern);
      findNext();
   }
   /**
    * 
    */
   private void findNext(){
       
        // protocol may be http, https, ftp, file, and jar
        String protocol = k_path.getProtocol();
        if ("jar".equals(protocol)) {
            String file = k_path.getFile();
            //:file:/=5
            file = file.substring(5, file.indexOf('!'));
            try {
                JarFile jf = new JarFile(file);
                Enumeration<JarEntry> jenm = jf.entries();
                while (jenm.hasMoreElements()) {
                    JarEntry entry = jenm.nextElement();
                    if (entry.isDirectory())
                        continue;
                    String jar = entry.getName();
                    if (SimpleFilter.compareSubstring(k_filePattern, jar)) {
                        k_nextEntries.add(jf.getInputStream(entry));
                    }

                }
            } catch (IOException e) {
               throw new I18nException(e);
            }

        } else {
            File file = null;
            try {
                file = new File(k_path.toURI());
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
            if (!k_over) {
                try {
                    if (file != null && file.exists())
                        if (!file.isDirectory()) {
                            k_over = true;
                            k_nextEntries.add(file.toURI().toURL());

                        } else {
                            File[] files = file.listFiles();
                            while (files != null && k_file_idx < files.length && k_nextEntries.size() == 0) {
                                if (SimpleFilter.compareSubstring(k_filePattern, files[k_file_idx].getName())) {
                                    k_nextEntries.add(files[k_file_idx].toURI().toURL());
                                }
                                k_file_idx++;
                            }
                        }
                } catch (MalformedURLException e) {
                    // igonore.
                }
            }
        }

    }

   @Override
   public boolean hasMoreElements()
   {
      return (k_nextEntries.size() != 0);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.Enumeration#nextElement()
    */
   @Override
   public Object nextElement()
   {
      if (k_nextEntries.size() == 0)
      {
         throw new NoSuchElementException("No more entries.");
      }
      Object last = k_nextEntries.remove(0);
      findNext();
      return last;
   }

}
