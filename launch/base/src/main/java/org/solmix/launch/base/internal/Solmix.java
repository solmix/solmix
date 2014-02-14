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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

import org.solmix.launch.base.shared.LaunchConstants;
import org.solmix.launch.base.shared.Notifiable;

/**
 * 
 * @author ffz
 * @version 2012-3-17
 * @since 0.0.4
 */

public class Solmix
{

    public static final String PROP_SYSTEM_PACKAGES = "org.solmix.launch.system.packages";

    public static final String PROP_SOLMIX_HOME = LaunchConstants.SOLMIX_HOME;

    public static final String PROP_ENV_SOLMIX_HOME = "SOLMIX_HOME";

    public static final String PROP_SOLMIX_BASE = "solmix.base";

    public static final String PROP_SOLMIX_DATA = "solmix.data";

    private static final String JAAS_PROPERTYNAME = "java.security.auth.login.config";

    /**
     * The property for auto-discovering the bundles
     */
    public static final String PROP_AUTO_START = "auto.start";

    public static final String PROP_FRAMEWORK_FELIX = "framework.felix";

    public static final String FELIX_CONFIG_PROPERTIES_FILE = "config.properties";

    public static final String FELIX_SYSTEM_PROPERTIES_FILE = "system.properties";

    public static final String FELIX_STARTUP_PROPERTIES_FILE = "startup.properties";

    public static final String PROP_DEFAULT_REPOSITORY = "default.repository";

    public static final String INCLUDES_PROPERTY = "${includes}"; // mandatory includes

    public static final String OPTIONALS_PROPERTY = "${optionals}"; // optionals includes

    private static final String DELIM_START = "${";

    private static final String DELIM_STOP = "}";

    private File solmixHome;

    private File solmixBase;

    private File solmixData;

    private Properties configProps;

    private final int shutdownTimeout = 5 * 60 * 1000;

    Logger LOG = Logger.getLogger(this.getClass().getName());

    /**
     * The <code>Felix</code> instance loaded on {@link #init()} and stopped on {@link #destroy()}.
     */
    private Framework framework;

    private final String PROP_OSGI_FRAMEWORK_FACTORY = "osgi.framework.factory";

    private final String PROP_AUTO_INSTALL = "auto.install";

    public Solmix(final Notifiable notifiable, final Map<String, String> propOverwrite)
    {
        try {
            launch(notifiable, propOverwrite);
        } catch (Exception e) {
            destroy();
            e.printStackTrace();
            LOG.log(Level.FINEST, "ErrorCode:-1\n can't start osgi framework!", e);
        }
    }

    /**
     * Set the JAAS system configuration file.
     */
    protected void tryToSetJAASConfig() {
        String jaas = System.getProperty(JAAS_PROPERTYNAME);
        String home = System.getProperty(PROP_SOLMIX_HOME);
        if (home == null)
            home = "";
        if (jaas == null || jaas.length() == 0) {

            try {
                System.setProperty(JAAS_PROPERTYNAME, home + System.getProperty("file.separator") + "etc" + System.getProperty("file.separator")
                    + "jaas.config");
            } catch (SecurityException se) {
                LOG.log(Level.FINER, "Failed to set {" + JAAS_PROPERTYNAME + "}, check application server settings");
                LOG.log(Level.FINER, se.getMessage());
                LOG.log(Level.FINER, "Aborting startup");
                return;
            }
        } else {
            LOG.info("JAAS config file set by parent container or some other application");
            LOG.info("Config in use {" + System.getProperty(JAAS_PROPERTYNAME) + "}");
            LOG.info("Please make sure JAAS config has all necessary modules (refer config/jaas.config) configured");
        }

    }

