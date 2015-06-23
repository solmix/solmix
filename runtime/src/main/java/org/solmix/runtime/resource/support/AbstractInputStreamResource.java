package org.solmix.runtime.resource.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;

public abstract  class AbstractInputStreamResource implements InputStreamResource {

	@Override
	public boolean exists() {
		try {
			return getFile().exists();
		}
		catch (IOException ex) {
			// Fall back to stream existence: can we open the stream?
			try {
				InputStream is = getInputStream();
				is.close();
				return true;
			}
			catch (Throwable isEx) {
				return false;
			}
		}
	}

	@Override
	public boolean isReadable() {
		 return true;
	}

	@Override
	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	@Override
	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
	}

	@Override
	public URI getURI() throws IOException {
		URL url = getURL();
		try {
			return new URI(StringUtils.replace(url.toString(), " ", "%20"));
		}
		catch (URISyntaxException ex) {
			throw new IOException("Invalid URI [" + url + "]", ex);
		}
	}

	@Override
	public long lastModified() throws IOException {
		long lastModified = getFile().lastModified();
		if (lastModified == 0L) {
			throw new FileNotFoundException(getDescription() +
					" cannot be resolved in the file system for resolving its last-modified timestamp");
		}
		return lastModified;
	}

	@Override
	public String getFilename() {
		return null;
	}
	public InputStreamResource createRelative(String relativePath) throws IOException{
	    throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
	 }

}
