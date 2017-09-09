package org.solmix.karaf.plugin;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.apache.karaf.deployer.blueprint.BlueprintURLHandler;
import org.apache.karaf.deployer.features.FeatureURLHandler;
import org.apache.karaf.deployer.spring.SpringURLHandler;

public class CustomBundleUrlStreamHandlerFactory implements URLStreamHandlerFactory {

    private static final String MVN_URI_PREFIX = "mvn";
    private static final String WRAP_URI_PREFIX = "wrap";
    private static final String FEATURE_URI_PREFIX = "feature";
    private static final String SPRING_URI_PREFIX = "spring";
    private static final String BLUEPRINT_URI_PREFIX = "blueprint";

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        switch (protocol) {
            case MVN_URI_PREFIX:
                return new org.ops4j.pax.url.mvn.Handler();
            case WRAP_URI_PREFIX:
                return new org.ops4j.pax.url.wrap.Handler();
            case FEATURE_URI_PREFIX:
                return new FeatureURLHandler();
            case SPRING_URI_PREFIX:
                return new SpringURLHandler();
            case BLUEPRINT_URI_PREFIX:
                return new BlueprintURLHandler();
            default:
                return null;
        }
    }

}

