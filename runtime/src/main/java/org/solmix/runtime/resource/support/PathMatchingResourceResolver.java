package org.solmix.runtime.resource.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.Files;
import org.solmix.runtime.resource.InputStreamResource;
import org.springframework.core.io.UrlResource;

public class PathMatchingResourceResolver extends ResourceResolverAdaptor {

	private ClassLoader classLoader;
	
	public PathMatchingResourceResolver(){
		this(ClassLoaderUtils.getDefaultClassLoader());
	}
	public PathMatchingResourceResolver(ClassLoader classLoader){
		this.classLoader=classLoader;
	}

	@Override
	public InputStreamResource getAsStream(String location) {
		Assert.assertNotNull(location, "Location must not be null");
		if (location.startsWith("/")) {
			return getResourceByPath(location);
		}
		else if (location.startsWith(Files.CLASSPATH_URL_PREFIX)) {
			return new ClassLoaderResource(location.substring(Files.CLASSPATH_URL_PREFIX.length()), getClassLoader());
		}
		else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			}
			catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	private InputStreamResource getResourceByPath(String location) {
		return new ClassLoaderResource(location, getClassLoader());
	}
	
	public  ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public InputStreamResource[] getAsStreams(String locationPattern) throws IOException {
		Assert.assertNotNull(locationPattern,"locationPattern is null");
		if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
			if (isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
				// a class path resource pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// all class path resources with the given name
				return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
			}
		}
		else {
			// Only look for a pattern after a prefix here
			// (to not get fooled by a pattern symbol in a strange prefix).
			int prefixEnd = locationPattern.indexOf(":") + 1;
			if (isPattern(locationPattern.substring(prefixEnd))) {
				// a file pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// a single resource with the given name
				return new InputStreamResource[] {getResourceLoader().getResource(locationPattern)};
			}
		}
		return null;
	}

	private boolean isPattern(String path) {
		return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
	}
	@Override
	public <T> T resolve(String resourceName, Class<T> resourceType) {
		return null;
	}
}
