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

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * 
 * @author ffz
 * @version 110035 2012-3-16
 */

public class BundleVersionInfo extends VersionInfo<File>
{

    private final String symbolicName;

    private final Version version;

    private final boolean isSnapshot;

    private final long lastModified;

    private final File source;

    public BundleVersionInfo(File bundle) throws IOException
    {
        source = bundle;
        final JarFile f = new JarFile(bundle);
        try {
            final Manifest m = f.getManifest();
            if (m == null) {
                symbolicName = null;
                version = null;
                isSnapshot = false;
                lastModified = BND_LAST_MODIFIED_MISSING;
            } else {
                symbolicName = m.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
                final String v = m.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
                version = v == null ? null : new Version(v);
                isSnapshot = v != null && v.contains(SNAPSHOT_MARKER);
                final String last = m.getMainAttributes().getValue(BND_LAST_MODIFIED);
                long lastMod = BND_LAST_MODIFIED_MISSING;
                if (last != null) {
                    try {
                        lastMod = Long.parseLong(last);
                    } catch (NumberFormatException ignore) {
                    }
                }
                lastModified = lastMod;
            }
        } finally {
            f.close();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + source.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#getSource()
     */
    @Override
    public File getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#isBundle()
     */
    @Override
    public boolean isBundle() {
        return symbolicName != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#getBundleSymbolicName()
     */
    @Override
    public String getBundleSymbolicName() {
        return symbolicName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#getVersion()
     */
    @Override
    public Version getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#isSnapshot()
     */
    @Override
    public boolean isSnapshot() {
        return isSnapshot;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.bundle.VersionInfo#getBundleLastModified()
     */
    @Override
    public long getBundleLastModified() {
        return lastModified;
    }

}
