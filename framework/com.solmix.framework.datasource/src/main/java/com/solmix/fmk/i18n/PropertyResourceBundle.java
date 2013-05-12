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

package com.solmix.fmk.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 
 * @author Administrator
 * @version 110035 2011-3-15
 */

public class PropertyResourceBundle extends ResourceBundle
{

   private final Properties props;

   PropertyResourceBundle( final ResourceBundle parent, final URL source ) throws IOException
   {
     this(parent, source.openStream());
        
   }
   PropertyResourceBundle(final ResourceBundle parent,InputStream is) throws IOException{
       if ( parent != null )
           setParent( parent );
       props = new Properties();
       try
       {
       props.load( is );
       } finally
       {
           if(is!=null)
          is.close();
       }
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.ResourceBundle#getKeys()
    */
   @Override
   public Enumeration getKeys()
   {
      return new CombinedEnumeration( props.keys(), parent.getKeys() );
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
    */
   @Override
   protected Object handleGetObject( String key )
   {
      return props.get( key );
   }

}
