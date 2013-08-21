package org.solmix.api.interfaces;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-8-21
 */
public interface CURD
{
   Object add(Object create) throws Exception;
   Object update(Object update)throws Exception;
   Object remove(Object delete)throws Exception;
   Object fetch(Object select)throws Exception;

}
