
package org.solmix.service.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

public class EventHandlerTracker extends ServiceTracker<IEventHandler, EventHandlerProxy>
{

    /** The proxies in this list match all events. */
    private final List<EventHandlerProxy> matchingAllEvents;

    /**
     * This is a map for exact topic matches. The key is the topic, the value is a list of proxies.
     */
    private final Map<String, List<EventHandlerProxy>> matchingTopic;

    /**
     * This is a map for wildcard topics. The key is the prefix of the topic, the value is a list of proxies
     */
    private final Map<String, List<EventHandlerProxy>> matchingPrefixTopic;

    /** The context for the proxies. */
    private HandlerContext handlerContext;

    public EventHandlerTracker(final BundleContext context)
    {
        super(context, IEventHandler.class.getName(), null);

        // we start with empty collections
        this.matchingAllEvents = new CopyOnWriteArrayList<EventHandlerProxy>();
        this.matchingTopic = new ConcurrentHashMap<String, List<EventHandlerProxy>>();
        this.matchingPrefixTopic = new ConcurrentHashMap<String, List<EventHandlerProxy>>();
    }

    /**
     * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
     */
    @Override
    public EventHandlerProxy addingService(final ServiceReference<IEventHandler> reference) {
        final EventHandlerProxy proxy = new EventHandlerProxy(this.handlerContext, reference);
        if (proxy.update()) {
            this.put(proxy);
        }
        return proxy;
    }

    /**
     * @see org.osgi.util.tracker.ServiceTracker#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void modifiedService(final ServiceReference<IEventHandler> reference, final EventHandlerProxy proxy) {
        this.remove(proxy);
        if (proxy.update()) {
            this.put(proxy);
        }
    }

    /**
     * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void removedService(final ServiceReference<IEventHandler> reference, final EventHandlerProxy proxy) {
        this.remove(proxy);
        proxy.dispose();
    }

    private void updateMap(final Map<String, List<EventHandlerProxy>> proxyListMap, final String key, final EventHandlerProxy proxy, final boolean add) {
        List<EventHandlerProxy> proxies = proxyListMap.get(key);
        if (proxies == null) {
            if (!add) {
                return;
            }
            proxies = new CopyOnWriteArrayList<EventHandlerProxy>();
            proxyListMap.put(key, proxies);
        }

        if (add) {
            proxies.add(proxy);
        } else {
            proxies.remove(proxy);
            if (proxies.size() == 0) {
                proxyListMap.remove(key);
            }
        }
    }

    /**
     * Check the topics of the event handler and put it into the corresponding collections.
     */
    private synchronized void put(final EventHandlerProxy proxy) {
        final String[] topics = proxy.getTopics();
        if (topics == null) {
            this.matchingAllEvents.add(proxy);
        } else {
            for (int i = 0; i < topics.length; i++) {
                final String topic = topics[i];

                if (topic.endsWith("/*")) {
                    // prefix topic: we remove the /*
                    final String prefix = topic.substring(0, topic.length() - 2);
                    this.updateMap(this.matchingPrefixTopic, prefix, proxy, true);
                } else {
                    // exact match
                    this.updateMap(this.matchingTopic, topic, proxy, true);
                }
            }
        }
    }

    /**
     * Check the topics of the event handler and remove it from the corresponding collections.
     */
    private synchronized void remove(final EventHandlerProxy proxy) {
        final String[] topics = proxy.getTopics();
        if (topics == null) {
            this.matchingAllEvents.remove(proxy);
        } else {
            for (int i = 0; i < topics.length; i++) {
                final String topic = topics[i];

                if (topic.endsWith("/*")) {
                    // prefix topic: we remove the /*
                    final String prefix = topic.substring(0, topic.length() - 2);
                    this.updateMap(this.matchingPrefixTopic, prefix, proxy, false);
                } else {
                    // exact match
                    this.updateMap(this.matchingTopic, topic, proxy, false);
                }
            }
        }
    }

    /**
     * Get all handlers for this event
     *
     * @param event The event topic
     * @return All handlers for the event
     */
    public Collection<EventHandlerProxy> getHandlers(final IEvent event) {
        final String topic = event.getTopic();

        final Set<EventHandlerProxy> handlers = new HashSet<EventHandlerProxy>();

        // Add all handlers matching everything
        this.checkHandlerAndAdd(handlers, this.matchingAllEvents, event);

        // Now check for prefix matches
        if (!this.matchingPrefixTopic.isEmpty()) {
            int pos = topic.lastIndexOf('/');
            while (pos != -1) {
                final String prefix = topic.substring(0, pos);
                this.checkHandlerAndAdd(handlers, this.matchingPrefixTopic.get(prefix), event);

                pos = prefix.lastIndexOf('/');
            }
        }

        // Add the handlers for matching topic names
        this.checkHandlerAndAdd(handlers, this.matchingTopic.get(topic), event);

        return handlers;
    }

    /**
     * Checks each handler from the proxy list if it can deliver the event If the event can be delivered, the proxy is
     * added to the handlers.
     */
    private void checkHandlerAndAdd(final Set<EventHandlerProxy> handlers, final List<EventHandlerProxy> proxies, final IEvent event) {
        if (proxies != null) {
            for (final EventHandlerProxy p : proxies) {
                if (p.canDeliver(event)) {
                    handlers.add(p);
                }
            }
        }
    }

    /**
     * The context object passed to the proxies.
     */
    static final class HandlerContext
    {

        /** The bundle context. */
        public final BundleContext bundleContext;

        /** Is a topic required. */
        public final boolean requireTopic;

        public HandlerContext(final BundleContext bundleContext, final boolean requireTopic)
        {
            this.bundleContext = bundleContext;
            this.requireTopic = requireTopic;
        }
    }
}
