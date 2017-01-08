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
package org.solmix.service.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月14日
 */

public class DefaultEventTaskManager implements EventTaskManager
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventTaskManager.class);

    private final Container container;
    private final EventHandlerBlackList blackList;
    
    private EventHandlerTracker eventHandlerTracker;

    private final TopicFilter topicFilter;
    
    private final Map<Filter, IEventHandler> cachedHandlers;
    
    private boolean loaded;
    /**
     * @param eventHandlerBlackListImpl
     * @param topicFilter
     */
    public DefaultEventTaskManager( Container container,EventHandlerBlackList backlist, TopicFilter topicFilter)
    {
        this.container=container;
        this.blackList=backlist;
        this.topicFilter=topicFilter;
        cachedHandlers = Collections.synchronizedMap(new HashMap<Filter, IEventHandler>());
    }
    
    public List<EventTask> createEventTasks(IEvent event) {
        if (!loaded) {
        	synchronized (this) {
        		//container中的配置在加载完成时已经确定
        		 getHandlersFromContainer();
        		 loaded=true;
			}
           
        }
        if (event.getTopic() == null || event.getTopic().trim().equals("")) {
            return null;
        }
        final List<EventTask> result = new ArrayList<EventTask>();
        Hashtable<String,String> topicFilter = new Hashtable<String,String>();
        topicFilter.put(TopicFilter.TOPIC_PROP, event.getTopic());
        for (Filter filter : cachedHandlers.keySet()) {
            IEventHandler handler = cachedHandlers.get(filter);
            if (filter.match(topicFilter)) {
                if (!blackList.contains(handler)) {
                    //XXX No support event filter,maybe support by next version.
                    result.add(new DefaultEventTask(this, event, handler));
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("Topic :" + event.getTopic() + " is blacklisting.");
                }
            }
        }
        if(eventHandlerTracker !=null){
        	
        }
        return result;
    }

    private Filter createTopicFilter(String topic) throws InvalidSyntaxException{
    	String formatTopic = topicFilter.createFilter(topic);
    	return  FrameworkUtil.createFilter(formatTopic);
    }
    /**
     * 
     */
    private void getHandlersFromContainer() {
        if(container!=null){
        	//为了支持OSGI，首先检查是否在OSGI下运行，如果在osgi下运行启动IEventHanderTracker
        	final BundleContext context = container.getExtension(BundleContext.class);
        	if(context!=null){
        		synchronized (this) {
        			eventHandlerTracker = new EventHandlerTracker(context);
            		eventHandlerTracker.open();
				}
        		container.addListener(new ContainerListener() {
					
					@Override
					public void handleEvent(ContainerEvent event) {
						if(event.getType()==ContainerEvent.PRECLOSE){
							if(eventHandlerTracker!=null&&context!=null){
								eventHandlerTracker.close();
							}
						}
						
					}
				});
        	}
        	//其它环境下都可以通过ConfiguredBeanProvider屏蔽底层差异
            ConfiguredBeanProvider provider=   container.getExtension(ConfiguredBeanProvider.class);
            Collection< ? extends IEventHandler> handlers=  provider.getBeansOfType(IEventHandler.class);
            for(IEventHandler handler:handlers){
            	addEventHandler(handler);
            }
        }
    }
    

    /**
     * @param handler
     */
    public void addToBlackList(IEventHandler handler) {
        blackList.add(handler);

    }
    
	public void addEventHandler(String topic, IEventHandler handler) {
		try {
			Filter filter =createTopicFilter(topic);
			cachedHandlers.put(filter, handler);
		} catch (InvalidSyntaxException e) {
			logger.error("Filter expression syntax error", e);
		}
	}

	public void addEventHandler(IEventHandler handler) {
		 EventTopic t = handler.getClass().getAnnotation(EventTopic.class);
         if(t==null){
         	String outMsg="handler class {} not annotated by @EventTopic,will be ignored";
         	if(logger.isDebugEnabled()){
         		logger.error(outMsg,handler.getClass().getName());
         	}else{
         		logger.warn(outMsg,handler.getClass().getName());
         	}
         	 return;
         }
        
         String topic = t.value();
         addEventHandler(topic,handler);
	}

	public void removeEventHandler(String topic) {
		  for (Filter filter : cachedHandlers.keySet()) {
	            Hashtable<String,String> topicFilter = new Hashtable<String,String>();
	            topicFilter.put(TopicFilter.TOPIC_PROP, topic);
	            if (filter.match(topicFilter)) {
	               cachedHandlers.remove(filter);
	            }
	        }
	}
}
