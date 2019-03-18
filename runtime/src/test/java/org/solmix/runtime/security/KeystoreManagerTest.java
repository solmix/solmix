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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;
import org.solmix.runtime.Containers;
import org.solmix.runtime.support.ExContainerSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年5月4日
 */

public class KeystoreManagerTest extends ExContainerSupport
{

    @Test
    public void test() throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException {
        KeystoreInfo ki = new KeystoreInfo("solmix", "target/solmix.jks/", "solmixpwd", true);
        KeystoreManager sm = Containers.createExtensionIfNoProvided(container, KeystoreManager.class);
        KeyStore ks = sm.getKeyStore(ki);
        assertNotNull(ks);
        File file = new File("target/solmix.jks/");
        assertTrue(file.exists());
        if (file.exists()) {
            file.delete();
        }
        KeyStore.Entry entry = ks.getEntry(ki.getAlias(), new KeyStore.PasswordProtection(ki.getFilePassword().toCharArray()));
        assertTrue(entry != null);

        byte[] pk = ((PrivateKeyEntry) entry).getPrivateKey().getEncoded();
        ByteBuffer encryptionKey = Charset.forName("US-ASCII").encode(ByteBuffer.wrap(pk).toString());
        String key = encryptionKey.toString();
        assertEquals("java.nio.HeapByteBuffer[pos=0 lim=48 cap=48]", key);

    }

    @Test
    public void test_failed() {
        KeystoreInfo ki = new KeystoreInfo("solmix", "target/solmix2.jks/", "solmixpwd", false);
        KeystoreManager sm = Containers.createExtensionIfNoProvided(container, KeystoreManager.class);
        KeyStore ks = null;
        try {
            ks = sm.getKeyStore(ki);
            File file = new File(ki.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            fail("should get ioException");
        } catch (KeyStoreException e) {
            fail("should get ioException");
        } catch (IOException e) {
            assertTrue("Should get IOException", true);
        }
        assertNull(ks);
    }

    @Test
    public void test_getCustomTrustManager() throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreInfo ki = new KeystoreInfo("solmix", "target/solmix.jks/", "solmixpwd", true);
        KeystoreManager sm = Containers.createExtensionIfNoProvided(container, KeystoreManager.class);
        KeyStore ks = sm.getKeyStore(ki);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(ks, ki.getFilePasswordCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ks);

        X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        X509TrustManager customTrustManager = sm.getCustomTrustManager(defaultTrustManager, ki, false, ks);
        assertNotNull(customTrustManager);

    }

}
