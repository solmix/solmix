/*
 * ========THE SOLMIX PROJECT=====================================
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
package org.solmix.web.shared.comet;

import java.io.Serializable;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-23
 */
public class IMEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 73939688459784314L;
    public  int CONNECTED_NOTIFICATION=-1;
    private String message;
    private String sourceName;
    private String targetName;
    private int type=0;
    public IMEvent() {

    }

    public IMEvent(String sourceName, String targetName,String data) {
        this.sourceName=sourceName;
        this.targetName=targetName;
        this.message = data;
    }
    public IMEvent(String sourceName, String targetName) {
        this.sourceName=sourceName;
        this.targetName=targetName;
    }



    
    
    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    
    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

  

    @Override
    public String toString() {
        return "Form["+sourceName + "] to ["+targetName+"] message:" + getMessage();
    }

    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
    /**
     * @return the sourceName
     */
    public String getSourceName() {
        return sourceName;
    }

    
    /**
     * @param sourceName the sourceName to set
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    
    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    
    /**
     * @param targetName the targetName to set
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }


}