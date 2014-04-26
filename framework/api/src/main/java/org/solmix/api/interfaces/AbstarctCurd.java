package org.solmix.api.interfaces;


public class AbstarctCurd implements CURD
{

   @Override
   public Object add(Object create) throws Exception
   {
    throw new Exception("No Supported");
   }

   @Override
   public Object update(Object update) throws Exception
   {
      throw new Exception("No Supported");
   }

   @Override
   public Object remove(Object delete) throws Exception
   {
      throw new Exception("No Supported");
   }

   @Override
   public Object fetch(Object select) throws Exception
   {
      throw new Exception("No Supported");
   }

  

}
