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

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-16
 */

public class CombinedResourceBundle extends ResourceBundle
{

   private final ResourceBundle resourceBundle;

   private final ResourceBundle defaultResourceBundle;

   private final Locale locale;

   /**
    * @param bundleResourceBundle
    * @param defaultResourceBundle
    * @param locale
    */
   public CombinedResourceBundle(ResourceBundle resourceBundle, ResourceBundle defaultResourceBundle, final Locale locale)
   {
      this.resourceBundle = resourceBundle;
      this.defaultResourceBundle = defaultResourceBundle;
      this.locale = locale;
   }

   public Locale getLocale()
   {
      return locale;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.ResourceBundle#getKeys()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Enumeration<String> getKeys()
   {
      return new CombinedEnumeration(resourceBundle.getKeys(), defaultResourceBundle.getKeys());
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
    */
   @Override
   protected Object handleGetObject(String key)
   {
      // check primary resource bundle first
      try
      {
         return resourceBundle.getObject(key);
      } catch (MissingResourceException mre)
      {
         // ignore
      }

      // now check the default resource bundle
      try
      {
         return defaultResourceBundle.getObject(key);
      } catch (MissingResourceException mre)
      {
         // ignore
      }

      // finally fall back to using the key
      return key;
   }

}
