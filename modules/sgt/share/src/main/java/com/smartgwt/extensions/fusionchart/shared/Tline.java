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

package com.smartgwt.extensions.fusionchart.shared;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-6
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Tline
{

    @XmlAttribute
    private String startvalue;

    @XmlAttribute
    private String displayValue;

    @XmlAttribute
    private String color;

    @XmlAttribute
    private String thickness;

    @XmlAttribute
    private String isTrendZone;

    /**
     * @return the startvalue
     */
    public String getStartvalue() {
        return startvalue;
    }

    /**
     * @param startvalue the startvalue to set
     */
    public void setStartvalue(String startvalue) {
        this.startvalue = startvalue;
    }

    /**
     * @return the displayValue
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * @param displayValue the displayValue to set
     */
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the thickness
     */
    public String getThickness() {
        return thickness;
    }

    /**
     * @param thickness the thickness to set
     */
    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    /**
     * @return the isTrendZone
     */
    public String getIsTrendZone() {
        return isTrendZone;
    }

    /**
     * @param isTrendZone the isTrendZone to set
     */
    public void setIsTrendZone(String isTrendZone) {
        this.isTrendZone = isTrendZone;
    }
}
