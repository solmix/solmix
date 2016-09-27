/**
 * Copyright (c) 2014 The Solmix Project
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
package org.solmix.exchange.interceptor.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.Reflection;
import org.solmix.commons.util.StringUtils;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.FaultType;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;


/**
 * 转化为本地Exception.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月12日
 */

public class ClientFaultConverter extends PhaseInterceptorSupport<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientFaultConverter.class);
    public ClientFaultConverter() {
        this(Phase.UNMARSHAL);
    }

    public ClientFaultConverter(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Fault fault = (Fault)message.getContent(Exception.class);
        FaultType type  = message.get(FaultType.class);
           String detail= fault.getDetail();
           if(!StringUtils.isEmpty(detail)){
               Class<?> exceptionClass = null;
               List<String> lines = new ArrayList<String>();
               try {
                   StringReader stringReader = new StringReader(detail);
                   BufferedReader reader = new BufferedReader(stringReader);
                    try {
                        String line;
                        while ((line = reader.readLine()) != null)
                            lines.add(line);
                    } finally {
                        reader.close();
                    }
                    String firstLine= lines.get(0);
                    String className = firstLine.substring(0, firstLine.indexOf(":"));
                    String msg =firstLine.substring(firstLine.indexOf(":")+1,firstLine.length());
                    if(type==FaultType.CHECKED_FAULT){
                        exceptionClass= ClassLoaderUtils.loadClass(className, ClientFaultConverter.class);
                    }else{
                        if(className.startsWith("java.lang")){
                            exceptionClass= ClassLoaderUtils.loadClass(className, ClientFaultConverter.class);
                        }else{
                            exceptionClass= java.io.IOException.class;
                        }
                    }
                   
                    if(exceptionClass!=null&&Throwable.class.isAssignableFrom(exceptionClass)){
                        Throwable throwable= null;
                        Constructor<?> cons=  exceptionClass.getConstructor(String.class);
                        if(cons!=null){
                            throwable=(Throwable) cons.newInstance(msg);
                        }else{
                            throwable=  (Throwable)Reflection.newInstance(exceptionClass);
                        }
                        List<StackTraceElement> stackTraceList = new ArrayList<StackTraceElement>();
                        for(int i=1;i<lines.size();i++){
                            String st= lines.get(i);
                            StackTraceElement stack = parseStackTrackLine(st);
                            if(stack!=null)
                            	stackTraceList.add(stack);
                        }
                        StackTraceElement[] stackTraceElement = new StackTraceElement[stackTraceList.size()];
                        throwable.setStackTrace(stackTraceList.toArray(stackTraceElement));
                        message.setContent(Exception.class, new Fault(throwable));
                    }
               } catch (IOException e) {
                   //ignore
               } catch (Exception e) {
                   throw new Fault(e);
               }
              
        }

    }
    private static StackTraceElement parseStackTrackLine(String oneLine) {
    	String[] lineSplited =oneLine.split("!");
    	if(lineSplited.length>=4){
    		return new StackTraceElement(lineSplited[0], lineSplited[1],
    				lineSplited[2], Integer.parseInt(lineSplited[3]));
    	}else{
    		return null;
    	}
    	
    }
    
    public static String toString(Throwable e ,int limit) {
        return toString(null, e, limit);
    }
    public static String toString(String msg, Throwable e ,int limit) {
        StringBuffer w = new StringBuffer();
       
        w.append(e.getClass().getName()+":");
        if (msg != null) {
            w.append(msg+": " );
        }
        if (e.getMessage() != null) {
            w.append( e.getMessage());
        }
        w.append("\n");
            StackTraceElement[] elements = e.getStackTrace();
            if(elements!=null){
                for(StackTraceElement el:elements){
                    if(w.length()<limit-60){
                    w.append(el.getClassName()).append("!").append(el.getMethodName()).append("!")
                    .append(el.getFileName()).append("!").append(el.getLineNumber()).append("\n");
                    }else{
                        w.append("....more....");
                    }
                }
                
            }
            return w.toString();
    }
}
