
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

package org.solmix.launch.base.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-20
 */

public class BootstrapLogManager
{

    private static Handler handler;

    private static final String KARAF_BOOTSTRAP_LOG = "solmix.bootstrap.log";

    private static Properties configProps;

    public static synchronized Handler getDefaultHandler() {
        if (handler != null) {
            return handler;
        }
        String filename;
        File log;
        Properties props = new Properties();
        filename = configProps.getProperty(KARAF_BOOTSTRAP_LOG);

        if (filename != null) {
            log = new File(filename);
        } else {
            // Make a best effort to log to the default file appender configured for log4j
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(System.getProperty("karaf.base") + "/etc/org.ops4j.pax.logging.cfg");
                props.load(fis);
            } catch (IOException e) {
                props.setProperty("log4j.appender.out.file", "${karaf.data}/log/karaf.log");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
            filename = Solmix.substVars(props.getProperty("log4j.appender.out.file"), "log4j.appender.out.file", null, null);
            log = new File(filename);
        }

        try {
            handler = new BootstrapLogManager.SimpleFileHandler(log);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return handler;
    }

    public static void setProperties(Properties configProps) {
        BootstrapLogManager.configProps = configProps;
    }

    /**
     * Implementation of java.util.logging.Handler that does simple appending to a named file. Should be able to use
     * this for bootstrap logging via java.util.logging prior to startup of pax logging.
     */
    public static class SimpleFileHandler extends StreamHandler
    {

        public SimpleFileHandler(File file) throws IOException
        {
            open(file, true);
        }

        private void open(File logfile, boolean append) throws IOException {
            if (!logfile.getParentFile().exists()) {
                try {
                    logfile.getParentFile().mkdirs();
                } catch (SecurityException se) {
                    throw new IOException(se.getMessage());
                }
            }
            FileOutputStream fout = new FileOutputStream(logfile, append);
            BufferedOutputStream out = new BufferedOutputStream(fout);
            setOutputStream(out);
        }

        public synchronized void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            super.publish(record);
            flush();
        }
    }

}
