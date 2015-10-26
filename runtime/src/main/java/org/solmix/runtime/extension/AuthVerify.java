/*
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

package org.solmix.runtime.extension;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.solmix.commons.util.NetUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年10月26日
 */

public class AuthVerify
{

    private static final int VALIDATE_IP = 1, VALIDATE_SCOPE = 2;

    private String text;

    private static Pattern colonPattern = Pattern.compile(":");

    private Date startDate;

    private Date endDate;

    private String ip;

    public AuthVerify(String text)
    {
        text = text.trim();
        this.text = text;
    }

    public boolean verify() {
        try {
            String[] parts = colonPattern.split(text, 0);
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] datas = parts[0].split("~");
            startDate = sdf.parse(datas[0]);
            endDate = sdf.parse(datas[1]);
            if(System.currentTimeMillis()>endDate.getTime()){
                return false;
            }
            if(parts.length>1){
                String[] ips = parts[1].split("/");
                String local = NetUtils.getLocalHost();
                if(Integer.valueOf(ips[0])==VALIDATE_IP){
                    return ips[1].indexOf(local)!=-1;
                }else if(Integer.valueOf(ips[0])==VALIDATE_SCOPE){
                    return NetUtils.isValidIP(ips[1], local);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
