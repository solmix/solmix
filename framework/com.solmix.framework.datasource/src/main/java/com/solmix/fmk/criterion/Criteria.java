package com.solmix.fmk.criterion;

import java.util.HashMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110045  2013-3-28
 */
public class Criteria extends HashMap<String, Object>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   public Criteria(String key,Object value){
       put(key,value);
   }
   public Criteria add(String key,Object value){
       this.put(key,value);
       return this;
   }
}
