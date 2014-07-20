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

package org.solmix.command.karaf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月10日
 */

public abstract class AbstractDownloadCommand extends OsgiCommandSupport
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDownloadCommand.class);

    /**
     * @param location
     * @return
     */
    protected String getDownLoadedFile(String location) {
        String repository = System.getProperty("karaf.default.repository");
        String base = System.getProperty("karaf.home");
        String protocol = location.substring(0, location.indexOf(":"));
        String path = location.substring(location.indexOf(":") + 1);
        StringBuilder sb = new StringBuilder().append(base).append(
            File.separatorChar).append(repository).append(File.separatorChar);
        if ("mvn".equals(protocol)) {
            String[] strs = path.split("/");
            String groupId = strs[0].replace('.', File.separatorChar);
            String artificationId = strs[1];
            String version = strs[2];
            String type = "jar";
            if (strs.length > 3)
                type = strs[3];
            String classifier = null;
            if (strs.length > 4)
                classifier = strs[4];
            StringBuffer file = new StringBuffer().append(artificationId).append(
                '-').append(version);
            if (classifier != null)
                file.append('-').append(classifier);
            file.append('.').append(type);
            sb.append(groupId).append(File.separatorChar).append(artificationId).append(
                File.separatorChar).append(version).append(File.separatorChar).append(
                file);
        }

        return sb.toString();
    }

    protected void downLoadFile(String location, String finalname)
        throws IOException {
        File file = new File(finalname);
        if (file.exists()) {
            System.out.println("Ignoring bundle from \u001B[36m" + location
                + "\u001B[0m ");
            return;
        } else {
            System.out.println("Downloading bundle from \u001B[33m" + location
                + "\u001B[0m");
        }
        InputStream is = null;
        FileOutputStream fop = null;
        File tmp = new File(finalname + ".jtmp");
        try {
            URLConnection conn = new URL(location).openConnection();
            if (conn.getContentLength() != -1)
                System.out.println("Downloading bundle size \u001B[33m"
                    + conn.getContentLength() + "Byte\u001B[0m");
            is = new BufferedInputStream(conn.getInputStream());
            if (!tmp.exists()) {
                File parentFile = tmp.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                tmp.createNewFile();
            } else {
                tmp.delete();
                tmp.createNewFile();
            }

            fop = new FileOutputStream(tmp);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = is.read(buffer)) != -1) {
                fop.write(buffer, 0, bytesRead);
            }
            System.out.println("Saved bundle at \u001B[32m" + file.getPath()
                + "\u001B[0m");
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (is != null)
                is.close();
            if (fop != null) {
                fop.flush();
                fop.close();
            }
        }
        renameFile(tmp, file);
    }

    private void renameFile(File src, File target) throws IOException {
        BufferedInputStream inBuffer = null;
        BufferedOutputStream outBuffer = null;
        try {
            inBuffer = new BufferedInputStream(new FileInputStream(src));
            outBuffer = new BufferedOutputStream(new FileOutputStream(target));
            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = inBuffer.read(buffer)) != -1) {
                outBuffer.write(buffer, 0, bytesRead);
            }
            outBuffer.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (inBuffer != null)
                inBuffer.close();
            if (outBuffer != null) {
                outBuffer.flush();
                outBuffer.close();
            }
        }
        src.delete();

    }
}
