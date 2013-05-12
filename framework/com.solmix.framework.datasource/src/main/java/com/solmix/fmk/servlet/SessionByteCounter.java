
package com.solmix.fmk.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.solmix.commons.io.IByteCounter;
import com.solmix.commons.logs.Logger;

public class SessionByteCounter implements IByteCounter
{

   public SessionByteCounter( HttpServletRequest request, long totalBytes, String formID, List errors )
   {
      log = new Logger( SessionByteCounter.class.getName() );
      bytesSoFar = 0L;
      this.totalBytes = 0L;
      lastUpdateMod = 0L;
      updateSessionFrequency = 1024L;
      this.request = request;
      session = request.getSession( true );
      this.totalBytes = totalBytes;
      this.formID = formID;
      clearSession();
      session.setAttribute( "totalBytes", new Long( totalBytes ) );
      if ( errors != null )
         setErrors( errors );
   }

   public long getTotalBytes()
   {
      return totalBytes;
   }

   public long getBytesSoFar()
   {
      return bytesSoFar;
   }

   public void incrementBy( long numBytes )
   {
      bytesSoFar += numBytes;
      long mod = bytesSoFar / updateSessionFrequency;
      if ( mod > lastUpdateMod )
      {
         lastUpdateMod = mod;
         session.setAttribute( "bytesSoFar", new Long( bytesSoFar ) );
      }
   }

   public void setErrors( List errors )
   {
      session.setAttribute( "errors", errors );
   }

   public List getErrors()
   {
      return (List) session.getAttribute( "errors" );
   }

   public void clearSession()
   {
      session.removeAttribute( "errors" );
      session.removeAttribute( "totalBytes" );
      session.removeAttribute( "bytesSoFar" );
   }

   private final Logger log;

   HttpServletRequest request;

   HttpSession session;

   String formID;

   long bytesSoFar;

   long totalBytes;

   long lastUpdateMod;

   long updateSessionFrequency;
}
