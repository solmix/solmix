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

package org.solmix.api.types;

/**
 * BuildIn export type.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public enum ExportType implements StringValueEnum
{
    DEFAULT(1) , JSON(2) , XML(3) , XLS(4) , OOXML(5) , OTHER(255);

    private int value;

    ExportType(int value)
    {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getStringValue() {
        switch (value) {
            case 1:
                return "default";
            case 2:
                return "json";
            case 3:
                return "xml";
            case 4:
                return "xls";
            case 5:
                return "ooxml";
            default:
                return "other";
        }
    }
}
