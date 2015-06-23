package org.solmix.runtime.resource.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.StringUtils;

public  abstract class AbstractFileStreamResource extends AbstractInputStreamResource {
	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		Assert.assertNotNull(url, "Resource URL must not be null");
		if (!"file".equals(url.getProtocol())) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + url);
		}
		try {
			return new File(new URI(StringUtils.replace(url.toString(), " ", "%20")).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			return new File(url.getFile());
		}
	}
	
	protected File getFile(URI uri) throws IOException {
         
          return Files.getFile(uri, getDescription());
	}
	
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = getURL();
		if (Files.isJarURL(url)) {
			URL actualUrl = Files.extractJarFileURL(url);
			
			return Files.getFile(actualUrl, "Jar URL");
		}
		else {
			return getFile();
		}
	}
	

	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		if (Files.isFileURL(url) || Files.isJarURL(url)) {
			// Proceed with file system resolution...
			return super.lastModified();
		}
		else {
			// Try a URL connection last-modified header...
			URLConnection con = url.openConnection();
			return con.getLastModified();
		}
	}
	
	@Override
	public boolean isReadable() {
		try {
			URL url = getURL();
			if (Files.isFileURL(url)) {
				// Proceed with file system resolution...
				File file = getFile();
				return (file.canRead() && !file.isDirectory());
			}
			else {
				return true;
			}
		}
		catch (IOException ex) {
			return false;
		}
	}
}
