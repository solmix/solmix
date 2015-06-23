package org.solmix.runtime.resource.support;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.AntMatcher;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.Files;
import org.solmix.runtime.resource.InputStreamResource;
import org.springframework.util.StringUtils;

public class PathMatchingResourceResolver extends ResourceResolverAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(PathMatchingResourceResolver.class);
	private ClassLoader classLoader;
	private AntMatcher matcher;
	
	public PathMatchingResourceResolver(){
		this(ClassLoaderUtils.getDefaultClassLoader());
	}
	public PathMatchingResourceResolver(ClassLoader classLoader){
		this.classLoader=classLoader;
		matcher=new AntMatcher();
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
				return new URLResource(url);
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
				return new InputStreamResource[] {getAsStream(locationPattern)};
			}
		}
	}

    protected InputStreamResource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        Set<InputStreamResource> result = new LinkedHashSet<InputStreamResource>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        return result.toArray(new InputStreamResource[result.size()]);
    }

    protected URLResource convertClassLoaderURL(URL url) {
        return new URLResource(url);
    }
    protected InputStreamResource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        InputStreamResource[] rootDirResources = getAsStreams(rootDirPath);
        Set<InputStreamResource> result = new LinkedHashSet<InputStreamResource>(16);
        for (InputStreamResource rootDirResource : rootDirResources) {
              if (isJarResource(rootDirResource)) {
                    result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
              }
              else {
                    result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
              }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return result.toArray(new InputStreamResource[result.size()]);
    }

    
    protected Set<InputStreamResource> doFindPathMatchingFileResources(InputStreamResource rootDirResource, String subPattern) throws IOException {
        File rootDir;
        try {
              rootDir = rootDirResource.getFile().getAbsoluteFile();
        }
        catch (IOException ex) {
              if (LOG.isWarnEnabled()) {
                  LOG.warn("Cannot search for matching files underneath " + rootDirResource +
                                " because it does not correspond to a directory in the file system", ex);
              }
              return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }
    
    protected Set<InputStreamResource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for matching resources in directory tree [" + rootDir.getPath() + "]");
        }
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<InputStreamResource> result = new LinkedHashSet<InputStreamResource>(matchingFiles.size());
        for (File file : matchingFiles) {
              result.add(new FileResource(file));
        }
        return result;
  }
    
    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
        if (!rootDir.exists()) {
              // Silently skip non-existing directories.
              if (LOG.isDebugEnabled()) {
                    LOG.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
              }
              return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
              // Complain louder if it exists but is no directory.
              if (LOG.isWarnEnabled()) {
                    LOG.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
              }
              return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
              if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() +
                                "] because the application is not allowed to read the directory");
              }
              return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
              fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<File>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
  }
    
    protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
        if (LOG.isDebugEnabled()) {
              LOG.debug("Searching directory [" + dir.getAbsolutePath() +
                          "] for files matching pattern [" + fullPattern + "]");
        }
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
              if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
              }
              return;
        }
        for (File content : dirContents) {
              String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
              if (content.isDirectory() &&matcher.matchStart(fullPattern, currPath + "/")) {
                    if (!content.canRead()) {
                          if (LOG.isDebugEnabled()) {
                                LOG.debug("Skipping subdirectory [" + dir.getAbsolutePath() +
                                            "] because the application is not allowed to read the directory");
                          }
                    }
                    else {
                          doRetrieveMatchingFiles(fullPattern, content, result);
                    }
              }
              if (matcher.match(fullPattern, currPath)) {
                    result.add(content);
              }
        }
  }
    
    protected Collection<? extends InputStreamResource> doFindPathMatchingJarResources(InputStreamResource rootDirResource, String subPattern) throws IOException {
        URLConnection con = rootDirResource.getURL().openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean newJarFile = false;

        if (con instanceof JarURLConnection) {
              // Should usually be the case for traditional JAR files.
              JarURLConnection jarCon = (JarURLConnection) con;
              jarFile = jarCon.getJarFile();
              jarFileUrl = jarCon.getJarFileURL().toExternalForm();
              JarEntry jarEntry = jarCon.getJarEntry();
              rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        }
        else {
              // No JarURLConnection -> need to resort to URL file parsing.
              // We'll assume URLs of the format "jar:path!/entry", with the protocol
              // being arbitrary as long as following the entry format.
              // We'll also handle paths with and without leading "file:" prefix.
              String urlFile = rootDirResource.getURL().getFile();
              int separatorIndex = urlFile.indexOf(Files.JAR_URL_SEPARATOR);
              if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + Files.JAR_URL_SEPARATOR.length());
                    jarFile = getJarFile(jarFileUrl);
              }
              else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
              }
              newJarFile = true;
        }

        try {
              if (LOG.isDebugEnabled()) {
                  LOG.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
              }
              if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                    // Root entry path must end with slash to allow for proper matching.
                    // The Sun JRE does not return a slash here, but BEA JRockit does.
                    rootEntryPath = rootEntryPath + "/";
              }
              Set<InputStreamResource> result = new LinkedHashSet<InputStreamResource>(8);
              for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                    JarEntry entry = entries.nextElement();
                    String entryPath = entry.getName();
                    if (entryPath.startsWith(rootEntryPath)) {
                          String relativePath = entryPath.substring(rootEntryPath.length());
                          if (matcher.match(subPattern, relativePath)) {
                                result.add(rootDirResource.createRelative(relativePath));
                          }
                    }
              }
              return result;
        }
        finally {
              // Close jar file, but only if freshly obtained -
              // not from JarURLConnection, which might cache the file reference.
              if (newJarFile) {
                    jarFile.close();
              }
        }
    }
    
    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(Files.FILE_URL_PREFIX)) {
              try {
                    return new JarFile(Files.toURI(jarFileUrl).getSchemeSpecificPart());
              }
              catch (URISyntaxException ex) {
                    // Fallback for URLs that are not valid URIs (should hardly ever happen).
                    return new JarFile(jarFileUrl.substring(Files.FILE_URL_PREFIX.length()));
              }
        }
        else {
              return new JarFile(jarFileUrl);
        }
  }
    
    protected boolean isJarResource(InputStreamResource resource) throws IOException {
        
        return Files.isJarURL(resource.getURL());
  }
    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }
    private boolean isPattern(String path) {
		return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
	}
	@Override
	public <T> T resolve(String resourceName, Class<T> resourceType) {
		return null;
	}
}
