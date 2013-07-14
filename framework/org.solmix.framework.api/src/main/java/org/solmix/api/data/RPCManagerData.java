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

package org.solmix.api.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2011-1-1 solmix-api
 */
@SuppressWarnings("unchecked")
public class RPCManagerData implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 6222742856162147931L;

   private Boolean responseIsCustom;


   private Boolean omitNullMapValuesInResponse;

   private Boolean isDownload;

   private String charset;

   private Boolean closeConnection;

   private Boolean prettyPrintResponse;

   private Boolean isExport;

   private Boolean enableAllDS;

   private String jsCallback;

   /**
    * @return the jsCallback
    */
   public String getJsCallback()
   {
      return jsCallback;
   }

   /**
    * @param jsCallback the jsCallback to set
    */
   public void setJsCallback( String jsCallback )
   {
      this.jsCallback = jsCallback;
   }

   private Boolean rest;

   private String customHTML;

   private String serverVersion;

   private String defaultDBName;

   private Map< Object ,Object > customerConfig;

   /**
    * @return the responseIsCustom
    */
   public Boolean getResponseIsCustom()
   {
      return responseIsCustom;
   }

   /**
    * @param responseIsCustom the responseIsCustom to set
    */
   public void setResponseIsCustom( Boolean responseIsCustom )
   {
      this.responseIsCustom = responseIsCustom;
   }

   /**
    * @return the omitNullMapValuesInResponse
    */
   public Boolean getOmitNullMapValuesInResponse()
   {
      return omitNullMapValuesInResponse;
   }

   /**
    * @param omitNullMapValuesInResponse the omitNullMapValuesInResponse to set
    */
   public void setOmitNullMapValuesInResponse( Boolean omitNullMapValuesInResponse )
   {
      this.omitNullMapValuesInResponse = omitNullMapValuesInResponse;
   }

   /**
    * @return the isDownload
    */
   public Boolean getIsDownload()
   {
      return isDownload;
   }

   /**
    * @param isDownload the isDownload to set
    */
   public void setIsDownload( Boolean isDownload )
   {
      this.isDownload = isDownload;
   }

   /**
    * @return the charset
    */
   public String getCharset()
   {
      return charset;
   }

   /**
    * @param charset the charset to set
    */
   public void setCharset( String charset )
   {
      this.charset = charset;
   }

   /**
    * @return the colseConnection
    */
   public Boolean getCloseConnection()
   {
      return closeConnection;
   }

   /**
    * @param colseConnection the colseConnection to set
    */
   public void setCloseConnection( Boolean closeConnection )
   {
      this.closeConnection = closeConnection;
   }

   /**
    * pretty print response data .
    * 
    * @since 0.x
    *        <p>
    *        Note:not support at this version.
    * @return the prettyPrintResponse
    */
   public Boolean getPrettyPrintResponse()
   {
      return prettyPrintResponse;
   }

   /**
    * @param prettyPrintResponse the prettyPrintResponse to set
    */
   public void setPrettyPrintResponse( Boolean prettyPrintResponse )
   {
      this.prettyPrintResponse = prettyPrintResponse;
   }

   /**
    * @return the isExport
    */
   public Boolean getIsExport()
   {
      return isExport;
   }

   /**
    * @param isExport the isExport to set
    */
   public void setIsExport( Boolean isExport )
   {
      this.isExport = isExport;
   }

   /**
    * @return the enableAllDS
    */
   public Boolean getEnableAllDS()
   {
      return enableAllDS;
   }

   /**
    * @param enableAllDS the enableAllDS to set
    */
   public void setEnableAllDS( Boolean enableAllDS )
   {
      this.enableAllDS = enableAllDS;
   }

   /**
    * @return the rest
    */
   public Boolean isRest()
   {
      return rest;
   }

   /**
    * @param rest the rest to set
    */
   public void setRest( Boolean rest )
   {
      this.rest = rest;
   }

   /**
    * @return the customHTML
    */
   public String getCustomHTML()
   {
      return customHTML;
   }

   /**
    * @param customHTML the customHTML to set
    */
   public void setCustomHTML( String customHTML )
   {
      this.customHTML = customHTML;
   }

   /**
    * @return the serverVersion
    */
   public String getServerVersion()
   {
      return serverVersion;
   }

   /**
    * @param serverVersion the serverVersion to set
    */
   public void setServerVersion( String serverVersion )
   {
      this.serverVersion = serverVersion;
   }


   /**
    * @return the defaultDBName
    */
   public String getDefaultDBName()
   {
      return defaultDBName;
   }

   /**
    * @param defaultDBName the defaultDBName to set
    */
   public void setDefaultDBName( String defaultDBName )
   {
      this.defaultDBName = defaultDBName;
   }

   /**
    * @return the customerConfig
    */
   public Map< Object ,Object > getCustomerConfig()
   {
      return customerConfig;
   }

   /**
    * @param customerConfig the customerConfig to set
    */
   public void setCustomerConfig( Map< Object ,Object > customerConfig )
   {
      this.customerConfig = customerConfig;
   }

   /**
    * @return the templateContext
    */
   public Map< String ,Object > getTemplateContext()
   {
      return templateContext;
   }

   /**
    * @param templateContext the templateContext to set
    */
   public void setTemplateContext( Map< String ,Object > templateContext )
   {
      this.templateContext = templateContext;
   }

   public Map< String ,Object > templateContext;

   private Map attributes;

   /**
    * @param string
    * @param servletRequestAttributeMapFacade
    */
   public void addToTemplateContext( String key, Object value )
   {
      if ( templateContext == null )
         templateContext = new HashMap< String ,Object >();
      templateContext.put( key, value );

   }

   /**
    * @param string
    * @return
    */
   public Object getFromTemplateContext( String string )
   {
      if ( templateContext == null )
         return null;
      return templateContext.get( string );
   }

   /**
    * @param key
    * @return
    */
   public Object getAttribute(String key)
   {
      if (attributes != null)
         return attributes.get(key);
      return null;
   }


   public void setAttribute(String key, Object value)
   {
      if (attributes == null)
         attributes = new HashMap();
      attributes.put(key, value);
   }

   public void removeAttribute(String key)
   {
      if (attributes != null)
         attributes.remove(key);

   }
}
