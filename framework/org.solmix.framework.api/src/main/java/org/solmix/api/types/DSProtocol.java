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

package org.solmix.api.types;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-18 solmix-api
 */
public enum DSProtocol implements ValueEnum
{
   /**
    * Data is added to the dataURL, with each property in the data becoming an HTTP parameter,&#010 eg
    * http://service.com/search?keyword=foo
    */
   GETPARAMS( "getParams" ) ,
   /**
    * Data is POST'd to the dataURL, with each property becoming an HTTP parameter, &#010 exactly as an HTML form would
    * submit them if it had one input field per property in the&#010 data.
    */
   POSTPARAMS( "postParams" ) ,
   /**
    * Data is serialized as XML via and POST'd as the&#010 HTTP request body with contentType text/xml
    */
   POSTXML( "postXML" ) ,

   /**
    * This setting entirely bypasses the SmartGWT comm system. Instead of the DataSource sending an HTTP request to the
    * server, the developer is expected to override
    * {@link com.smartgwt.client.data.DataSource#transformRequest(com.smartgwt.client.data.DSRequest)} to perform their
    * own custom data manipulation logic, and then call
    * {@link com.smartgwt.client.data.DataSource#processResponse(String, com.smartgwt.client.data.DSResponse)} to handle
    * the results of this action. The user must populate dsRequest.data in the transformRequest method. If call was
    * successful status and data should be filled. If call was unsuccessful only status should contain error code.
    */
   CLIENTCUSTOM( "clientCustom" ) ,

   /**
    * Data is serialized as XML via
    * {@link com.smartgwt.client.data.DataSource#xmlSerialize(com.google.gwt.core.client.JavaScriptObject)} , wrapped in
    * a SOAP&#010 envelope, and POST'd as the HTTP request body with contentType "text/xml". Generally&#010 only used in
    * connection with a {@link com.smartgwt.client.docs.WsdlBinding 'WSDL web service'}.
    */
   SOAP( "soap" ) ,
   /**
    * dsRequest.data is assumed to be a String set up by
    * {@link com.smartgwt.client.data.DataSource#transformRequest(com.smartgwt.client.data.DSRequest)} &#010 and is
    * POST'd as the HTTP request body.
    */
   POSTMESSAGE( "postMessage" );

   private String value;

   DSProtocol( String value )
   {
      this.value = value;
   }

   public String value()
   {
      return this.value;
   }
}
