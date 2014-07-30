/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.runtime.support.ext;

import java.util.Map;

import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.extension.ExtensionContainer;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-7
 */

public class ContainerAdaptor extends ExtensionContainer
{

    public ContainerAdaptor()
    {
        this(null);
    }

    public ContainerAdaptor(Map<Class<?>, Object> beans)
    {
        this(beans, Thread.currentThread().getContextClassLoader());
    }

    public ContainerAdaptor(Map<Class<?>, Object> beans, ClassLoader extensionClassLoader)
    {
        super(beans);
        BeanConfigurer configurer = (BeanConfigurer) super.extensions.getObject(BeanConfigurer.class);
        if (null == configurer) {
            configurer = new BeanConfigurer() {

                @Override
                public void configureBean(Object beanInstance) {

                }

                @Override
                public void configureBean(String name, Object beanInstance) {

                }

            };
            super.extensions.putObject(BeanConfigurer.class, configurer);
        }
    }

}
