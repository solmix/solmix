/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.runtime.security;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月19日
 */

public class KeystoreInfo
{
    private String alias;
    private String filePath;
    private String filePassword;
    private boolean isDefault;
    private String keyCN="";
    private char[] m_arrFilePassword ; 
    public KeystoreInfo(){   
    }
    
    public KeystoreInfo(String alias, String filePath, String filePassword, boolean isDefault){
        this.alias = alias;
        this.filePath = filePath;
        this.setFilePassword( filePassword);
        this.isDefault = isDefault;
    }

    /**
     * @return the keyCN
     */
    public String getKeyCN() {
        return keyCN;
    }

    /**
     * @param keyCN the keyCN to set
     */
    public void setKeyCN(String keyCN) {
        this.keyCN = keyCN;
    }

    /**
     * @return the hqDefault
     */
    public boolean isDefault() {
        return isDefault;
    }
    /**
     * @param hqDefault the hqDefault to set
     */
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }
    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }
    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    /**
     * @return the filePassword
     */
    public String getFilePassword() {
        return filePassword;
    }
    /**
     * @param filePassword the filePassword to set
     */
    public void setFilePassword(String filePassword) {
        this.filePassword = filePassword;
        if(filePassword != null) { 
            this.m_arrFilePassword = filePassword.toCharArray() ; 
        }
    }
    
    public final char[] getFilePasswordCharArray() { 
        return this.m_arrFilePassword ;
    }
    
}
