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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.exec.Execute;
import org.solmix.commons.exec.ExecuteWatchdog;
import org.solmix.commons.exec.PumpStreamHandler;
import org.solmix.commons.timer.StopWatch;
import org.solmix.commons.util.FileUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年4月19日
 */

public class KeystoreManager
{

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreManager.class);

    private final AtomicBoolean isDB = new AtomicBoolean(false);

    private static KeystoreManager keystoreManager = new KeystoreManager();
    @Resource
    private Container container;

    public static KeystoreManager getKeystoreManager() {
        return keystoreManager;
    }

    public KeyStore getKeyStore(KeystoreInfo keystoreConfig) throws KeyStoreException, IOException {
        FileInputStream keyStoreFileInputStream = null;
        String filePath = keystoreConfig.getFilePath();
        String filePassword = keystoreConfig.getFilePassword();

        // check if keystoreConfig valid (block if it's null or "")
        String errorMsg = "";
        if (keystoreConfig.getAlias() == null) {
            errorMsg += " alias is null. ";
        }
        if (keystoreConfig.getFilePath() == null) {
            errorMsg += " filePath is null. ";
        }
        if (keystoreConfig.getFilePassword() == null) {
            errorMsg += " password is null. ";
        }
        if (!"".equals(errorMsg)) {
            throw new KeyStoreException(errorMsg);
        }

        try {
            KeyStore keystore = CustomKeyStore.getInstance(KeyStore.getDefaultType(), isDB,container);
            File file = new File(filePath);
            char[] password =  filePassword.toCharArray();;

            if (!file.exists()) {
                //throw IOException
                if (!StringUtils.isBlank(filePath) && !keystoreConfig.isDefault()) {
                    throw new IOException("User specified keystore [" + filePath + "] does not exist.");
                }

                
                createInternalKeystore(keystoreConfig);
                FileUtils.setReadWriteOnlyByOwner(file);
            }

            // ...keystore exist, so init the file input stream...
            keyStoreFileInputStream = new FileInputStream(file);
            //加载仓库文件
            keystore.load(keyStoreFileInputStream, password);

            return keystore;
        } catch (NoSuchAlgorithmException e) {
            errorMsg = "The algorithm used to check the integrity of the keystore cannot be found.";
            throw new KeyStoreException(errorMsg, e);
        } catch (CertificateException e) {
            errorMsg = "Keystore cannot be loaded. One possibility is that the password is incorrect.";
            throw new KeyStoreException(errorMsg, e);
        } finally {
            if (keyStoreFileInputStream != null) {
                keyStoreFileInputStream.close();
                keyStoreFileInputStream = null;
            }
        }
    }

    private String getDName(KeystoreInfo keystoreConfig) {
        return "CN=" + keystoreConfig.getKeyCN() + " (HOMO Self-Signed Cert), OU=HOMO, O=homo.solmix.org, L=Unknown, ST=Unknown, C=CN";
    }

    private void createInternalKeystore(KeystoreInfo keystoreConfig) throws KeyStoreException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String javaHome = System.getProperty("java.home");
        String keytool = javaHome + File.separator + "bin" + File.separator + "keytool";
        String filePath = keystoreConfig.getFilePath();
        File file = new File(filePath);
        File parent  = file.getParentFile();
        if(parent!=null&&!parent.exists()){
        	parent.mkdir();
        }
        String[] args = { keytool, "-genkey", "-dname", getDName(keystoreConfig), "-alias", keystoreConfig.getAlias(), "-keystore",
        		filePath, "-storepass", keystoreConfig.getFilePassword(), "-keypass", keystoreConfig.getFilePassword(), "-keyalg",
            "RSA", "-validity", "3650" // 10 years
        };

        int timeout = 5 * 60 * 1000; // 5min
        ExecuteWatchdog wdog = new ExecuteWatchdog(timeout);
        Execute exec = new Execute(new PumpStreamHandler(output), wdog);

        exec.setCommandline(args);

        LOG.debug("Generating keystore: {}", keystoreConfig.getFilePath());

        int rc;

        try {
            rc = exec.execute();
        } catch (Exception e) {
            rc = -1;
            LOG.error(e.getMessage(), e);
        }

        if (rc != 0) {
            String msg = output.toString().trim();

            if (msg.length() == 0) {
                msg = "timeout after " + timeout + "ms";
            }

            if (!msg.toLowerCase().contains("key pair not generated, alias <" + keystoreConfig.getAlias().toLowerCase() + "> already exists")) {
                // can't have password in log
                throw new KeyStoreException("Failed to create keystore:" + keystoreConfig.getAlias() + ", " + msg);
            }
        }
    }

    public X509TrustManager getCustomTrustManager(X509TrustManager defaultTrustManager, KeystoreInfo keystoreConfig,
        boolean acceptUnverifiedCertificates, KeyStore trustStore) {
        return new CustomTrustManager(defaultTrustManager, keystoreConfig, acceptUnverifiedCertificates, trustStore, isDB.get());
    }
    
    private class CustomTrustManager implements X509TrustManager {
        private final Logger log = LoggerFactory.getLogger(X509TrustManager.class);
        private final X509TrustManager defaultTrustManager;
        private final KeystoreInfo keystoreConfig;
        private final boolean acceptUnverifiedCertificates;
        private final KeyStore trustStore;
        private final boolean isDB;
        private CustomTrustManager(X509TrustManager defaultTrustManager,
            KeystoreInfo keystoreConfig,
                                   boolean acceptUnverifiedCertificates,
                                   KeyStore trustStore, boolean isDB) {
            this.defaultTrustManager = defaultTrustManager;
            this.keystoreConfig = keystoreConfig;
            this.acceptUnverifiedCertificates = acceptUnverifiedCertificates;
            this.trustStore = trustStore;
            this.isDB = isDB;
        }
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return defaultTrustManager.getAcceptedIssuers();
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException e){
                CertificateExpiredException expiredCertException = getCertExpiredException(e);
                if (expiredCertException!=null){
                    log.error("Fail the connection because received certificate is expired. " +
                            "Please update the certificate.",expiredCertException);
                    throw new CertificateException(e);
                }
                if (acceptUnverifiedCertificates) {
                    log.info("Import the certification. (Received certificate is not trusted by keystore)");
                    importCertificate(chain);
                } else {
                    log.warn("Fail the connection because received certificate is not trusted by " +
                             "keystore: alias=" + keystoreConfig.getAlias());
                    log.debug("Fail the connection because received certificate is not trusted by " +
                              "keystore: alias=" + keystoreConfig.getAlias() +
                              ", acceptUnverifiedCertificates="+acceptUnverifiedCertificates,e);
                    throw new CertificateException(e);
                }
            }
        }
        private CertificateExpiredException getCertExpiredException(Exception e){  
            while (e !=null){
                if (e instanceof CertificateExpiredException){
                    return (CertificateExpiredException)e;
                }
                e = (Exception) e.getCause();
            }
            return null;
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
            defaultTrustManager.checkClientTrusted(chain, authType);
        }
        
        private void importCertificate(X509Certificate[] chain)
            throws CertificateException {
            FileOutputStream ksFileOutputStream = null;
            final boolean debug = log.isDebugEnabled();
            final StopWatch watch = new StopWatch();
            try {
                for (X509Certificate cert : chain) {
                    String[] cnValues = getCNs(cert);
                    String alias = (cnValues != null && cnValues.length > 0) ? cnValues[0] : "UnknownCN";
                    alias += "-ts=" + System.currentTimeMillis();
                    trustStore.setCertificateEntry(alias, cert);
                }
                if (!isDB) {
                    ksFileOutputStream = new FileOutputStream(keystoreConfig.getFilePath());
                    trustStore.store(ksFileOutputStream, keystoreConfig.getFilePassword().toCharArray());
                }
            } catch (FileNotFoundException e) {
                // Can't find the keystore in the path
                log.error("Can't find the keystore in " + keystoreConfig.getFilePath() +
                          ". Error message: " + e, e);
            } catch (NoSuchAlgorithmException e) {
                log.error("The algorithm is not supported. Error message: " + e, e);
            } catch (Exception e) {
                // expect KeyStoreException, IOException
                log.error("Exception when trying to import certificate: " + e, e);
            } finally {
                close(ksFileOutputStream);
                ksFileOutputStream = null;
                if (debug) log.debug("importCert: " + watch);
            }
        }
        public String[] getCNs(final X509Certificate cert) {
            final String subjectPrincipal = cert.getSubjectX500Principal().toString();
            try {
                final String cn = extractCN(subjectPrincipal);
                return cn != null ? new String[] { cn } : null;
            } catch (final SSLException ex) {
                return null;
            }
        }
        String extractCN(final String subjectPrincipal) throws SSLException {
            if (subjectPrincipal == null) {
                return null;
            }
            try {
                final LdapName subjectDN = new LdapName(subjectPrincipal);
                final List<Rdn> rdns = subjectDN.getRdns();
                for (int i = rdns.size() - 1; i >= 0; i--) {
                    final Rdn rds = rdns.get(i);
                    final Attributes attributes = rds.toAttributes();
                    final Attribute cn = attributes.get("cn");
                    if (cn != null) {
                        try {
                            final Object value = cn.get();
                            if (value != null) {
                                return value.toString();
                            }
                        } catch (final NoSuchElementException ignore) {
                        } catch (final NamingException ignore) {
                        }
                    }
                }
                return null;
            } catch (final InvalidNameException e) {
                throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
            }
        }


        private void close(FileOutputStream fos) {
            if (fos == null) {
                return;
            }
            try {
                fos.close();
            } catch (IOException e) {}
        }
    }
}
