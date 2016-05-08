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
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.Enumeration;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solmix.runtime.support.ExContainerSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年5月4日
 */

public class KeystoreUtilsTest extends ExContainerSupport
{

    private static final String KEYSTORE_RELATIVE_PATH = "solmix.jks";

    private final char[] KEYSTORE_PASSWORD = "solmixpwd".toCharArray();

    private static String ksFileDir;

    private static String ksFullyqualifiedPath;

    private KeyStore keystore;

    @BeforeClass
    public static final void setup() {
        final URL keystorePathUrl = KeystoreUtilsTest.class.getResource(KEYSTORE_RELATIVE_PATH);
        ksFullyqualifiedPath = keystorePathUrl.getPath();
        ksFileDir = new File(ksFullyqualifiedPath).getParent();
    }

    @Before
    public void methodSetup() {
        try {
            this.keystore = KeystoreUtils.loadKeyStore(ksFullyqualifiedPath, KEYSTORE_PASSWORD);
        } catch (Throwable t) {
            fail("Keystore load failure.");
            throw new RuntimeException(t);
        }
    }

    @After
    public final void teardown() {
        keystore = null;
    }

    @Test
    public void test_loadKeystore() throws Throwable {
        byte[] keystoreFileContent = KeystoreUtils.loadKeystore(ksFullyqualifiedPath);

        final KeyStore convertedKeystore = KeystoreUtils.loadKeyStore(keystoreFileContent, KEYSTORE_PASSWORD);
        compareKeystores(this.keystore, convertedKeystore, "Byte[] converted keytstore is not the same as standardly loaded one");
    }

    @Test
    public void testPersistKeyStoretoFile() {

        final String sNewKeystoreFullyQualifiedPath = ksFileDir + "/persisted.version";
        KeyStore origKeystore = this.keystore;

        final File persitedKeystoreFile = new File(sNewKeystoreFullyQualifiedPath);
        if (persitedKeystoreFile.exists()) {
            persitedKeystoreFile.delete();
        }

        try {

            try {
                KeystoreUtils.persistKeyStore(origKeystore, sNewKeystoreFullyQualifiedPath, KEYSTORE_PASSWORD);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }

            try {
                final KeyStore persitedKeystore = KeystoreUtils.loadKeyStore(sNewKeystoreFullyQualifiedPath, KEYSTORE_PASSWORD);

                this.compareKeystores(origKeystore, persitedKeystore, "Persited Keystore is not the same as original one");

            } catch (Throwable t) {
                throw new RuntimeException(t);
            }

        } finally {
            if (persitedKeystoreFile.exists()) {
                persitedKeystoreFile.delete();
            }
        }
    }

    @Test
    public void TestConvertKeyStoreToByteArray() {

        byte[] keystoreFileContent = null;
        try {

            keystoreFileContent = KeystoreUtils.keyStoreToByteArray(this.keystore, KEYSTORE_PASSWORD);
        } catch (Throwable t) {
            fail("Failed to convert to byte[].");
            throw new RuntimeException(t);
        }

        try {
            final KeyStore convetedKeystore = KeystoreUtils.loadKeyStore(keystoreFileContent, KEYSTORE_PASSWORD);
            this.compareKeystores(this.keystore, convetedKeystore, "converted Keystore is not the same as original one");
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    private final void compareKeystores(final KeyStore templateKeystore, final KeyStore targetKeystore, final String errorMsg) throws Throwable {
        if (templateKeystore == null && targetKeystore != null) {
            fail("Template keystore is null but target is not");
        } else if (templateKeystore != null && targetKeystore == null) {
            fail("target keystore is null but template is not");
        } // EO if target keystore is null but template is not

        final Enumeration<String> aliasesEnumeration = templateKeystore.aliases();
        String alias = null;

        final PasswordProtection passwordMetadata = new PasswordProtection(KEYSTORE_PASSWORD);
        PrivateKeyEntry templateKeystoreEntry = null, targetKeystoreEntry = null;
        java.security.cert.Certificate templateCertificate = null, targetCertificate = null;

        while (aliasesEnumeration.hasMoreElements()) {
            alias = aliasesEnumeration.nextElement();
            if (templateKeystore.isKeyEntry(alias)) {
                templateKeystoreEntry = (PrivateKeyEntry) templateKeystore.getEntry(alias, passwordMetadata);
                templateCertificate = templateKeystoreEntry.getCertificate();

                targetKeystoreEntry = (PrivateKeyEntry) targetKeystore.getEntry(alias, passwordMetadata);

                if (targetKeystoreEntry == null) {
                    fail("Could not find key entry for alias " + alias + " in target keystore");
                }

                targetCertificate = targetKeystore.getCertificate(alias);
            } else {
                templateCertificate = templateKeystore.getCertificate(alias);
                targetCertificate = targetKeystore.getCertificate(alias);
            }

            assertEquals(errorMsg + "with error:\n\t Target certificate with alias " + alias + " is not the same as template one",
                templateCertificate, targetCertificate);

        }

    }
}
