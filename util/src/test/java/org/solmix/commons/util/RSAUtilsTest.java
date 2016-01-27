/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年10月26日
 */

public class RSAUtilsTest extends Assert
{
    
    static String publicKey;
    static String privateKey;
 
    @Before
    public  void setup() throws Exception{
        Map<String, Object> keyMap = RSAUtils.genKeyPair();
        publicKey = RSAUtils.getPublicKey(keyMap);
        privateKey = RSAUtils.getPrivateKey(keyMap);
        System.err.println("公钥: \n\r" + publicKey);
        System.err.println("私钥： \n\r" + privateKey);
    }
    
    @Test
    public void generationKey()throws Exception{
        privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIL1Vub2R41nA2nxjk414023rooV2KDulI0IX12+/cU5RJoCkln6uB5B8xSh6NGWo22Fl5IfcAflL+g8FV5FgglL1tXWXMbiDYD1zvs2Xrf77hWV0Xn6InNS3Ijrfxw1AggUc1mXehY5/Le9CPGszRkQmWeh6VtApG3UM/EkdIh9AgMBAAECgYEAgGmggDNIsFAt2cniV7ChpciSXpbTZ+LqSWzHTr6ESstACKCy74ZY2lqiyD2HdVT4BeH0YXVwPl2u31NjRKB1w8zcyAKvYXrXKGp7OVByz/YMVcIijtui/8MLrRiAxe69OiDRhE9lA7io+gnYUKLnyv7HJDjQzIvwz/1qJS9iqKUCQQDK7Rvu4ZC54YwAYnVRnyh6geJ1DaGYd09BMIAlrR4Poc9f1Ej+RJiJX3wlHHzveLC7TSHF/D3B3WIxelp3NvovAkEApTWif6Bi2S+MkKvgIMf02H3eLd+2+VmK6QV0SUTFdwH9QPEv1Z+WJpMckM+8cavAxYAUemo0J7fEvPLa16y5EwJBAJA0JyF+kcZGDaNIVG6IV8+W9UKRSUB7qIp+2NHtT+tz5VYIGUb3oB4fCK2mrPHQJmczzMhRE+HsXJckh50oKGkCQF9RlrP0IZQVbxB5WhMPyyCtXmcxUCyFkTPoxbMQTq3fI/M4NNUYAlW3Qx/5+0vKQKqyvx3x8K2JxaUOqV0OdqkCQALxlO+Sls96MOo/OaBsCRLL0yQf49+Kcc7RhSSem5rmhL3lXgmpHMFsg5dxB9SSdPVy/+hpHigr59Uk0+7Vrc4=";
        String source = "2015-10-1~2016-02-01:1/10.176.34.59";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println("加密后：\r\n" + Base64Utils.encode(encodedData));
    }
   
    @Test
    public void test() throws Exception{
        System.err.println("私钥加密——公钥解密");
        String source = "这是一行测试RSA数字签名的无意义文字";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println("加密后：\r\n" + Base64Utils.encode(encodedData));
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
        System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encodedData, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        System.err.println("验证结果:\r" + status);
    }

}
