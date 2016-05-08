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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.solmix.runtime.Container;
import org.solmix.runtime.Containers;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年5月4日
 */

public class DefaultSSLProvider implements SSLProvider
{

    private SSLContext sslContext;

    public DefaultSSLProvider(Container container, KeystoreInfo keystoreInfo, boolean acceptUnverifiedCertificates)
    {
        try {
            KeystoreManager keystoreMgr = Containers.createExtensionIfNoProvided(container, KeystoreManager.class);
            KeyStore trustStore = keystoreMgr.getKeyStore(keystoreInfo);
            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(trustStore, keystoreInfo.getFilePassword());
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(trustStore);
            X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
            X509TrustManager customTrustManager = keystoreMgr.getCustomTrustManager(defaultTrustManager, keystoreInfo, acceptUnverifiedCertificates,
                trustStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { customTrustManager }, new SecureRandom());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    private KeyManagerFactory getKeyManagerFactory(final KeyStore keystore, final String password) throws KeyStoreException {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, password.toCharArray());
            return keyManagerFactory;
        } catch (NoSuchAlgorithmException e) {
            throw new KeyStoreException("The algorithm is not supported: " + e, e);
        } catch (UnrecoverableKeyException e) {
            throw new KeyStoreException("Password for the keystore is invalid: " + e, e);
        }
    }

    private TrustManagerFactory getTrustManagerFactory(final KeyStore keystore) throws KeyStoreException, IOException {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);
            return trustManagerFactory;
        } catch (NoSuchAlgorithmException e) {
            throw new KeyStoreException(e);
        }
    }
    @Override
    public SSLContext getSSLContext() {
        return sslContext;
    }


}
