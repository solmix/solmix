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
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月19日
 */

public class CustomKeyStoreSpi extends KeyStoreSpi
{
    private final Logger log = LoggerFactory.getLogger(CustomKeyStoreSpi.class);
    public static final String PRIVATE_KEY_ENTRY = "PrivateKeyEntry";
    public static final String TRUSTED_CERT_ENTRY = "trustedCertEntry";
    public static final String SECRET_KEY_ENTRY = "SecretKeyEntry";
    
    private final CustomKeystoreManager customKeystoreManager;
    private final Map<String, Object[]> engineAliases = new HashMap<String, Object[]>();

    public CustomKeyStoreSpi(CustomKeystoreManager customKeystoreManager) {
        this.customKeystoreManager = customKeystoreManager;
    }
    
    @Override
    public Key engineGetKey(String alias, char[] password)
    throws NoSuchAlgorithmException, UnrecoverableKeyException {
        Object[] objs = engineAliases.get(alias);
        if (objs == null) {
            log.warn("alias=" + alias + " has no associated certificate");
            return null;
        }
        Certificate cert = (Certificate) objs[1];
        return cert.getPublicKey();
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        Object[] objs = engineAliases.get(alias);
        return (Certificate[]) objs[2];
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        Object[] objs = engineAliases.get(alias);
        return (Certificate) objs[1];
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        throw new UnsupportedOperationException("engineGetCreationDate() is not supported");
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain)
    throws KeyStoreException {
        throw new UnsupportedOperationException("engineSetKeyEntry() is not supported");
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain)
    throws KeyStoreException {
        throw new UnsupportedOperationException("engineSetKeyEntry() is not supported");
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert)
    throws KeyStoreException {
        customKeystoreManager.create(alias, TRUSTED_CERT_ENTRY, cert, null);
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        throw new UnsupportedOperationException("engineDeleteEntry() is not supported");
    }

    @Override
    public Enumeration<String> engineAliases() {
        return new Vector<String>(engineAliases.keySet()).elements();
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        return engineAliases.containsKey(alias);
    }

    @Override
    public int engineSize() {
        return engineAliases.size();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        Object[] objs = engineAliases.get(alias);
        if (objs == null) {
            return false;
        }
        String type = (String) objs[0];
        return (type.equals(PRIVATE_KEY_ENTRY) || type.equals(SECRET_KEY_ENTRY));
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        Object[] objs = engineAliases.get(alias);
        if (objs == null) {
            return false;
        }
        String type = (String) objs[0];
        return type.equals(TRUSTED_CERT_ENTRY);
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        throw new UnsupportedOperationException("engineGetCertificateAlias() is not supported");
    }

    @Override
    public void engineStore(OutputStream stream, char[] password)
    throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException("engineStore() is not supported");
    }

    @Override
    public void engineLoad(InputStream stream, char[] password)
    throws IOException, NoSuchAlgorithmException, CertificateException {
        final Collection<? extends KeystoreEntry> entries = customKeystoreManager.getKeystore();
        final boolean debug = log.isDebugEnabled();
        for (final KeystoreEntry entry : entries) {
            final String alias = entry.getAlias();
            final String type = entry.getType();
            final Certificate cert = entry.getCertificate();
            final Certificate[] chain = entry.getCertificateChain();
            Object[] objs = engineAliases.get(alias);
            if (objs == null) {
                objs = new Object[3];
                objs[0] = type;
                objs[1] = cert;
                objs[2] = chain;
                engineAliases.put(alias, objs);
            }
            if (debug) log.debug("adding alias=" + alias + ",type=" + type);
        }
    }

}
