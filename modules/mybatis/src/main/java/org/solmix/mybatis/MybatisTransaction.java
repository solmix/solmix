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

package org.solmix.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月16日
 */

public class MybatisTransaction
{

    private static Logger log = LoggerFactory.getLogger(MybatisTransaction.class.getName());

    public static final String SESSION_ATTR_KEY = "_slx_mybatis_session_key";

    public static final String ENV_NAME = "_slx_envo_name";

    public static String getTransactionKey(String envo) {
        return new StringBuilder().append(SESSION_ATTR_KEY).append("_").append(
            envo).toString();
    }

    public static boolean startTransaction(DSCall dsc, String envo,
        SqlSessionFactory sqlSessionFactory) throws SlxException {
        String connectionKey = getTransactionKey(envo);
        SqlSession session = (SqlSession) dsc.getAttribute(connectionKey);
        if (session == null) {
            session = sqlSessionFactory.openSession(false);

            dsc.setAttribute(ENV_NAME, envo);
            dsc.setAttribute(connectionKey, session);
            if (log.isTraceEnabled())
                log.trace("Started new transaction [ " + session.hashCode()
                    + " ]");
            return true;
        } else {
            if (log.isTraceEnabled())
                log.trace((new StringBuilder()).append(
                    "startTransaction called but transaction \"").append(
                    session.hashCode()).append(
                    "\" was already active - ignoring the startTransaction request").toString());
            return true;
        }
    }

    /**
     * @param call
     * @param environment
     * @param sqlSessionFactory
     * @throws SlxException 
     */
    public static void rollbackTransaction(DSCall call, String environment,
        SqlSessionFactory sqlSessionFactory) throws SlxException {
            String connectionKey = getTransactionKey(environment);
            SqlSession session = (SqlSession) call.getAttribute(connectionKey);
            if (session == null)
                throw new SlxException(
                    Tmodule.MYBATIS,
                    Texception.OBJECT_IS_NULL,
                    (new StringBuilder()).append("No current SqlSession for '").append(
                        environment).append("'").toString());
            if(log.isTraceEnabled())
                log.trace((new StringBuilder()).append("Rolling back transaction \"").append(
                session.hashCode()).append("\"").toString());
                session.rollback();
    }

    /**
     * @param call
     * @param environment
     * @param sqlSessionFactory
     * @throws SlxException 
     */
    public static void commitTransaction(DSCall dsc, String environment,
        SqlSessionFactory sqlSessionFactory) throws SlxException {
        String connectionKey = getTransactionKey(environment);
        SqlSession session = (SqlSession) dsc.getAttribute(connectionKey);
        if (session == null)
            throw new SlxException(
                Tmodule.MYBATIS,
                Texception.OBJECT_IS_NULL,
                (new StringBuilder()).append("No current SqlSession for '").append(
                    environment).append("'").toString());
        if(log.isTraceEnabled())
            log.trace((new StringBuilder()).append("Committing transaction \"").append(
                session.hashCode()).append("\"").toString());
        session.commit();
        
    }

    /**
     * @param call
     * @param environment
     * @param sqlSessionFactory
     * @throws SlxException 
     */
    public static void endTransaction(DSCall dsc, String environment,
        SqlSessionFactory sqlSessionFactory) throws SlxException {
        String connectionKey = getTransactionKey(environment);
        SqlSession session = (SqlSession) dsc.getAttribute(connectionKey);
        if (session == null){
            throw new SlxException(
                Tmodule.MYBATIS,
                Texception.OBJECT_IS_NULL,
                (new StringBuilder()).append("No current SqlSession for '").append(
                    environment).append("'").toString());
        } else {
            if(log.isTraceEnabled())
                log.trace((new StringBuilder()).append("Ending transaction \"").append(
                    session.hashCode()).append("\"").toString());
            session.close();
            dsc.removeAttribute(ENV_NAME);
            dsc.removeAttribute(connectionKey);
            return;
        }
        
    }
    
}
