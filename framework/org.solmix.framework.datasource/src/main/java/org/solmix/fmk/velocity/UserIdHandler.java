
package org.solmix.fmk.velocity;

import org.solmix.api.datasource.DSRequest;
import org.solmix.fmk.auth.Authentication;

public class UserIdHandler
{

   public UserIdHandler( DSRequest dsRequest )
   {
      this.dsRequest = dsRequest;
   }

   public String toString()
   {
      String userId= dsRequest.getContext().getUserId();
      return userId!=null?userId:Authentication.getUsername();
      
   }

   private DSRequest dsRequest;
}
