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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.solmix.commons.util.FileUtils;
import org.solmix.commons.util.PropertyUtils;
import org.solmix.commons.util.TokenUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年4月15日
 */

public class SecurityPropertyUtils
{

    private static final String ENCRYPTION_KEY_PROP = "k";

    private static final String KEY_ENCRYPTION_KEY = "Security solmix encry";

    private static final String LOCK_FILE_NAME = System.getProperty("java.io.tmpdir") + "/agent.encrypt.lock";

    public static void storeProperties(String file, String propEncKey, Map<String, String> entriesToStore) {

        Map<String, String> encryptedEntriesToStore = new HashMap<String, String>();
        for (Map.Entry<String, String> entryToStore : entriesToStore.entrySet()) {
            String encryptedVal = SecurityUtils.encrypt(SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM, propEncKey, entryToStore.getValue());
            encryptedEntriesToStore.put(entryToStore.getKey(), encryptedVal);
        }
        PropertyUtils.storeProperties(file, encryptedEntriesToStore);
    }

    public static synchronized String getPropertyEncryptionKey(String fileName) {

        if (fileName == null || fileName.trim().length() < 1) {
            throw new IllegalArgumentException("Illegal Argument: fileName [" + fileName + "]");
        }

        File encryptionKeyFile = new File(fileName);

        if (!encryptionKeyFile.exists()) {
            throw new IllegalArgumentException("The encryption key file [" + fileName + "] doesn't exist");
        }

        String encryptionKey;
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(encryptionKeyFile));

            String encryptedKey = props.getProperty(ENCRYPTION_KEY_PROP);

            if (encryptedKey != null) {
                encryptionKey = SecurityUtils.decrypt(SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM, KEY_ENCRYPTION_KEY, encryptedKey);
            } else {
                throw new IllegalArgumentException("Invalid properties encryption key");
            }
        } catch (Exception exc) {
            throw new IllegalArgumentException(exc);
        }

        return encryptionKey;
    }

    public static synchronized void ensurePropertiesEncryption(String propsFileName, String encryptionKeyFileName, Set<String> secureProps) {

        int tries = 10;
        while (!lock() && tries > 0) {
            tries--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                /* ignore */ }
        }
        if (tries <= 0) {
            throw new IllegalStateException(LOCK_FILE_NAME + " is locked. can't continue.");
        }

        try {

            if (propsFileName == null || propsFileName.trim().length() < 1) {
                throw new IllegalArgumentException("Illegal Argument: propsFileName [" + propsFileName + "]");
            }

            if (encryptionKeyFileName == null || encryptionKeyFileName.trim().length() < 1) {
                throw new IllegalArgumentException("Illegal Argument: encryptionKeyFileName [" + encryptionKeyFileName + "]");
            }

            if (secureProps == null || secureProps.size() < 1) {
                return;
            }

            Properties props = PropertyUtils.loadProperties(propsFileName);

            boolean alreadyEncrypted = isAlreadyEncrypted(props);

            File encryptionKeyFile = new File(encryptionKeyFileName);

            String encryptionKey;

            if (encryptionKeyFile.exists()) {
                encryptionKey = getPropertyEncryptionKey(encryptionKeyFileName);
            } else {
                if (alreadyEncrypted) {
                    // The encryption key is new, but the properties are already encrypted.
                    throw new IllegalStateException("The properties are already encrypted, but the encryption key is missing");
                }
                encryptionKey = createAndStorePropertyEncryptionKey(encryptionKeyFileName);
            }

            // Collect all the properties that should be encrypted but still aren't
            Map<String, String> unEncProps = new HashMap<String, String>();
            for (Enumeration<?> propKeys = props.propertyNames(); propKeys.hasMoreElements();) {
                String key = (String) propKeys.nextElement();
                String value = props.getProperty(key);

                if (value != null && secureProps.contains(key) && !SecurityUtils.isMarkedEncrypted(value)) {
                    unEncProps.put(key, value);
                }
            }

            // Encrypt secure properties.
            if (unEncProps.size() > 0) {
                storeProperties(propsFileName, encryptionKey, unEncProps);
            }
        } finally {
            unlock(false);
        }
    }
    
    static synchronized String createAndStorePropertyEncryptionKey(String fileName) {
        if (fileName == null || fileName.trim().length() < 1) {
            throw new IllegalArgumentException("Illegal Argument: fileName [" + fileName + "]");
        }

        File encryptionKeyFile = new File(fileName);

        if (encryptionKeyFile.exists()) {
            throw new IllegalArgumentException("Attempt to override an encryption key file [" + fileName + "]");
        }

        String encryptionKey;
        try {
            encryptionKey = TokenUtils.generateRandomToken();
            String encryptedKey = SecurityUtils.encrypt(
                    SecurityUtils.DEFAULT_ENCRYPTION_ALGORITHM, KEY_ENCRYPTION_KEY, encryptionKey);
            Properties props = new Properties();
            props.put(ENCRYPTION_KEY_PROP, encryptedKey);
            
            props.store(new FileOutputStream(fileName), null);
            
            // set read/write permissions to be given to the owner only
            File encKeyFile = new File(fileName);
            FileUtils.setReadWriteOnlyByOwner(encKeyFile);
        } catch (Exception exc) {
            throw new IllegalStateException(exc);
        }

        return encryptionKey;
    }

    
    /**已加密*/
    private static boolean isAlreadyEncrypted(Properties props) {
        for (Object value : props.values()) {
            if (SecurityUtils.isMarkedEncrypted((String) value)) {
                return true;
            }
        }
        return false;
    }

    /**文件锁*/
    static boolean lock() {
        File lockFile = new File(LOCK_FILE_NAME);
        try {
            return lockFile.createNewFile();
        } catch (IOException ignore) {
            return false;
        }
    }

    public static boolean unlock(boolean shouldLog) {
        File lockFile = new File(LOCK_FILE_NAME);
        boolean res = lockFile.delete();
        if (res && shouldLog) {
            System.out.println(LOCK_FILE_NAME + " was deleted.");
                                                                 
        }
        return res;
    }

}
