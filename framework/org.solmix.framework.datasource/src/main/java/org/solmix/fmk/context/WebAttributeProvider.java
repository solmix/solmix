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

package org.solmix.fmk.context;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.context.AttributeProvider;
import org.solmix.api.context.Context.Scope;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-28
 */

public class WebAttributeProvider implements AttributeProvider
{

    private static final Logger log = LoggerFactory.getLogger(WebAttributeProvider.class);

    private final WebContextImpl context;

    public WebAttributeProvider(WebContextImpl context)
    {
        this.context = context;
    }

    public HttpServletRequest getRequest() {
        return context.getRequest();
    }

    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        if (value == null) {
            removeAttribute(name, scope);
            return;
        }
        switch (scope) {
            case LOCAL:
                getRequest().setAttribute(name, value);
                break;
            case SESSION:
                if (!(value instanceof Serializable)) {
                    log.warn("Trying to store a non-serializable attribute in session: " + name + ". Object type is " + value.getClass().getName(),
                        new Throwable("This stacktrace has been added to provide debugging information"));
                    return;
                }

                HttpSession httpsession = getRequest().getSession(false);
                if (httpsession == null) {
                    log.debug("Session initialized in order to set attribute '{}' to '{}'. You should avoid using session when possible!", name,
                        value);
                    httpsession = getRequest().getSession(true);
                }
                httpsession.setAttribute(name, value);
                break;
            case SYSTEM:
                context.getSystemContext().setAttribute(name, value, scope);
                break;
            default:
                getRequest().setAttribute(name, value);
                log.debug("Undefined scope, setting attribute [{}] in request scope", name);

        }

    }

    @Override
    public Object getAttribute(String name, Scope scope) {
        switch (scope) {
            case LOCAL:
                Object obj = getRequest().getAttribute(name);
                if (obj == null) {
                    obj = getRequest().getParameter(name);
                }

                return obj;
            case SESSION:
                HttpSession httpsession = getRequest().getSession(false);
                if (httpsession == null) {
                    return null;
                }
                return httpsession.getAttribute(name);
            case SYSTEM:
                return context.getSystemContext().getAttribute(name, scope);
            default:
                log.error("illegal scope passed");
                return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.AttributeProvider#getAttributes(org.solmix.api.context.Context.Scope)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map<String, Object> getAttributes(Scope scope) {
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<String> keysEnum;
        switch (scope) {
            case LOCAL:
                // add parameters
                Enumeration paramEnum = getRequest().getParameterNames();
                while (paramEnum.hasMoreElements()) {
                    final String name = (String) paramEnum.nextElement();
                    map.put(name, getRequest().getParameter(name));
                }
                // attributes have higher priority
                keysEnum = getRequest().getAttributeNames();
                while (keysEnum.hasMoreElements()) {
                    String key = keysEnum.nextElement();
                    Object value = getAttribute(key, scope);
                    map.put(key, value);
                }
                return map;
            case SESSION:
                HttpSession httpsession = getRequest().getSession(false);
                if (httpsession == null) {
                    return map;
                }
                keysEnum = httpsession.getAttributeNames();
                while (keysEnum.hasMoreElements()) {
                    String key = keysEnum.nextElement();
                    Object value = getAttribute(key, scope);
                    map.put(key, value);
                }
                return map;
            case SYSTEM:
                return context.getSystemContext().getAttributes(Scope.SYSTEM);
            default:
                log.error("no illegal scope passed");
                return map;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.AttributeProvider#removeAttribute(java.lang.String,
     *      org.solmix.api.context.Context.Scope)
     */
    @Override
    public void removeAttribute(String name, Scope scope) {
        switch (scope) {
            case LOCAL:
                getRequest().removeAttribute(name);
                break;
            case SESSION:
                HttpSession httpsession = getRequest().getSession(false);
                if (httpsession != null) {
                    httpsession.removeAttribute(name);
                }
                break;
            case SYSTEM:
                context.getSystemContext().removeAttribute(name, scope);
                break;
            default:
                log.error("no illegal scope passed");
        }

    }

}
