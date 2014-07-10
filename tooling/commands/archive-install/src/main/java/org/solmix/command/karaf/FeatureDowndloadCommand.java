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

package org.solmix.command.karaf;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.ConfigFileInfo;
import org.apache.karaf.features.Dependency;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.apache.karaf.features.Resolver;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-12
 */
@Command(scope = "download", name = "feature", description = "downlaod the karaf features relative archives")
public class FeatureDowndloadCommand extends AbstractDownloadCommand
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDowndloadCommand.class);

    @Argument(index = 0, name = "feature", description = "The name and version of the features to install. A feature id looks like name/version. The version is optional.", required = true, multiValued = true)
    List<String> features;

    private Map<String, Map<String, Feature>> allFeatures;

    private FeaturesService service;

    @Override
    protected Object doExecute() throws Exception {
        ServiceReference<?> ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            System.out.println("FeaturesService service is unavailable.");
            return null;
        }
        try {
            FeaturesService admin = (FeaturesService) getBundleContext().getService(ref);
            if (admin == null) {
                System.out.println("FeaturesService service is unavailable.");
                return null;
            }

            doExecute(admin);
        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;
    }

    /**
     * @param admin
     * @throws Exception 
     */
    private void doExecute(FeaturesService admin) throws Exception {
        service = admin;
        if(features.size()==1&&"*".equals(features.get(0))){
          for(String featureName:this.getFeatures().keySet()){
              downloadArchive( featureName);
          }
              
        }
        for (String feature : features) {
            String[] split = feature.split("/");
            String name = split[0];
            downloadArchive( name);
        }

    }

    private void downloadArchive(Feature feature) throws Exception {
        for (Dependency dependency : feature.getDependencies()) {
//            VersionRange range = "0.0.0".equals(dependency.getVersion()) ? VersionRange.ANY_VERSION : new VersionRange(
//                dependency.getVersion(), true, true);
            Feature fi = null;
            Map<String, Feature> avail = getFeatures().get(dependency.getName());
            if (avail != null) {
                for (Feature f : avail.values()) {
//                    Version v = VersionTable.getVersion(f.getVersion());
//                    if (range.contains(v)) {
//                        if (fi == null || VersionTable.getVersion(fi.getVersion()).compareTo(v) < 0) {
                            fi = f;
//                        }
//                    }
                }
            }
            if (fi == null) {
                throw new Exception("No feature named '" + dependency.getName() + "' with version '" + dependency.getVersion() + "' available");
            }
            if (!(fi.getName().equals(feature.getName()) && fi.getVersion().equals(feature.getVersion()))) {
                downloadArchive( fi);
            }
        }
        for (ConfigFileInfo configFile : feature.getConfigurationFiles()) {
            downLoadConfigurationFile(configFile.getLocation(),
                    configFile.getFinalname(), configFile.isOverride());
        }
        for (BundleInfo bInfo : resolve(feature)) {
            try {
                downLoadBundle(bInfo);
            } catch (Exception e) {
                System.out.println("\u001B[31m"+e.getMessage()+"\u001B[0m");
                LOGGER.error(e.getMessage()); 
            }
        }
    }

   
    /**
     * @param bInfo
     * @throws IOException 
     */
    private void downLoadBundle(BundleInfo bInfo) throws IOException {
        String location = bInfo.getLocation();
        String finalname = getDownLoadedFile(location);
        downLoadFile(location,finalname);
    }
    
    
   

    /**
     * @param location
     * @param finalname
     * @param override
     * @throws IOException 
     */
    private void downLoadConfigurationFile(String location, String name, boolean override) throws IOException {
        String finalname = getDownLoadedFile(location);
        downLoadFile(location,finalname);
        
    }
    /**
     * @param feature
     * @return
     * @throws Exception 
     */
    private List<BundleInfo> resolve(Feature feature) throws Exception {
        String resolver = feature.getResolver();
        // If no resolver is specified, we expect a list of uris
        if (resolver == null || resolver.length() == 0) {
            return feature.getBundles();
        }
        boolean optional = false;
        if (resolver.startsWith("(") && resolver.endsWith(")")) {
            resolver = resolver.substring(1, resolver.length() - 1);
            optional = true;
        }
        // Else, find the resolver
        String filter = "(&(" + Constants.OBJECTCLASS + "=" + Resolver.class.getName() + ")(name=" + resolver + "))";
        ServiceTracker tracker = new ServiceTracker(bundleContext, FrameworkUtil.createFilter(filter), null);
        tracker.open();
        try {
            if (optional) {
                Resolver r = (Resolver) tracker.getService();
                if (r != null) {
                    return r.resolve(feature);
                } else {
                    LOGGER.debug("Optional resolver '" + resolver + "' not found, using the default resolver");
                    return feature.getBundles();
                }
            } else {
                Resolver r = (Resolver) tracker.waitForService(5000);
                if (r == null) {
                    throw new Exception("Unable to find required resolver '" + resolver + "'");
                }
                return r.resolve(feature);
            }
        } finally {
            tracker.close();
        }
    }

 

    /**
     * @param name
     */
    private void downloadArchive(String name) {
        try {
            Feature feature = service.getFeature(name);
            downloadArchive(feature);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    protected Map<String, Map<String, Feature>> getFeatures() throws Exception {
        if (allFeatures == null) {
            // the outer map's key is feature name, the inner map's key is feature version
            Map<String, Map<String, Feature>> map = new HashMap<String, Map<String, Feature>>();
            // Two phase load:
            // * first load dependent repositories
            Repository[] repos = service.listRepositories();
            Map<URI, Repository> repositories = new HashMap<URI, Repository>();
            for (Repository repo : repos) {
                repositories.put(repo.getURI(), repo);
            }
            // * then load all features
            for (Repository repo : repositories.values()) {
                for (Feature f : repo.getFeatures()) {
                    if (map.get(f.getName()) == null) {
                        Map<String, Feature> versionMap = new HashMap<String, Feature>();
                        versionMap.put(f.getVersion(), f);
                        map.put(f.getName(), versionMap);
                    } else {
                        map.get(f.getName()).put(f.getVersion(), f);
                    }
                }
            }
            allFeatures = map;
        }
        return allFeatures;
    }
}
