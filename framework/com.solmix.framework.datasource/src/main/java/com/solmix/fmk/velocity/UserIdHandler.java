
package com.solmix.fmk.velocity;

import com.solmix.api.datasource.DSRequest;
import com.solmix.fmk.auth.Authentication;

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
