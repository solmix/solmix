package com.solmix.api.interfaces;


public interface CURD
{
   Object add(Object create) throws Exception;
   Object update(Object update)throws Exception;
   Object remove(Object delete)throws Exception;
   Object fetch(Object select)throws Exception;

}
