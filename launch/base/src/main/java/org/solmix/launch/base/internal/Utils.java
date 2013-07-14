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
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * 
 * @author ffz
 * @version 0.0.1
 * @since 0.0.4
 */

public class Utils
{

    public static File getSolmixHome(Class<?> mainClass, String solmixHomeProperty, String solmixHomeEnv) throws IOException {
        File configFile = null;
        // Use the system property if specified.
        String path = System.getProperty(solmixHomeProperty);
        if (path != null) {
            configFile = validateDirectoryExists(path, "Invalid " + solmixHomeProperty + " system property", false, true);
        }
        if (configFile == null) {
            path = System.getenv(solmixHomeEnv);
            if (path != null) {
                configFile = validateDirectoryExists(path, "Invalid " + solmixHomeEnv + " environment variable", false, true);
            }
        }
        if (configFile == null) {
            // guess the home from the location of the jar
            URL url = mainClass.getClassLoader().getResource(mainClass.getName().replace(".", "/") + ".class");
            if (url != null) {
                try {
                    JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                    url = jarConnection.getJarFileURL();
                    configFile = new File(new URI(url.toString())).getCanonicalFile().getParentFile().getParentFile();
                } catch (Exception ignored) {
                }
            }
        }

        if (configFile == null) {
            // Dig into the classpath to guess the location of the jar
            String classpath = System.getProperty("java.class.path");
            int index = classpath.toLowerCase().indexOf("karaf.jar");
            int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;
            if (index >= start) {
                String jarLocation = classpath.substring(start, index);
                configFile = new File(jarLocation).getCanonicalFile().getParentFile();
            }
        }

        return configFile;

    }

    public static File validateDirectoryExists(String path, String errPrefix, boolean createDirectory, boolean validate) {
        File rc;
        try {
            rc = new File(path).getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : " + e.getMessage());
        }
        if (!rc.exists() && !createDirectory && validate) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : does not exist");
        }
        if (!rc.exists() && createDirectory) {
            try {
                rc.mkdirs();
            } catch (SecurityException se) {
                throw new IllegalArgumentException(errPrefix + " '" + path + "' : " + se.getMessage());
            }
        }
        if (rc.exists() && !rc.isDirectory()) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : is not a directory");
        }
        return rc;
    }

    public static File getDirectory(String directoryProperty, File defaultValue, boolean create, boolean validate) {
        File direct = null;
        String path = System.getProperty(directoryProperty);
        if (path != null) {
            direct = validateDirectoryExists(path, "Invalid " + directoryProperty + " system property", create, validate);
        }
        if (direct == null) {
            direct = defaultValue;
        }

        return direct;
    }

    public static String[] convertToMavenUrlsIfNeeded(String location, boolean convertToMavenUrls) {
        String[] parts = location.split("\\|");
        if (convertToMavenUrls) {
            if (!parts[1].startsWith("mvn:")) {
                String[] p = parts[1].split("/");
                if (p.length >= 4 && p[p.length - 1].startsWith(p[p.length - 3] + "-" + p[p.length - 2])) {
                    String artifactId = p[p.length - 3];
                    String version = p[p.length - 2];
                    String classifier;
                    String type;
                    String artifactIdVersion = artifactId + "-" + version;
                    StringBuffer sb = new StringBuffer();
                    if (p[p.length - 1].charAt(artifactIdVersion.length()) == '-') {
                        classifier = p[p.length - 1].substring(artifactIdVersion.length() + 1, p[p.length - 1].lastIndexOf('.'));
                    } else {
                        classifier = null;
                    }
                    type = p[p.length - 1].substring(p[p.length - 1].lastIndexOf('.') + 1);
                    sb.append("mvn:");
                    for (int j = 0; j < p.length - 3; j++) {
                        if (j > 0) {
                            sb.append('.');
                        }
                        sb.append(p[j]);
                    }
                    sb.append('/').append(artifactId).append('/').append(version);
                    if (!"jar".equals(type) || classifier != null) {
                        sb.append('/');
                        if (!"jar".equals(type)) {
                            sb.append(type);
                        }
                        if (classifier != null) {
                            sb.append('/').append(classifier);
                        }
                    }
                    parts[1] = parts[0];
                    parts[0] = sb.toString();
                } else {
                    parts[1] = parts[0];
                }
            } else {
                String tmp = parts[0];
                parts[0] = parts[1];
                parts[1] = tmp;
            }
        } else {
            parts[1] = parts[0];
        }
        return parts;
    }
}
