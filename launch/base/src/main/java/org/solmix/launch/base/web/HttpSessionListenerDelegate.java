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

package org.solmix.launch.base.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.felix.http.proxy.ProxyListener;

/**
 * HTTP 会话监听代理类。
 * 
 * @author Administrator
 * @version 110035 2012-3-16
 */

public class HttpSessionListenerDelegate implements HttpSessionAttributeListener, HttpSessionListener, ServletContextListener
{

    private final ProxyListener proxyListener = new ProxyListener();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        proxyListener.contextInitialized(sce);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        proxyListener.contextDestroyed(sce);

    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        proxyListener.sessionCreated(se);

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        proxyListener.sessionDestroyed(se);

    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        proxyListener.attributeAdded(se);

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        proxyListener.attributeRemoved(se);

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        proxyListener.attributeReplaced(se);

    }

}
