/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.service.toolkit.support;

import static java.util.Collections.unmodifiableSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.service.toolkit.ToolFactory;
import org.solmix.service.toolkit.ToolNameAware;
import org.solmix.service.toolkit.ToolSetFactory;
import org.solmix.service.toolkit.ToolkitContext;
import org.solmix.service.toolkit.ToolkitException;
import org.solmix.service.toolkit.ToolkitService;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月30日
 */

public class DefaultToolkitService implements ToolkitService
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultToolkitService.class);

    private final static AtomicInteger contextKeyCounter = new AtomicInteger();

    private Map<String, Object> toolFactories;

    private Map<String, ToolFactory> tools = new HashMap<String, ToolFactory>();

    private Map<String, ToolSetInfo<ToolSetFactory>> toolsInSet = new HashMap<String, DefaultToolkitService.ToolSetInfo<ToolSetFactory>>();

    private Set<ToolName> toolNames = new HashSet<DefaultToolkitService.ToolName>();

    private Map<String, Object> prePulledTools = new HashMap<String, Object>();

    @Resource
    private Container container;

    @Override
    public ToolkitContext getContext() {
        return new ToolkitContextImpl();
    }

    @PostConstruct
    public void init() {
        if (toolFactories != null) {
            for (Map.Entry<String, Object> e : toolFactories.entrySet()) {
                String name = Assert.assertNotNull(StringUtils.trimToNull(e.getKey()), "tool name");
                Object f = e.getValue();

                if (f instanceof ToolNameAware) {
                    ((ToolNameAware) f).setToolName(name);
                }

                if(f instanceof ToolFactory){
                    ToolFactory factory =(ToolFactory)f;
                    if(factory.isSingleton()){
                        Object tool;
                        try {
                            tool=factory.createTool();
                        } catch (Exception ex) {
                            throw new ToolkitException("Could not create tool: \"" + name + "\"", ex);
                        }
                        ToolName toolName = new ToolName(null, name, false);

                        toolNames.add(toolName);
                        prePulledTools.put(name, tool);
                        if(LOG.isDebugEnabled()){
                            LOG.debug("Pre-pulled tool: {} = {}", toolName, tool);
                        }
                    }else{
                        ToolName toolName = new ToolName(null, name, false);

                        toolNames.add(toolName);
                        tools.put(name, factory);

                       LOG.debug("Pre-queued tool: {}", toolName);
                    }
                }
                if(f instanceof ToolSetFactory){
                    ToolSetFactory factory =(ToolSetFactory)f;
                    if(factory.isSingleton()){
                        Iterable<String> names = factory.getToolNames();
                        if (names != null) {
                            for (String nameInSet : names) {
                                nameInSet = StringUtils.trimToNull(nameInSet);

                                if (nameInSet != null) {
                                    Object tool;
                                    try {
                                        tool = factory.createTool(nameInSet);
                                    } catch (Exception ex) {
                                        throw new ToolkitException("Could not create tool: \"" + name + "." + nameInSet + "\"", ex);
                                    }

                                    ToolName toolName = new ToolName(name, nameInSet, false);

                                    toolNames.add(toolName);
                                    prePulledTools.put(nameInSet, tool);

                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Pre-pulled tool: {} = {}", toolName, tool);
                                    }
                                }
                            }
                        }
                       
                    }else{
                        Iterable<String> names = factory.getToolNames();
                        if (names != null) {
                            for (String nameInSet : names) {
                                nameInSet = StringUtils.trimToNull(nameInSet);

                                if (nameInSet != null) {

                                    ToolName toolName = new ToolName(name, nameInSet, false);

                                    toolNames.add(toolName);
                                    prePulledTools.put(nameInSet, new ToolSetInfo<ToolSetFactory>(name,
                                        factory, null));

                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Pre-queued tool: {}", toolName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public Map<String, Object> getTools() {
        return getContext().getTools();
    }

    public void setToolFactories(Map<String, Object> factories) {
        this.toolFactories = factories;
    }

    static class ToolSetInfo<F>
    {

        private final String toolSetName;

        private final F factory;

        private final Object tool;

        public ToolSetInfo(String toolSetName, F factory, Object tool)
        {
            this.toolSetName = toolSetName;
            this.factory = factory;
            this.tool = tool;
        }

        public String getToolSetName() {
            return toolSetName;
        }

        public F getFactory() {
            return factory;
        }

        public Object getTool() {
            return tool;
        }
    }

    static final class ToolName implements Comparable<ToolName>
    {

        private final String qname;

        private final String name;

        public ToolName(String namespace, String name, boolean parse)
        {
            namespace = StringUtils.trimToNull(namespace);
            name = Assert.assertNotNull(StringUtils.trimToNull(name), "tool name");

            if (parse) {
                name = StringUtils.trim(name, "/");
                int index = name.lastIndexOf("/");

                if (index >= 0) {
                    if (namespace == null) {
                        namespace = name.substring(0, index);
                    } else {
                        namespace = namespace + "/" + name.substring(0, index);
                    }

                    namespace = StringUtils.trim(namespace, "/");
                    name = name.substring(index + 1);
                }
            }

            this.qname = namespace == null ? "/" + name : "/" + namespace + "/" + name;
            this.name = name;
        }

        public String getQualifiedName() {
            return qname;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(ToolName o) {
            return qname.compareTo(o.qname);
        }

        @Override
        public int hashCode() {
            return 31 + qname.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof ToolName)) {
                return false;
            }

            return qname.equals(((ToolName) obj).qname);
        }

        @Override
        public String toString() {
            return qname;
        }
    }

    private class ToolkitContextImpl implements ToolkitContext
    {

        private Set<ToolName> toolNames = new HashSet<DefaultToolkitService.ToolName>();

        private final Map<String, Object> pulledTools = new HashMap<String, Object>();

        private Map<String, Object> alltools;

        private Set<String> alltoolNames;

        public ToolkitContextImpl()
        {
            toolNames.addAll(DefaultToolkitService.this.toolNames);
        }

        @Override
        public Map<String, Object> getTools() {
            for (String name : tools.keySet()) {
                put(name);
            }
            for (String name : toolsInSet.keySet()) {
                put(name);
            }
            alltools = new HashMap<String, Object>();
            if(pulledTools!=null){
                alltools.putAll(pulledTools);
            }
            if(prePulledTools!=null){
                alltools.putAll(prePulledTools);
            }
            return Collections.unmodifiableMap(alltools);
        }
        @Override
        public Object put(String name) {
            name = StringUtils.trimToNull(name);
            if (name == null) {
                return null;
            }
            Object tool;

            // 如果name已经被pre-pulled，则直接返回
            tool = prePulledTools.get(name);
            if (tool == null) {
                // 检查本地缓存，如果name早已存在，则直接返回
                tool = pulledTools.get(name);

                if (tool == null) {
                    tool = doPulling(name); // encoded tool

                    if (tool != null) {
                        pulledTools.put(name, tool);
                    }
                }
            }
            return tool;
        }
        @Override
        public Set<String> getToolNames() {
            if (alltoolNames == null) {
                Set<String> names = new TreeSet<String>();

                for (ToolName toolName : toolNames) {
                    names.add(toolName.getName());
                }

                alltoolNames = unmodifiableSet(names);
            }

            return alltoolNames;
        }
        @Override
        public Set<String> getQualifiedToolNames() {
            Set<String> names = new TreeSet<String>();

            for (ToolName toolName : toolNames) {
                names.add(toolName.getQualifiedName());
            }

            return names;
        }

        private Object doPulling(String name) {
            // 如果存在于tools中，则pull之。
            ToolFactory toolFactory = tools.get(name);

            if (toolFactory != null) {
                Object tool;

                try {
                    tool = toolFactory.createTool();
                } catch (Exception ex) {
                    throw new ToolkitException("Could not create tool: \"" + name + "\"", ex);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Pulled tool: {} = {}", name, tool);
                }

                return tool;
            }

            // 如果存在于toolsInSet中，则pull之。
            ToolSetInfo<ToolSetFactory> toolSetInfo = toolsInSet.get(name);

            if (toolSetInfo != null) {
                Object tool;

                try {
                    tool = toolSetInfo.getFactory().createTool(name);
                } catch (Exception ex) {
                    throw new ToolkitException("Could not create tool: \"" + toolSetInfo.getToolSetName() + "." + name + "\"", ex);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Pulled tool: {}.{} = {}", new Object[] { toolSetInfo.getToolSetName(), name, tool });
                }

                return tool;
            }
            return null;
        }
    }
}
