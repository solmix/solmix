/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.bean;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.namespace.QName;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月7日
 */

public class ConfiguredBean implements Configurable
{

    private  String configureName;

    public ConfiguredBean(){
        
    }
    public ConfiguredBean(String configureName)
    {
        this.configureName = configureName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.Configurable#getConfigureName()
     */
    @Override
    public String getConfigureName() {
        return configureName;
    }
    private String stringAttr = "hello";
    private Boolean booleanAttr = Boolean.TRUE;
    private BigInteger integerAttr = BigInteger.ONE;
    private Integer intAttr = new Integer(2);
    private Long longAttr = new Long(3);
    private Short shortAttr = new Short((short)4);
    private BigDecimal decimalAttr = new BigDecimal("5");
    private Float floatAttr = new Float(6F);
    private Double doubleAttr = new Double(7D);
    private Byte byteAttr = new Byte((byte)8);
    private QName qnameAttr = new QName("http://www.w3.org/2001/XMLSchema", "schema", "xs");
    private byte[] base64BinaryAttr = DatatypeConverter.parseBase64Binary("abcd");
    private byte[] hexBinaryAttr = new HexBinaryAdapter().unmarshal("aaaa");
    private Long unsignedIntAttr = new Long(9);
    private Integer unsignedShortAttr = new Integer(10);
    private Short unsignedByteAttr = new Short((short)11);
    

    public byte[] getBase64BinaryAttr() {
        return base64BinaryAttr;
    }

    public void setBase64BinaryAttr(byte[] base64BinaryAttr) {
        this.base64BinaryAttr = base64BinaryAttr;
    }

    public Boolean getBooleanAttr() {
        return booleanAttr;
    }

    public void setBooleanAttr(Boolean booleanAttr) {
        this.booleanAttr = booleanAttr;
    }

    public Byte getByteAttr() {
        return byteAttr;
    }

    public void setByteAttr(Byte byteAttr) {
        this.byteAttr = byteAttr;
    }

    public BigDecimal getDecimalAttr() {
        return decimalAttr;
    }

    public void setDecimalAttr(BigDecimal decimalAttr) {
        this.decimalAttr = decimalAttr;
    }

    public Double getDoubleAttr() {
        return doubleAttr;
    }

    public void setDoubleAttr(Double doubleAttr) {
        this.doubleAttr = doubleAttr;
    }

    public Float getFloatAttr() {
        return floatAttr;
    }

    public void setFloatAttr(Float floatAttr) {
        this.floatAttr = floatAttr;
    }

    public byte[] getHexBinaryAttr() {
        return hexBinaryAttr;
    }

    public void setHexBinaryAttr(byte[] hexBinaryAttr) {
        this.hexBinaryAttr = hexBinaryAttr;
    }

    public Integer getIntAttr() {
        return intAttr;
    }

    public void setIntAttr(Integer intAttr) {
        this.intAttr = intAttr;
    }

    public BigInteger getIntegerAttr() {
        return integerAttr;
    }

    public void setIntegerAttr(BigInteger integerAttr) {
        this.integerAttr = integerAttr;
    }

    public Long getLongAttr() {
        return longAttr;
    }

    public void setLongAttr(Long longAttr) {
        this.longAttr = longAttr;
    }

    public QName getQnameAttr() {
        return qnameAttr;
    }

    public void setQnameAttr(QName qnameAttr) {
        this.qnameAttr = qnameAttr;
    }

    public Short getShortAttr() {
        return shortAttr;
    }

    public void setShortAttr(Short shortAttr) {
        this.shortAttr = shortAttr;
    }

    public String getStringAttr() {
        return stringAttr;
    }

    public void setStringAttr(String stringAttr) {
        this.stringAttr = stringAttr;
    }

    public Short getUnsignedByteAttr() {
        return unsignedByteAttr;
    }

    public void setUnsignedByteAttr(Short unsignedByteAttr) {
        this.unsignedByteAttr = unsignedByteAttr;
    }

    public Long getUnsignedIntAttr() {
        return unsignedIntAttr;
    }

    public void setUnsignedIntAttr(Long unsignedIntAttr) {
        this.unsignedIntAttr = unsignedIntAttr;
    }

    public Integer getUnsignedShortAttr() {
        return unsignedShortAttr;
    }

    public void setUnsignedShortAttr(Integer unsignedShortAttr) {
        this.unsignedShortAttr = unsignedShortAttr;
    }
    public void setConfigureName(String beanName) {
        this.configureName = beanName;
    }    
    
     class TTestBean extends ConfiguredBean{

        /**
         * @param configureName
         */
        public TTestBean(String configureName)
        {
            super(configureName);
        }
        
    }
}
