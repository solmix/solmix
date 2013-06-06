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

package com.solmix.fmk.servlets.internel;

import static com.solmix.api.servlets.FilterManager.FILTER_NAME;
import static com.solmix.api.servlets.FilterManager.FILTER_ORDER;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.framework.Constants.SERVICE_RANKING;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.fmk.servlets.internel.helper.SlxFilterConfig;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-9
 */

public class FilterManagerTracker extends ServiceTracker<Filter, Filter>
{

    /** default log */
    private final Logger log = LoggerFactory.getLogger(FilterManagerTracker.class);

    private static final String FILTER_SERVICE_NAME = Filter.class.getName();

    private final FilterChainHelper filterChain;

    private ServletContext servletContext;

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    public FilterManagerTracker(BundleContext context, ServletContext servletContext)
    {
        super(context, FILTER_SERVICE_NAME, null);
        filterChain = new FilterChainHelper();

        this.servletContext = servletContext;
    }

    public FilterChainHelper getFilterChain() {
        return this.filterChain;
    }

    @Override
    public Filter addingService(ServiceReference<Filter> reference) {
        Filter service = super.addingService(reference);
        initFilter(service, reference);
        return service;
    }

    /**
     * @param service
     * @param reference
     */
    private synchronized void initFilter(Filter filter, ServiceReference<Filter> reference) {
        Object filterName = reference.getProperty(FILTER_NAME);
        if (filterName == null) {
            filterName = reference.getProperty(SERVICE_ID);
        }
        if (filterName == null) {
            log.error("initFilter: Missing name for filter {}", reference);
        } else {
            try {
                final FilterConfig config = new SlxFilterConfig(servletContext, reference, filterName.toString());
                filter.init(config);
                Long serviceId = (Long) reference.getProperty(SERVICE_ID);
                int orderId;
                // get the order, Integer.MAX_VALUE by default
                Object orderObj = reference.getProperty(SERVICE_RANKING);
                // filter order is defined as lower value has higher priority
                // while service ranking is the opposite
                // In addition we allow different types than Integer
                if (orderObj == null) {
                    orderObj = reference.getProperty(FILTER_ORDER);
                    if (orderObj != null && orderObj instanceof Integer) {
                        orderId = ((Integer) orderObj).intValue();
                        orderId = orderId * -1;

                    }
                }
                orderId = (orderObj instanceof Integer) ? ((Integer) orderObj).intValue() : 0;
                this.getFilterChain().addFilter(filter, serviceId, orderId);

            } catch (ServletException e) {
                log.error("Filter " + filterName + " failed to initialize", e);
            } catch (Throwable t) {
                log.error("Unexpected Problem initializingFilter ", t);
            }
        }

    }

    @Override
    public void modifiedService(ServiceReference<Filter> reference, Filter service) {
        destroyFilter(service, reference);
        initFilter(service, reference);
        super.modifiedService(reference, service);
    }

    /**
     * @param service
     * @param reference
     */
    private void destroyFilter(Filter service, ServiceReference<Filter> reference) {
        Object service_id = reference.getProperty(SERVICE_ID);
        boolean remove = false;
        remove = this.filterChain.removeFilterById(service_id);
        if (remove) {
            try {
                service.destroy();
            } catch (Exception e) {
                log.error("Unexpected problem destroying Filter {}", filter, e);
            }
        }

    }

    @Override
    public void removedService(ServiceReference<Filter> reference, Filter service) {
        this.destroyFilter(service, reference);
        super.removedService(reference, service);

    }

}
