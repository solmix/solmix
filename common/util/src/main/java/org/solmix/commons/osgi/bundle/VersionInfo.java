/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.commons.osgi.bundle;

import org.osgi.framework.Version;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-16
 */

public abstract class VersionInfo<T> implements Comparable<VersionInfo<?>>
{

    private static final int A_GREATER = 1;

    private static final int B_GREATER = -1;

    private static final int EQUAL = 0;

    /** Marker used by Maven to identify snapshots */
    public static final String SNAPSHOT_MARKER = "SNAPSHOT";

    /** Name of the BND attribute that provides the bundle's last modified timestamp */
    public static final String BND_LAST_MODIFIED = "Bnd-LastModified";

    /**
     * Value for {@link #getBundleLastModified} if corresponding header is not present
     */
    public static final long BND_LAST_MODIFIED_MISSING = -1L;

    /** Return the source of information: underlying File or Bundle */
    public abstract T getSource();

    /** True if the provided data is a valid bundle */
    public abstract boolean isBundle();

    /** Return the bundle symbolic name, null if not available */
    public abstract String getBundleSymbolicName();

    /** Return the bundle version, null if not available */
    public abstract Version getVersion();

    /** True if the bundle version indicates a snapshot */
    public abstract boolean isSnapshot();

    /**
     * Return the bundle last modification time, based on the BND_LAST_MODIFIED manifest header, if available. This is
     * *not* the Bundle.getLastModified() value, which refers to actions in the OSGi framework.
     * 
     * @return BND_LAST_MODIFIED_MISSING if header not supplied
     */
    public abstract long getBundleLastModified();

    /**
     * Compare based on bundle version info, and for snapshots based on {@link #getBundleLastModified}
     */
    public int compareTo(VersionInfo<?> other) {
        // Handle null values
        if (other == null) {
            throw new IllegalArgumentException("b is null, cannot compare");
        }

        // Handle non-bundles: we don't want them!
        if (!isBundle()) {
            throw new IllegalArgumentException("Not a bundle, cannot compare: " + this);
        }
        if (!other.isBundle()) {
            throw new IllegalArgumentException("Not a bundle, cannot compare:" + other);
        }

        // First compare symbolic names
        int result = getBundleSymbolicName().compareTo(other.getBundleSymbolicName());

        // Then compare versions
        if (result == EQUAL) {
            final Version va = getVersion();
            final Version vb = other.getVersion();
            if (va == null && vb == null) {
                // result = EQUAL
            } else if (vb == null) {
                result = A_GREATER;
            } else if (va == null) {
                result = B_GREATER;
            } else {
                result = va.compareTo(vb);
            }
        }

        // Then, if snapshots, compare modification times, more recent comes first
        if (result == EQUAL && isSnapshot()) {
            final long ma = getBundleLastModified();
            final long mb = other.getBundleLastModified();
            if (ma > mb) {
                result = A_GREATER;
            } else if (mb > ma) {
                result = B_GREATER;
            }
        }

        return result;
    }

}
