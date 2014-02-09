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

package org.solmix.api.context;

import java.util.Locale;

import org.solmix.api.i18n.ResourceBundleManager;

/**
 * The framework internal context,Typical use as follow: 
 * <p>1:
 * <code>
 * ...
 * public XXXXX(final SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
        if(sc!=null){
            sc.setBean(this, PoolManagerFactory.class);
           
        }
    } 
 * ...
 * </code>
 * <p>2:
 * <code>
 * ...
 *  final SystemContext sc = SlxContext.getThreadSystemContext();
 *  sc.getBean(xxx.class);
 *  ...
 * </code>
 * @version 110035 2012-9-28
 */

public interface SystemContext extends Context
{

    public static final String DEFAULT_CONTEXT_ID = "solmix";

    public static final String CONTEXT_PROPERTY_NAME = "solmix.context.system.id";

    /**
     * System context not used this method.used default with {@linkplain ResourceBundleManager}
     */
    @Override
    @Deprecated
    void setLocale(Locale locale);

    <T> T getBean(Class<T> beanType);

    <T> void setBean(T bean, Class<T> beanType);

    boolean hasBeanByName(String name);

    /**
     * Return the SystemContext ID
     * 
     * @return
     */
    String getId();

    /**
     * Close this context.
     * 
     * @param wait
     */
    void close(boolean wait);

    /**
     * Open this context for using.
     */
    void open();
}
