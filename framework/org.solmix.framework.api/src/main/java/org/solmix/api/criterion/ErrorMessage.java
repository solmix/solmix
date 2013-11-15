
package org.solmix.api.criterion;

import java.io.Serializable;
import java.util.HashMap;

public class ErrorMessage extends HashMap<Object,Object> implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 3444176638479218950L;

   public ErrorMessage()
   {
   }

   public ErrorMessage( String errorString )
   {
      this(errorString, null);
   }


   public ErrorMessage( String errorString, Object suggestedValue )
   {
      this( errorString, suggestedValue, (Object)null );
   }

   public ErrorMessage(String errorString, Object suggestedValue, Object... argments)
   {
      put( "errorMessage", errorString );
      if (argments != null)
         this.setArgments(argments);
      if ( suggestedValue != null )
         put( "suggestedValue", suggestedValue );
   }
   public void setErrorString( String errorString )
   {
      put( "errorMessage", errorString );
   }

   public String getErrorString()
   {
      return (String) get( "errorMessage" );
   }

   public void setSuggestedValue( Object suggestedValue )
   {
      if ( suggestedValue != null )
         put( "suggestedValue", suggestedValue );
   }

   public Object getSuggestedValue()
   {
      return get( "suggestedValue" );
   }

   public void setStopOnError( Boolean stopOnError )
   {
      put( "stopOnError", stopOnError );
   }

   public Boolean getStopOnError()
   {
      return (Boolean) get( "stopOnError" );
   }

   public Object[] getArgments()
   {
      return (Object[]) get("argments");
   }

   public void setArgments(Object... argments)
   {
      put("argments", argments);
   }

   public String getMessage()
   {
      return getErrorString() + "Suggested Value is:" + getSuggestedValue().toString();
   }
   @Override
   public String toString(){
       StringBuilder sb = new StringBuilder();
       sb.append("{");
       sb.append(" errorMessage:").append(getErrorString());
       if(getSuggestedValue()!=null)
       sb.append(" suggestedValue:").append(getSuggestedValue());
       if(getArgments()!=null){
           sb.append(" arememts:[");
           for(Object o:getArgments()){
               sb.append(o!=null?o.toString():"null");
           }
           sb.append("]");
       }
       sb.append("}");
       
       return sb.toString();
       
   }
}
