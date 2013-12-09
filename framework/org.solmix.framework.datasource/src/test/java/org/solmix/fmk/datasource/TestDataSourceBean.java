/*
 * SOLMIX PROJECT
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.fmk.datasource;

import java.util.Date;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月8日
 */

public class TestDataSourceBean
{

    private String username;
    private String password;
    private long org_id;
    private Date locktime;
    private EnumType type;
    
   
    
    
    /**
     * @return the bean
     */
    public String getBean() {
        return "bean";
    }



    
    /**
     * @param bean the bean to set
     */
    public void setBean(String bean) {
        username = bean;
    }



    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }


    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }


    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }


    
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }


    
    /**
     * @return the org_id
     */
    public long getOrg_id() {
        return org_id;
    }


    
    /**
     * @param org_id the org_id to set
     */
    public void setOrg_id(long org_id) {
        this.org_id = org_id;
    }


    
    /**
     * @return the locktime
     */
    public Date getLocktime() {
        return locktime;
    }


    
    /**
     * @param locktime the locktime to set
     */
    public void setLocktime(Date locktime) {
        this.locktime = locktime;
    }


    
    /**
     * @return the type
     */
    public EnumType getType() {
        return type;
    }


    
    /**
     * @param type the type to set
     */
    public void setType(EnumType type) {
        this.type = type;
    }


    public enum EnumType{
        AA,bb,ccc,ddd;
    }
    
}
