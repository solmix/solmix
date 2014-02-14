/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.base;

/**
 * The wrapper of a method argument.
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-4-8
 */

public class ReflectionArgument
{

    Class<?> type;

    Object value;

    boolean allowBeanConversion;

    boolean allowTypeConversion;

    public ReflectionArgument(Class<?> type, Object value)
    {
        allowBeanConversion = true;
        allowTypeConversion = true;
        this.type = type;
        this.value = value;
    }

    /**
     * @param type the argument type class.
     * @param value the value of this argument.
     * @param allowBeanConversion If possible,allowed to convert bean to another bean.
     * @param allowTypeConversion If possible,allowed to convert type to another type.
     */
    public ReflectionArgument(Class<?> type, Object value, boolean allowBeanConversion, boolean allowTypeConversion)
    {
        this(type, value);
        this.allowBeanConversion = allowBeanConversion;
        this.allowTypeConversion = allowTypeConversion;
    }

    /**
     * get the argument type class.
     * 
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the allowBeanConversion
     */
    public boolean isAllowBeanConversion() {
        return allowBeanConversion;
    }

    /**
     * @param allowBeanConversion the allowBeanConversion to set
     */
    public void setAllowBeanConversion(boolean allowBeanConversion) {
        this.allowBeanConversion = allowBeanConversion;
    }

    /**
     * Allow this reflection argument auto type conversion.
     * 
     * @return the allowTypeConversion
     */
    public boolean isAllowTypeConversion() {
        return allowTypeConversion;
    }

    /**
     * @param allowTypeConversion the allowTypeConversion to set
     */
    public void setAllowTypeConversion(boolean allowTypeConversion) {
        this.allowTypeConversion = allowTypeConversion;
    }

}
