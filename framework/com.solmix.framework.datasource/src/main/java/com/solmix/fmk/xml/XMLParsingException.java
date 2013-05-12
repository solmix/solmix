
package com.solmix.fmk.xml;

import java.util.List;

public class XMLParsingException extends Exception
{

   public XMLParsingException()
   {
      errors = null;
   }

   public XMLParsingException( String message )
   {
      super( message );
      errors = null;
   }

   public List getErrors()
   {
      return errors;
   }

   public void setErrors( List errors )
   {
      this.errors = errors;
   }

   List errors;
}
