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

package org.solmix.launch.base.shared;

import java.beans.Introspector;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 * 
 * @author ffz
 * @version 0.0.1 2012-3-16
 * @since 0.0.4
 */

public class Loader
{

    private final File launchLibs;

    public Loader(final File launchLibs)
    {
        if (launchLibs == null) {
            throw new IllegalArgumentException("Solmix  Home must not be null or empty");
        }
        this.launchLibs = checkLaunchLibsDir(launchLibs);
        removeOldLoadedJars();
    }

    public void cleanupVM() {

        // ensure the JavaBeans introspector lets go of any classes it
        // may haved cached after introspection
        Introspector.flushCaches();

        // if solmix home is set, check whether we have to close the
        // launcher JAR JarFile, which might be cached in the platform
        closeLoadedJarFile(getLaunchJarFile());
    }

    /**
     * @param launcherJarFile
     */
    private void closeLoadedJarFile(final File[] loadedJarFiles) {
        for (File loadedJarFile : loadedJarFiles) {
            try {
                final URI launcherJarUri = loadedJarFile.toURI();
                final URL launcherJarRoot = new URL("jar:" + launcherJarUri + "!/");
                final URLConnection conn = launcherJarRoot.openConnection();
                if (conn instanceof JarURLConnection) {
                    final JarFile jarFile = ((JarURLConnection) conn).getJarFile();
                    jarFile.close();
                }
            } catch (Exception e) {
                // better logging here
            }
        }

    }

    /**
     * get launch jar file.
     * 
     * @return
     */
    private File[] getLaunchJarFile() {
        File result = null;
        final File[] launcherJars = getLaunchJarFiles();
        if (launcherJars == null || launcherJars.length == 0) {

            // return a non-existing file naming the desired primary name
            result = new File(launchLibs.getAbsolutePath() + LaunchConstants.LOADER_JAR_REL_PATH);
            if (!result.exists()) {
                result = new File(launchLibs, "org.solmix.launch.base.jar");
            }
            return new File[] { result };

        }

        return launcherJars;
    }

    /**
     * 
     */
    private void removeOldLoadedJars() {
        final File[] loadedJars = getLaunchJarFiles();
        // TODO
    }

    /**
     * 从solmixBase中加载所有可能为启动文件的JAR包。
     * 
     * @return
     */
    private File[] getLaunchJarFiles() {
        final File[] rawList = launchLibs.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.isFile()) {
                    if (pathname.getName().startsWith(LaunchConstants.LOADERS_PATH) || pathname.getName().contains(LaunchConstants.JAAS_PATH_LOADER))
                        return true;
                }
                return false;
            }
        });

        return rawList;
    }

    /**
     * @param loadersHome
     * @return
     */
    private static File checkLaunchLibsDir(File loadersHome) {
        if (loadersHome.exists()) {
            if (!loadersHome.isDirectory()) {
                throw new IllegalArgumentException("Solmix launch libs directory " + loadersHome + "exists but is not a directory");
            }
        } else if (!loadersHome.mkdirs()) {
            throw new IllegalArgumentException("Solmix launch libs directory" + loadersHome + "exists but  cannot be created as a directory");
        }
        return loadersHome;
    }

    /**
     * Load class form launch libs.
     * 
     * @param loaderClassName
     * @return
     */
    public Object loadLaucher(String loaderClassName) {
        File[] launchJars = getLaunchJarFile();
        final ClassLoader loader;
        try {
            URL[] urls = new URL[launchJars.length];
            for (int i = 0; i < launchJars.length; i++) {
                urls[i] = launchJars[i].toURI().toURL();
            }
            loader = new SolmixClassLoader(urls);
            // loader = this.getClass().getClassLoader();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Cannot create an URL from the JAR path name", e);
        }
        try {
            final Class<?> launcherClass = loader.loadClass(loaderClassName);
            return launcherClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find class " + loaderClassName, e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot instantiate launcher class " + loaderClassName, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access constructor of class " + loaderClassName, e);
        }
    }

    /**
     * Spools the contents of the input stream to the given file replacing the contents of the file with the contents of
     * the input stream. When this method returns, the input stream is guaranteed to be closed.
     * 
     * @throws IOException If an error occurrs reading or writing the input stream contents.
     */
    public static void spool(InputStream ins, File destFile) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            byte[] buf = new byte[8192];
            int rd;
            while ((rd = ins.read(buf)) >= 0) {
                out.write(buf, 0, rd);
            }
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignore) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /** Meant to be overridden to display or log info */
    protected void info(String msg) {
    }

}
