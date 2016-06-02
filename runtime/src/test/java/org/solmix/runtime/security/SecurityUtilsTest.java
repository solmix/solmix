/**
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
package org.solmix.runtime.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;
import org.solmix.commons.util.SecurityUtils;
import org.solmix.commons.util.TokenUtils;

import junit.framework.Assert;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月28日
 */

public class SecurityUtilsTest
{

    @Test
    public void isMarkedEncrypted() {
      assertTrue(SecurityUtils.isMarkedEncrypted("ENC(AA==)"));
      assertFalse(SecurityUtils.isMarkedEncrypted("ENC(AA==)ss"));
    }
    

    @Test
    public void unmark() {
        assertEquals("AA==",SecurityUtils.unmark("ENC(AA==)"));
        assertEquals("ENC(AAddd)",SecurityUtils.unmark("ENC(ENC(AAddd))"));
    }
    
    @Test
    public void unmarkRecursive() {
        assertEquals("AAddd",SecurityUtils.unmarkRecursive("ENC(ENC(AAddd))"));
    }
    
    @Test
    public void mark() {
        assertEquals("ENC(AA==)",SecurityUtils.mark("AA=="));
    }
    @Test
    public void getStandardPBEStringEncryptor() {
        StandardPBEStringEncryptor pbe=   SecurityUtils.getStandardPBEStringEncryptor("password");
        Assert.assertNotNull(pbe);
        pbe.encrypt("sdfsdfse");
    }
    @Test
    public void encrypt() throws Exception{
        StringEncryptor encryptor= createEncryptor("gdw");
        String token  =TokenUtils.generateRandomToken();
       String encrypted = SecurityUtils.encrypt(encryptor,token);
       assertEquals(token,SecurityUtils.decrypt(encryptor, encrypted));
    }
    @Test
    public void encrypt2() {
        String encrypted =    SecurityUtils.encrypt(SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM, "asdfe", "sdfewew!!2x");
        assertEquals("sdfewew!!2x",SecurityUtils.decrypt(SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM, "asdfe",encrypted));
    }
    @Test
    public void decryptRecursiveUnmark() throws Exception{
        StringEncryptor encryptor= createEncryptor("23411");
        String encrypted = SecurityUtils.encrypt(encryptor,"sdfe");
        assertEquals("sdfe",SecurityUtils.decryptRecursiveUnmark(encryptor, "ENC("+encrypted+")"));
    }
   
    protected PooledPBEStringEncryptor createEncryptor(String password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(1);
        encryptor.setAlgorithm(SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM);
        encryptor.setPassword(password);
        return encryptor; 
    }
}
