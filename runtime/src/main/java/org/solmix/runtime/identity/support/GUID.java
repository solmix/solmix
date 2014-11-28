/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.identity.support;

import java.security.SecureRandom;

import org.solmix.commons.util.Base64;
import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.IDCreateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class GUID extends StringID {

    private static final long serialVersionUID = -2447708882682151305L;

    public static final String SR_DEFAULT_ALGO = null;

    public static final String SR_DEFAULT_PROVIDER = null;

    public static final int DEFAULT_BYTE_LENGTH = 20;

    // Class specific SecureRandom instance
    protected static transient SecureRandom random;

    /**
     * Protected constructor for factory-based construction
     * 
     * @param n the Namespace this identity will belong to
     * @param provider the name of the algorithm to use. See
     *        {@link SecureRandom}
     * @param byteLength the length of the target number (in bytes)
     */
    protected GUID(AbstractNamespace n, String algo, String provider,
        int byteLength) throws IDCreateException {
        super(n, "");
        // Get SecureRandom instance for class
        try {
            getRandom(algo, provider);
        } catch (Exception e) {
            throw new IDCreateException("GUID creation failure: "
                + e.getMessage());
        }
        // make sure we have reasonable byteLength
        if (byteLength <= 0)
            byteLength = 1;
        byte[] newBytes = new byte[byteLength];
        // Fill up random bytes
        random.nextBytes(newBytes);
        // Set value
        value = Base64.encode(newBytes);
    }

    protected GUID(AbstractNamespace n, String value) {
        super(n, value);
    }

    protected GUID(AbstractNamespace n, int byteLength)
        throws IDCreateException {
        this(n, SR_DEFAULT_ALGO, SR_DEFAULT_PROVIDER, byteLength);
    }

    protected GUID(AbstractNamespace n) throws IDCreateException {
        this(n, DEFAULT_BYTE_LENGTH);
    }

    /**
     * Get SecureRandom instance for creation of random number.
     * 
     * @param algo the String algorithm specification (e.g. "SHA1PRNG") for
     *        creation of the SecureRandom instance
     * @param provider the provider of the implementation of the given algorighm
     *        (e.g. "SUN")
     * @return SecureRandom
     * @exception Exception thrown if SecureRandom instance cannot be
     *            created/accessed
     */
    protected static synchronized SecureRandom getRandom(String algo,
        String provider) throws Exception {
        // Given algo and provider, get SecureRandom instance
        if (random == null) {
            initializeRandom(algo, provider);
        }
        return random;
    }

    protected static synchronized void initializeRandom(String algo,
        String provider) throws Exception {
        if (provider == null) {
            if (algo == null) {
                try {
                    random = SecureRandom.getInstance("IBMSECURERANDOM");
                } catch (Exception e) {
                    random = SecureRandom.getInstance("SHA1PRNG");
                }
            } else {
                random = SecureRandom.getInstance(algo);
            }
        } else {
            random = SecureRandom.getInstance(algo, provider);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("GUID[");
        sb.append(value).append("]");
        return sb.toString();
    }

}