    /**
     * 加载config.properties 文件中的配置
     * 
     * @param propOverwrite
     * @return
     * @throws FileNotFoundException
     */
    private Properties loadConfigProperties(Map<String, String> propOverwrite) throws Exception {
        // See if the property URL was specified as a property.
        URL configPropURL;

        try {
            File etcFolder = new File(solmixBase, "etc");
            System.out.println(solmixBase.getAbsolutePath());
            if (!etcFolder.exists()) {
                throw new FileNotFoundException("etc folder not found: " + etcFolder.getAbsolutePath());
            }
            File file = new File(etcFolder, FELIX_CONFIG_PROPERTIES_FILE);
            configPropURL = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            System.err.print("Main: " + ex);
            return null;
        }

        Properties configProps = loadPropertiesFile(configPropURL, false);
        configProps.putAll(propOverwrite);
        // Perform variable substitution for system properties.
        for (Enumeration e = configProps.propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            configProps.setProperty(name, substVars(configProps.getProperty(name), name, null, configProps));
        }

        return configProps;

    }

    protected static Properties loadPropertiesFile(URL configPropURL, boolean failIfNotFound) throws Exception {
        // Read the properties file.
        Properties configProps = new Properties();
        InputStream is = null;
        try {
            is = configPropURL.openConnection().getInputStream();
            configProps.load(is);
            is.close();
        } catch (FileNotFoundException ex) {
            if (failIfNotFound) {
                throw ex;
            } else {
                System.err.println("WARN: " + configPropURL + " is not found, so not loaded");
            }
        } catch (Exception ex) {
            System.err.println("Error loading config properties from " + configPropURL);
            System.err.println("Main: " + ex);
            return configProps;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex2) {
                // Nothing we can do.
            }
        }
        String includes = configProps.getProperty(INCLUDES_PROPERTY);
        if (includes != null) {
            StringTokenizer st = new StringTokenizer(includes, "\" ", true);
            if (st.countTokens() > 0) {
                String location;
                do {
                    location = nextLocation(st);
                    if (location != null) {
                        URL url = new URL(configPropURL, location);
                        Properties props = loadPropertiesFile(url, true);
                        configProps.putAll(props);
                    }
                } while (location != null);
            }
            configProps.remove(INCLUDES_PROPERTY);
        }
        String optionals = configProps.getProperty(OPTIONALS_PROPERTY);
        if (optionals != null) {
            StringTokenizer st = new StringTokenizer(optionals, "\" ", true);
            if (st.countTokens() > 0) {
                String location;
                do {
                    location = nextLocation(st);
                    if (location != null) {
                        URL url = new URL(configPropURL, location);
                        Properties props = loadPropertiesFile(url, false);
                        configProps.putAll(props);
                    }
                } while (location != null);
            }
            configProps.remove(OPTIONALS_PROPERTY);
        }
        for (Enumeration e = configProps.propertyNames(); e.hasMoreElements();) {
            Object key = e.nextElement();
            if (key instanceof String) {
                String v = configProps.getProperty((String) key);
                configProps.put(key, v.trim());
            }
        }
        return configProps;
    }

    private static String nextLocation(StringTokenizer st) {
        String retVal = null;

        if (st.countTokens() > 0) {
            String tokenList = "\" ";
            StringBuffer tokBuf = new StringBuffer(10);
            String tok;
            boolean inQuote = false;
            boolean tokStarted = false;
            boolean exit = false;
            while ((st.hasMoreTokens()) && (!exit)) {
                tok = st.nextToken(tokenList);
                if (tok.equals("\"")) {
                    inQuote = !inQuote;
                    if (inQuote) {
                        tokenList = "\"";
                    } else {
                        tokenList = "\" ";
                    }

                } else if (tok.equals(" ")) {
                    if (tokStarted) {
                        retVal = tokBuf.toString();
                        tokStarted = false;
                        tokBuf = new StringBuffer(10);
                        exit = true;
                    }
                } else {
                    tokStarted = true;
                    tokBuf.append(tok.trim());
                }
            }

            // Handle case where end of token stream and
            // still got data
            if ((!exit) && (tokStarted)) {
                retVal = tokBuf.toString();
            }
        }

        return retVal;
    }

    private Framework createFramework(final Notifiable notifiable, Properties props) throws Exception {
        OsgiFrameworkFactory factory = new OsgiFrameworkFactoryImpl();
        return factory.newFramework(notifiable, props);
    }

    public void launch(final Notifiable notifiable, final Map<String, String> propOverwrite) throws Exception {
        if (propOverwrite.get(PROP_SOLMIX_HOME) != null) {
            File home = new File(propOverwrite.get(PROP_SOLMIX_HOME).trim());
            if (home.exists() && home.isDirectory()) {
                solmixHome = home;
            } else {
                solmixHome = Utils.getSolmixHome(Solmix.class, PROP_SOLMIX_HOME, PROP_ENV_SOLMIX_HOME);
            }
            if (solmixHome == null)
                throw new IOException("solmix home must set to start framework");
        }

        solmixBase = Utils.getDirectory(PROP_SOLMIX_BASE, solmixHome, false, true);
        solmixData = Utils.getDirectory(PROP_SOLMIX_DATA, new File(solmixBase, "data"), false, true);

        System.setProperty(PROP_SOLMIX_HOME, solmixHome.getAbsolutePath());
        System.setProperty(PROP_SOLMIX_BASE, solmixBase.getAbsolutePath());
        System.setProperty(PROP_SOLMIX_DATA, solmixData.getAbsolutePath());
        tryToSetJAASConfig();
        // 加载系统配置，配置参数放入system.setProperty()
        loadSystemProperties(solmixBase);
        // read the default parameters

        configProps = this.loadConfigProperties(propOverwrite);
        BootstrapLogManager.setProperties(configProps);
        LOG.addHandler(BootstrapLogManager.getDefaultHandler());

        // ClassLoader classLoader = createClassLoader(configProps);

        if (configProps.getProperty(Constants.FRAMEWORK_STORAGE) == null) {
            File storage = new File(solmixData.getPath(), "cache");
            try {
                storage.mkdirs();
            } catch (SecurityException se) {
                throw new Exception(se.getMessage());
            }
            configProps.setProperty(Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath());
        }
        // String factoryClass = configProps.getProperty(PROP_OSGI_FRAMEWORK_FACTORY);
        // if (factoryClass == null) {
        // InputStream is = classLoader.getResourceAsStream("META-INF/services/" + FrameworkFactory.class.getName());
        // BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        // factoryClass = br.readLine();
        // br.close();
        // }
        // FrameworkFactory factory = (FrameworkFactory) classLoader.loadClass(factoryClass).newInstance();
        framework = createFramework(notifiable, configProps);
        framework.init();
        loadStartupProperties(configProps);
        processBundlesInstall(framework.getBundleContext());
        framework.start();
    }

    /**
     * @param bundleContext
     */
    private void processBundlesInstall(BundleContext bundleContext) {
        // Check if we want maven style URL
        boolean mvnStyle = Boolean.parseBoolean(configProps.getProperty("mvn.style", "true"));
        // Retrieve the Start Level service, since it will be needed
        // to set the start level of the installed bundles.
        FrameworkStartLevel startLevel = framework.adapt(FrameworkStartLevel.class);
        // set auto start level is 60.
        startLevel.setInitialBundleStartLevel(60);
        // if frameworkk is clean and install all bundles in it.
        if (framework.getBundleContext().getBundles().length == 1) {
            autoInstall(PROP_AUTO_INSTALL, bundleContext, startLevel, mvnStyle, false);
            autoInstall(PROP_AUTO_START, bundleContext, startLevel, mvnStyle, true);
        }

    }

    /**
     * @param pROP_AUTO_INSTALL2
     * @param bundleContext
     * @param startLevel
     * @param mvnStyle
     * @param b
     */
    private List<Bundle> autoInstall(String propertyPrefix, BundleContext context, FrameworkStartLevel fsl, boolean mvnStyle, boolean start) {
        Map<Integer, String> autoStart = new TreeMap<Integer, String>();
        List<Bundle> bundles = new ArrayList<Bundle>();
        for (Object o : configProps.keySet()) {
            String key = (String) o;
            // Ignore all keys that are not the auto-start property.
            if (!key.startsWith(propertyPrefix)) {
                continue;
            }
            // If the auto-start property does not have a start level,
            // then assume it is the default bundle start level, otherwise
            // parse the specified start level.
            int startLevel = fsl.getInitialBundleStartLevel();
            if (!key.equals(propertyPrefix)) {
                try {
                    startLevel = Integer.parseInt(key.substring(key.lastIndexOf('.') + 1));
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid property: " + key);
                }
            }
            autoStart.put(startLevel, configProps.getProperty(key));
        }
        for (Integer startLevel : autoStart.keySet()) {
            StringTokenizer st = new StringTokenizer(autoStart.get(startLevel), "\" ", true);
            if (st.countTokens() > 0) {
                String location;
                do {
                    location = nextLocation(st);
                    if (location != null) {
                        try {
                            String[] parts = Utils.convertToMavenUrlsIfNeeded(location, mvnStyle);
                            Bundle b = context.installBundle(parts[0], new URL(parts[1]).openStream());
                            b.adapt(BundleStartLevel.class).setStartLevel(startLevel);
                            bundles.add(b);
                        } catch (Exception ex) {
                            System.err.println("Error installing bundle  " + location + ": " + ex);
                        }
                    }
                } while (location != null);
            }
        }
        // Now loop through and start the installed bundles.
        if (start) {
            for (Bundle b : bundles) {
                try {
                    String fragmentHostHeader = b.getHeaders().get(Constants.FRAGMENT_HOST);
                    if (fragmentHostHeader == null || fragmentHostHeader.trim().length() == 0) {
                        b.start();
                    }
                } catch (Exception ex) {
                    System.err.println("Error starting bundle " + b.getSymbolicName() + ": " + ex);
                }
            }
        }
        return bundles;

    }

    /**
     * @param configProps2
     */
    private void loadStartupProperties(Properties configProps) throws Exception {
        /**
         * load the startup bundles jar File.
         */
        List<File> bundleDirs = new ArrayList<File>();
        URL startupPropURL = null;
        File etcFolder = new File(solmixBase, "etc");
        if (!etcFolder.exists()) {
            throw new FileNotFoundException("etc folder not found: " + etcFolder.getAbsolutePath());
        }
        File file = new File(etcFolder, FELIX_STARTUP_PROPERTIES_FILE);
        startupPropURL = file.toURI().toURL();
        Properties startupProps = loadPropertiesFile(startupPropURL, true);
        String defaultRepo = System.getProperty(PROP_DEFAULT_REPOSITORY, "bundles");
        if (solmixBase.equals(solmixHome)) {
            File systemRepo = new File(solmixHome, defaultRepo);
            if (!systemRepo.exists()) {
                throw new FileNotFoundException("system repo not found: " + systemRepo.getAbsolutePath());
            }
            bundleDirs.add(systemRepo);
        } else {
            File baseSystemRepo = new File(solmixBase, defaultRepo);
            File homeSystemRepo = new File(solmixHome, defaultRepo);
            if (!baseSystemRepo.exists() && !homeSystemRepo.exists()) {
                throw new FileNotFoundException("system repos not found: " + baseSystemRepo.getAbsolutePath() + " "
                    + homeSystemRepo.getAbsolutePath());
            }
            bundleDirs.add(baseSystemRepo);
            bundleDirs.add(homeSystemRepo);
        }
        processConfigProps(configProps, startupProps, bundleDirs);
    }

    /**
     * @param configProps
     * @param startupProps
     * @param bundleDirs
     * @throws Exception
     */
    private void processConfigProps(Properties configProps, Properties startupProps, List<File> bundleDirs) throws Exception {
        if (bundleDirs == null) {
            return;
        }
        boolean hasErrors = false;
        String autoStartFlag = configProps.getProperty(PROP_AUTO_START, "").trim();
        configProps.remove(PROP_AUTO_START);
        if ("all".equals(autoStartFlag)) {
            ArrayList<File> jars = new ArrayList<File>();

            for (File bundleDir : bundleDirs) {
                findJars(bundleDir, jars);
            }

            StringBuffer sb = new StringBuffer();

            for (File jar : jars) {
                try {
                    sb.append("\"").append(jar.toURI().toURL().toString()).append("\" ");
                } catch (MalformedURLException e) {
                    System.err.print("Ignoring " + jar.toString() + " (" + e + ")");
                }
            }

            configProps.setProperty(PROP_AUTO_START, sb.toString());
        } else if (FELIX_STARTUP_PROPERTIES_FILE.equals(autoStartFlag)) {
            HashMap<Integer, StringBuffer> levels = new HashMap<Integer, StringBuffer>();
            for (Object o : startupProps.keySet()) {
                String name = (String) o;
                File file = findFile(bundleDirs, name);

                if (file != null) {
                    Integer level;
                    try {
                        level = new Integer(startupProps.getProperty(name).trim());
                    } catch (NumberFormatException e1) {
                        System.err.print("Ignoring " + file.toString() + " (run level must be an integer)");
                        continue;
                    }
                    StringBuffer sb = levels.get(level);
                    if (sb == null) {
                        sb = new StringBuffer(256);
                        levels.put(level, sb);
                    }
                    try {
                        sb.append("\"").append(file.toURI().toURL().toString()).append("|").append(name).append("\" ");
                    } catch (MalformedURLException e) {
                        System.err.print("Ignoring " + file.toString() + " (" + e + ")");
                    }
                } else {
                    System.err.println("Bundle listed in " + FELIX_STARTUP_PROPERTIES_FILE + " configuration not found: " + name);
                    hasErrors = true;
                }
            }

            for (Map.Entry<Integer, StringBuffer> entry : levels.entrySet()) {
                configProps.setProperty(PROP_AUTO_START + "." + entry.getKey(), entry.getValue().toString());
            }
        }
        if (hasErrors) {
            throw new Exception("Aborting due to missing startup bundles");
        }
    }

    private static File findFile(List<File> bundleDirs, String name) {
        for (File bundleDir : bundleDirs) {
            File file = findFile(bundleDir, name);
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    private static File findFile(File dir, String name) {
        name = fromMaven(name);
        File theFile = new File(dir, name);

        if (theFile.exists() && !theFile.isDirectory()) {
            return theFile;
        }
        return null;
    }

    private static final Pattern mvnPattern = Pattern.compile("mvn:([^/ ]+)/([^/ ]+)/([^/ ]*)(/([^/ ]+)(/([^/ ]+))?)?");

    /**
     * Returns a path for an srtifact. Input: path (no ':') returns path Input:
     * mvn:<groupId>/<artifactId>/<version>/<type>/<classifier> converts to default repo location path type and
     * classifier are optional.
     * 
     * 
     * @param name input artifact info
     * @return path as supplied or a default maven repo path
     */
    static String fromMaven(String name) {
        Matcher m = mvnPattern.matcher(name);
        if (!m.matches()) {
            return name;
        }
        StringBuilder b = new StringBuilder();
        b.append(m.group(1));
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) == '.') {
                b.setCharAt(i, '/');
            }
        }
        b.append("/");// groupId
        String artifactId = m.group(2);
        String version = m.group(3);
        String extension = m.group(5);
        String classifier = m.group(7);
        b.append(artifactId).append("/");// artifactId
        b.append(version).append("/");// version
        b.append(artifactId).append("-").append(version);
        if (present(classifier)) {
            b.append("-").append(classifier);
        } else {
            if (present(extension)) {
                b.append(".").append(extension);
            } else {
                b.append(".jar");
            }
        }
        return b.toString();
    }

    private static boolean present(String part) {
        return part != null && !part.isEmpty();
    }

    private static void findJars(File dir, ArrayList<File> jars) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                findJars(file, jars);
            } else {
                if (file.toString().endsWith(".jar")) {
                    jars.add(file);
                }
            }
        }
    }

    /**
     * @param configProps
     * @return
     * @throws Exception
     */
    private ClassLoader createClassLoader(Properties configProps) throws Exception {
        String bundle = configProps.getProperty(PROP_FRAMEWORK_FELIX);
        if (bundle == null) {
            throw new IllegalArgumentException("Property " + PROP_FRAMEWORK_FELIX + " must be set in the etc/" + FELIX_CONFIG_PROPERTIES_FILE
                + " configuration file");
        }
        File bundleFile = new File(solmixBase, bundle);
        if (!bundleFile.exists()) {
            bundleFile = new File(solmixHome, bundle);
        }
        if (!bundleFile.exists()) {
            throw new FileNotFoundException(bundleFile.getAbsolutePath());
        }

        List<URL> urls = new ArrayList<URL>();
        urls.add(bundleFile.toURI().toURL());
        // File[] libs = new File(solmixHome, "lib").listFiles();
        // if (libs != null) {
        // for (File f : libs) {
        // if (f.isFile() && f.canRead() && f.getName().endsWith(".jar")) {
        // urls.add(f.toURI().toURL());
        // }
        // }
        // }

        return new URLClassLoader(urls.toArray(new URL[urls.size()]), Solmix.class.getClassLoader());
    }

    /**
     * @param dir
     */
    @SuppressWarnings("rawtypes")
    protected void loadSystemProperties(File dir) {
        URL propURL;
        try {
            File file = new File(new File(solmixBase, "etc"), FELIX_SYSTEM_PROPERTIES_FILE);
            propURL = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            System.err.print("Main: " + ex);
            return;
        }

        // Read the properties file.
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = propURL.openConnection().getInputStream();
            props.load(is);
            is.close();
        } catch (FileNotFoundException ex) {
            // Ignore file not found.
        } catch (Exception ex) {
            System.err.println("Main: Error loading system properties from " + propURL);
            System.err.println("Main: " + ex);
            try {
                if (is != null)
                    is.close();
            } catch (IOException ex2) {
                // Nothing we can do.
            }
            return;
        }
        // Perform variable substitution on specified properties.
        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = System.getProperty(name, props.getProperty(name));
            System.setProperty(name, substVars(value, name, null, props));
        }
    }

    public static String substVars(String val, String currentKey, Map<String, String> cycleMap, Properties configProps)
        throws IllegalArgumentException {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null) {
            cycleMap = new HashMap<String, String>();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = val.indexOf(DELIM_STOP);

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf(DELIM_START);
        while (stopDelim >= 0) {
            int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
            if ((idx < 0) || (idx > stopDelim)) {
                break;
            } else if (idx < stopDelim) {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if ((startDelim < 0) && (stopDelim < 0)) {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if (((startDelim < 0) || (startDelim > stopDelim)) && (stopDelim >= 0)) {
            throw new IllegalArgumentException("stop delimiter with no start delimiter: " + val);
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = (configProps != null) ? configProps.getProperty(variable, null) : null;
        if (substValue == null) {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }

    protected BundleContext getBundleContext() {
        return framework.getBundleContext();

    }

    /**
     * Destroys this servlet by shutting down the OSGi framework and hence the delegatee servlet if one is set at all.
     */
    public final boolean destroy() {
        if (framework == null) {
            return true;
        }
        final Framework myFramework;
        synchronized (this) {
            myFramework = framework;
            framework = null;
        }
        if (myFramework.getState() == Bundle.ACTIVE || myFramework.getState() == Bundle.STARTING) {
            new Thread() {

                @Override
                public void run() {
                    try {
                        myFramework.stop();
                    } catch (BundleException e) {
                        LOG.log(Level.FINEST, "Error stopping solmix: " + e.getMessage());
                    }
                }
            }.start();
        }
        try {
            myFramework.waitForStop(0);
        } catch (InterruptedException e) {
            LOG.log(Level.FINEST, "Failure initiating Framework Shutdown", e);
            return false;
        }
        LOG.log(Level.INFO, "solmix framework stopped");
        return true;

    }
}
