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

package com.smartgwt.extensions.fusionchart.shared;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-6
 */
@XmlRootElement(name = "graph")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tgraph
{

    /**
     * @return the set
     */
    public List<Tset> getSet() {
        if (set == null) {
            set = new ArrayList<Tset>();
        }
        return set;
    }

    /**
     * @param set the set to set
     */
    public void setSet(List<Tset> set) {
        this.set = set;
    }

    /**
     * @return the trendlines
     */
    public Ttrendlines getTrendlines() {
        return trendlines;
    }

    /**
     * @param trendlines the trendlines to set
     */
    public void setTrendlines(Ttrendlines trendlines) {
        this.trendlines = trendlines;
    }

    /**
     * @return the bgcolor
     */
    public String getBgcolor() {
        return bgcolor;
    }

    /**
     * @param bgcolor the bgcolor to set
     */
    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return the subCaption
     */
    public String getSubCaption() {
        return subCaption;
    }

    /**
     * @param subCaption the subCaption to set
     */
    public void setSubCaption(String subCaption) {
        this.subCaption = subCaption;
    }

    /**
     * @return the yaxismaxvalue
     */
    public String getYaxismaxvalue() {
        return yaxismaxvalue;
    }

    /**
     * @param yaxismaxvalue the yaxismaxvalue to set
     */
    public void setYaxismaxvalue(String yaxismaxvalue) {
        this.yaxismaxvalue = yaxismaxvalue;
    }

    /**
     * @return the yaxisminvalue
     */
    public String getYaxisminvalue() {
        return yaxisminvalue;
    }

    /**
     * @param yaxisminvalue the yaxisminvalue to set
     */
    public void setYaxisminvalue(String yaxisminvalue) {
        this.yaxisminvalue = yaxisminvalue;
    }

    /**
     * @return the yaxisname
     */
    public String getYaxisname() {
        return yaxisname;
    }

    /**
     * @param yaxisname the yaxisname to set
     */
    public void setYaxisname(String yaxisname) {
        this.yaxisname = yaxisname;
    }

    /**
     * @return the xaxisname
     */
    public String getXaxisname() {
        return xaxisname;
    }

    /**
     * @param xaxisname the xaxisname to set
     */
    public void setXaxisname(String xaxisname) {
        this.xaxisname = xaxisname;
    }

    /**
     * @return the hovercapbg
     */
    public String getHovercapbg() {
        return hovercapbg;
    }

    /**
     * @param hovercapbg the hovercapbg to set
     */
    public void setHovercapbg(String hovercapbg) {
        this.hovercapbg = hovercapbg;
    }

    /**
     * @return the hovercapborder
     */
    public String getHovercapborder() {
        return hovercapborder;
    }

    /**
     * @param hovercapborder the hovercapborder to set
     */
    public void setHovercapborder(String hovercapborder) {
        this.hovercapborder = hovercapborder;
    }

    /**
     * @return the numdivlines
     */
    public String getNumdivlines() {
        return numdivlines;
    }

    /**
     * @param numdivlines the numdivlines to set
     */
    public void setNumdivlines(String numdivlines) {
        this.numdivlines = numdivlines;
    }

    /**
     * @return the numberSuffix
     */
    public String getNumberSuffix() {
        return numberSuffix;
    }

    /**
     * @param numberSuffix the numberSuffix to set
     */
    public void setNumberSuffix(String numberSuffix) {
        this.numberSuffix = numberSuffix;
    }

    protected List<Tset> set;

    protected Ttrendlines trendlines;

    @XmlAttribute
    protected String bgcolor;

    @XmlAttribute
    protected String caption;

    @XmlAttribute
    protected String subCaption;

    @XmlAttribute
    protected String yaxismaxvalue;

    @XmlAttribute
    protected String yaxisminvalue;

    @XmlAttribute
    protected String yaxisname;

    @XmlAttribute
    protected String xaxisname;

    @XmlAttribute
    protected String hovercapbg;

    @XmlAttribute
    protected String hovercapborder;

    @XmlAttribute
    protected String numdivlines;

    @XmlAttribute
    protected String numberSuffix;

}
