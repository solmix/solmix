
package org.solmix.fmk.velocity;

import org.solmix.api.datasource.DSRequest;

public class UserIdHandler
{

   public UserIdHandler( DSRequest dsRequest )
   {
      this.dsRequest = dsRequest;
   }

   @Override
public String toString()
   {
      String userId= dsRequest.getContext().getUserId();
      return userId/*!=null?userId:Authentication.getUsername()*/;
      
   }

   private final DSRequest dsRequest;
}
