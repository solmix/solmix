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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年5月4日
 */

public class KeystoreUtils
{

    public static final KeyStore loadKeyStore(final String ksFilePath, final char[] ksPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        java.io.FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(ksFilePath));
            return loadKeyStore(fis, ksPassword);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static final KeyStore loadKeyStore(final InputStream is, final char[] ksPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(is, ksPassword);
        return ks;
    }

    public static final byte[] keyStoreToByteArray(final KeyStore ks, final char[] ksPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        ByteArrayOutputStream bos = null;
        bos = new ByteArrayOutputStream();
        ks.store(bos, ksPassword);
        return bos.toByteArray();
    }

    public static final byte[] loadKeystore(final String ksFilePath)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        java.io.FileInputStream fis = null;
        try {
            final File keystoreFile = new File(ksFilePath);
            fis = new FileInputStream(keystoreFile);

            final byte[] arrContent = new byte[(int) keystoreFile.length()];
            fis.read(arrContent);
            return arrContent;

        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static final KeyStore loadKeyStore(final byte[] keystoreFileContent, char[] ksPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        final ByteArrayInputStream bis = new ByteArrayInputStream(keystoreFileContent);
        return loadKeyStore(bis, ksPassword);
    }

    public static final void persistKeyStore(final KeyStore ks, final String ksFilePath, final char[] ksPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        FileOutputStream fos = null;
        try {
            final File file = new File(ksFilePath);
            fos = new FileOutputStream(file);
            ks.store(fos, ksPassword);
        } finally {
            if (fos != null)
                fos.close();
        }
    }
}
