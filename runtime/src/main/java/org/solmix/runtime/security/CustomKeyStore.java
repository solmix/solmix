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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.Provider;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;

import org.solmix.runtime.Container;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年4月19日
 */

public class CustomKeyStore extends KeyStore
{

    protected CustomKeyStore(KeyStoreSpi keyStoreSpi, Provider provider, String type)
    {
        super(keyStoreSpi, provider, type);
    }

    public static KeyStore getInstance(String type, AtomicBoolean isDb, Container container) throws KeyStoreException {
        CustomKeystoreManager customKeystoreManager = container.getExtension(CustomKeystoreManager.class);
        if (customKeystoreManager == null) {
            isDb.set(false);
            return KeyStore.getInstance(type);
        }
        try {
            final CustomKeyStoreSpi dbKeyStoreSpi = new CustomKeyStoreSpi(customKeystoreManager);
            final Provider[] providers = Security.getProviders("KeyStore." + type);
            isDb.set(true);
            return new CustomKeyStore(dbKeyStoreSpi, providers[0], type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
