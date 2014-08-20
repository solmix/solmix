/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月20日
 */

public class SQLFilter implements Filter
{
    public static final String DEFAULT_URL="/SqlError.jsp";
    private final String pattern = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(add|exec|insert|select|delete|update|count|mid|master|truncate|char|declare)\\b)";

    private  Pattern sqlPattern;
    private FilterConfig config;
    private String failedUrl;
    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config=filterConfig;
        String checkString=filterConfig.getInitParameter("pattern");
        failedUrl=filterConfig.getInitParameter("pattern");
        if(checkString==null||checkString.trim().isEmpty()){
            checkString=pattern;
        }
        if(failedUrl==null||failedUrl.trim().isEmpty()){
            failedUrl=DEFAULT_URL;
        }
        sqlPattern= Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
       for (Enumeration<?> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
           String name = (String) paramNames.nextElement();
           String values[] = request.getParameterValues(name);
           for(String value:values){
               if(find(value)){
                   RequestDispatcher dispatcher=  request.getRequestDispatcher(DEFAULT_URL);
                   dispatcher.forward(request, response);
               }
           }
           
       }
    }
    
    protected boolean find(String value){
            if(sqlPattern.matcher(value).find()){
              return true;
            }
        return false ;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

}
