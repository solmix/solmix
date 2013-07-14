/*
 * SOLMIX PROJECT
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

package org.solmix.launch.base.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.solmix.launch.base.shared.Loader;
import org.solmix.launch.base.shared.Notifiable;

/**
 * 
 * @author ffz
 * @version 0.0.1 2012-3-18
 * @since 0.0.4
 */

public class OsgiFrameworkFactoryImpl implements OsgiFrameworkFactory
{

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.launch.shared.internal.OsgiFrameworkFactory#newFramework(org.solmix.launch.shared.Notifiable,
     *      java.util.Map)
     */
    @Override
    public Framework newFramework(Notifiable notifiable, Properties configuration) {
        return new SolmixFelix(notifiable, configuration);
    }

    private class SolmixFelix extends Felix
    {

        private final Notifiable notifiable;

        private Thread notifierThread;

        /**
         * @param notifiable
         * @param configMap
         */
        public SolmixFelix(final Notifiable notifiable, Properties configMap)
        {
            super(configMap);
            this.notifiable = notifiable;
        }

        @Override
        public void update() throws BundleException {
            update(null);
        }

        @Override
        public void update(final InputStream is) throws BundleException {
            // get the update file and make sure, the stream is closed
            try {
                startNotifier(true, is);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {
                    }
                }
            }

            // just stop the framework now
            super.stop();
        }

        // public void restart() throws BundleException {
        // super.stop();
        // }

        @Override
        public void stop() throws BundleException {
            startNotifier(false, null);
            super.stop();
        }

        public void stop(final int status) throws BundleException {
            startNotifier(false, null);
            super.stop(status);
        }

        private synchronized void startNotifier(final boolean restart, final InputStream ins) {
            if (notifierThread == null) {
                notifierThread = new Thread(new Notifier(restart, ins), "Solmix Servlet Notifier");
                notifierThread.setDaemon(false);
                notifierThread.start();
            }
        }

        private class Notifier implements Runnable
        {

            private final boolean restart;

            private final File updateFile;

            private Notifier(final boolean restart, final InputStream ins)
            {
                this.restart = restart;

                if (ins != null) {
                    File tmpFile;
                    try {
                        tmpFile = File.createTempFile("tmpupdate", ".jar");
                        Loader.spool(ins, tmpFile);
                    } catch (IOException ioe) {
                        // TOOD: log
                        tmpFile = null;
                    }
                    this.updateFile = tmpFile;
                } else {
                    this.updateFile = null;
                }
            }

            public void run() {

                try {
                    SolmixFelix.this.waitForStop(0);
                } catch (InterruptedException ie) {
                    // TODO: log
                }

                if (restart) {
                    SolmixFelix.this.notifiable.updated(updateFile);
                } else {
                    SolmixFelix.this.notifiable.stopped();
                }
            }
        }
    }

}
