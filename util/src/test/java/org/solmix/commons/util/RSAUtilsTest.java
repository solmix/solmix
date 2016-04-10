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
        privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAILmsCwCl76KdS7OryipI38ew9oY4RpDqsd2seAq0evuS8U8EGRIxQVd0YkbC9PeHt5F8fSrZivbg6s1+dhlAfe03tTFsW2YH9QtsGMLOEKv2Ihln80mM2UnrzY8IiuURD3K2/dqTDrTumICyrt1klmYJ5u5JXTx2izKDL/JvKKlAgMBAAECgYBRNfOoajdgdB/9WSccT8sA68JQRc0p8T87nmz+iTJRcDa79+angOoSyUDdEdWFrTFzbuuMguXRYc/PYZ5O3WOZObifoHvaGP/hDW7S8+oVuK1IFEnveM+33r2EzceAnpKR4FtfSWq7A4ziEoY1PujpxZWTBDD/dzCdRvwgRK+7QQJBAME+wyCNkBOu5BKYnIloMS9f0KSDDkk/srca/fBUEjSZbZhRUvETRXEoYeOjxuTf2hvFjiGNAhSHx/0OOia13zUCQQCtaQS5diDx7Vh26x6ugn6MZYwUb+Bvt35+HqvjLzD9k5PELDaZQf9MxfWsceny79ttWgWFloyj6XanOGokZvOxAkEAq44RYmPqhV7dARlU1rOV/q28J2BlnWecO+wNhn7MTr/qyK9hx71JB8VG6fWqi+Oi2MbQgD6TmzBTvfcUbutFBQJAL3gUBwDDO/aQxNzP5U1rfts9YUrO0UYVpkiXHPWKH6AKTyUbPRDH5ig6fB4iwJHQKzr9T/hKP4RlKplS1OwpwQJAZVYVCwYuNiWv45fugTZzOD6viDPh/Pbd3cC/BYaw4TdnBXV6KzNQjsghd5vXsgyVbt4kCDn37Cj90KVriuuQqg==";
        String source = "jdbc:oracle:thin:@10.176.162.76:1521:empt";
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
