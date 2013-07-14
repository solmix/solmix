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

package org.solmix.api.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-18 solmix-utils
 */
public class DataClass extends HashMap<String, Object>
{

    /**
    * 
    */
    private static final long serialVersionUID = -8157736771810501243L;

    public String getAttribute(String property) {
        return (String) get(property);
    }

    public Boolean getAttributeAsBoolean(String property) {
        return (Boolean) get(property);

    }

    public Date getAttributeAsDate(String property) {
        return (Date) get(property);
    }

    public Double getAttributeAsDouble(String property) {
        return (Double) get(property);
    }

    public Float getAttributeAsFloat(String property) {
        return (Float) get(property);
    }

    public String getAttributeAsString(String property) {
        return (String) get(property);
    }

    public Integer getAttributeAsInt(String property) {
        return (Integer) get(property);
    }

    public int[] getAttributeAsIntArray(String property) {
        return (int[]) get(property);
    }

    public Object getAttributeAsObject(String property) {
        return get(property);
    }

    public Map<?, ?> getAttributeAsMap(String property) {
        return (Map<?, ?>) get(property);
    }

    public String[] getAttributeAsStringArray(String property) {
        return (String[]) get(property);
    }

    public void setAttribute(String property, boolean value) {
        put(property, value);
    }

    public void setAttribute(String property, DataClass value) {
        put(property, value);
    }

    public void setAttribute(String property, DataClass[] value) {
        put(property, value);
    }

    public void setAttribute(String property, double value) {
        put(property, value);
    }

    public void setAttribute(String property, int value) {
        put(property, value);
    }

    public void setAttribute(String property, int[] value) {
        put(property, value);
    }

    public void setAttribute(String property, Boolean value) {
        put(property, value);
    }

    public void setAttribute(String property, Double value) {
        put(property, value);
    }

    public void setAttribute(String property, Float value) {
        put(property, value);
    }

    public void setAttribute(String property, Integer value) {
        put(property, value);
    }

    public void setAttribute(String property, Object value) {
        put(property, value);
    }

    public void setAttribute(String property, String value) {
        put(property, value);
    }

    public void setAttribute(String property, String[] value) {
        put(property, value);
    }

    public void setAttribute(String property, Date value) {
        put(property, value);
    }

    public void setAttribute(String property, Map<?, ?> value) {
        put(property, value);
    }
}
