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
package org.solmix.commons.management;

import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * 
 * @author solomon
 * @version 110035  2011-11-1
 */

public class MBeanRegistrer
{
    private MBeanServer mbeanServer;

    private Map<Object, String> mbeans;

    public void setMbeans(Map<Object, String> mbeans) {
        this.mbeans = mbeans;
    }

    public void registerMBeanServer(MBeanServer mbeanServer) throws JMException {
        if (this.mbeanServer != mbeanServer) {
            unregisterMBeans();
        }
        this.mbeanServer = mbeanServer;
        registerMBeans();
    }

    public void unregisterMBeanServer(MBeanServer mbeanServer) throws JMException {
        unregisterMBeans();
        this.mbeanServer = null;
    }

    public void init() throws Exception {
        registerMBeans();
    }

    protected void registerMBeans() throws JMException {
        if (mbeanServer != null && mbeans != null) {
            for (Map.Entry<Object, String> entry : mbeans.entrySet()) {
                String value = parseProperty(entry.getValue());
                mbeanServer.registerMBean(entry.getKey(), new ObjectName(value));
            }
        }
    }

    protected void unregisterMBeans() throws JMException {
        if (mbeanServer != null && mbeans != null) {
            for (Map.Entry<Object, String> entry : mbeans.entrySet()) {
                String value = parseProperty(entry.getValue());
                mbeanServer.unregisterMBean(new ObjectName(value));
            }
        }
    }

    protected String parseProperty(String raw) {
        if (raw.indexOf("${") > -1 && raw.indexOf("}", raw.indexOf("${")) > -1) {
            String var = raw.substring(raw.indexOf("${") + 2, raw.indexOf("}"));
            String val = System.getProperty(var);
            if (val != null) {
                raw = raw.replace("${" + var + "}", val);
            }
        }
        return raw;
    }
}
