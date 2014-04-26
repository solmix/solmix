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
public enum FieldType implements ValueEnum
{
   /**
    * Generic text, e.g. <code>"John Doe"</code>. This is the default field type. Use <code>field.length</code> to set
    * length.
    */
   TEXT( "text" ) ,
   /**
    * A boolean value, e.g. <code>true</code>
    */
   BOOLEAN( "boolean" ) ,
   /**
    * A whole number, e.g. <code>123</code>
    */
   INTEGER( "integer" ) ,
   /**
    * A floating point (decimal) number, e.g. <code>1.23</code>
    */
   FLOAT( "float" ) ,
   /**
    * A date, including time of day. Represented on the client as a JavaScript <code>Date</code> object. object.
    */
   DATE( "date" ) ,

   /**
    * A time of day, with no date. Represented internally on the client as a JavaScript Date object in UTC/GMT by
    * default
    */
   TIME( "time" ) ,

   /**
    * A date and time, accurate to the second. Represented on the client as a JavaScript Date object.
    */
   DATETIME( "datetime" ) ,
   /**
    * A text value constrained to a set of legal values specified by the field's
    * {@link com.smartgwt.client.data.DataSourceField#setValueMap(String[]) valueMap} , as though an&#010
    * {@link com.smartgwt.client.widgets.form.validator.IsOneOfValidator} validator had been declared.
    */
   ENUM( "enum" ) ,
   /**
    * An enum whose values are numeric.
    */
   INTENUM( "intEnum" ) ,
   /**
    * If you are using the SmartGWT SQL datasource connector, a <code>sequence</code> is a unique, increasing whole
    * number, incremented whenever a new record is added. Otherwise, <code>sequence</code> behaves identically to
    * <code>integer</code>. This type is typically used with <code>field.primaryKey</code> to auto-generate unique
    * primary keys.
    */
   SEQUENCE( "sequence" ) ,
   /**
    * A string representing a well-formed URL. Some components will render this as an HTML link (using an anchor tag for
    * example).
    */
   LINK( "link" ) ,
   /**
    * A string representing a well-formed URL that points to an image. Some components will render an IMG tag with the
    * value of this field as the 'src' attribute to render the image.
    */
   IMAGE( "image" ) ,
   /**
    * Arbitrary binary data. When this field type is present, three additional fields are automatically generated. They
    * are: &lt;fieldName&gt;_filename, &lt;fieldName&gt;_filesize, and&#010 &lt;fieldName&gt;_date_created where
    * &lt;fieldName&gt; is the value of the <code>name</code>&#010 attribute of this field. These fields are marked
    * as&#010 {@link com.smartgwt.client.data.DataSourceField#setHidden(Boolean) hidden }<code>:true</code> to suppress
    * their rendering by default. You&#010 can show one or more of these fields by specifying the field with a
    * <code>hidden:false</code>&#010 override in the fields array of the databound component.
    */
   BINARY( "binary" ) ,

   /**
    * Binary data comprising an image.
    */
   IMAGEFILE( "imageFile" ) ,

   /**
    * Fields of this type are automatically populated by the Smart GWT Server with the current authenticated userId as
    * part of add and update operations. By default, fields of this type are hidden and not editable; the server ignores
    * any value that the client sends in a field of this type.
    */
   MODIFIER( "modifier" ) ,

   /**
    * Fields of this type are automatically populated by the Smart GWT Server with the current date and time as part of
    * add and update operations. By default, fields of this type are hidden and not editable; the server ignores any
    * value that the client sends in a field of this type.
    */
   MODIFIERTIMESTAMP( "modifierTimestamp" ) ,

   /**
    * Fields of this type are automatically populated by the Smart GWT Server with the current authenticated userId as
    * part of add operations. By default, fields of this type are hidden and not editable; the server ignores any value
    * that the client sends in a field of this type.
    */
   CREATOR( "creator" ) ,

   /**
    * Fields of this type are automatically populated by the Smart GWT Server with the current date and time as part of
    * add and update operations. By default, fields of this type are hidden and not editable; the server ignores any
    * value that the client sends in a field of this type.
    */
   CREATORTIMESTAMP( "creatorTimestamp" ) , BLOB( "blob" ) ,
   /**
    * Password field type
    */
   PASSWORD( "password" );

   private String value;

   FieldType( String value )
   {
      this.value = value;
   }

   public String value()
   {
      return this.value;
   }
}
