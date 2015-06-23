/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.runtime.resource.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Files;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月23日
 */

public class URLResource extends AbstractFileStreamResource
{

    private final URI uri;

    private final URL url;

    private final URL cleanedUrl;

    public URLResource(URI uri) throws MalformedURLException
    {
        Assert.assertNotNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
    }
    public URLResource(URL url) {
        Assert.assertNotNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }
    public URLResource(String protocol, String location) throws MalformedURLException  {
        this(protocol, location, null);
  }
    public URLResource(String protocol, String location, String fragment) throws MalformedURLException  {
        try {
              this.uri = new URI(protocol, location, fragment);
              this.url = this.uri.toURL();
              this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
        }
        catch (URISyntaxException ex) {
              MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
              exToThrow.initCause(ex);
              throw exToThrow;
        }
  }
    public URLResource(String path) throws MalformedURLException {
        Assert.assertNotNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
  }
    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        try {
            return new URL(Files.normalizeAbsolutePath(originalPath,true));
        } catch (MalformedURLException ex) {
            // Cleaned URL path cannot be converted to URL
            // -> take original URL.
            return originalUrl;
        }
    }
    @Override
    public URLResource createRelative(String relativePath) throws MalformedURLException {
          if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
          }
          return new URLResource(new URL(this.url, relativePath));
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
          URLConnection con = this.url.openConnection();
          try {
                return con.getInputStream();
          }
          catch (IOException ex) {
                // Close the HTTP connection (if applicable).
                if (con instanceof HttpURLConnection) {
                      ((HttpURLConnection) con).disconnect();
                }
                throw ex;
          }
    }
    
    @Override
    public URL getURL() throws IOException {
          return this.url;
    }
    
    @Override
    public URI getURI() throws IOException {
          if (this.uri != null) {
                return this.uri;
          }
          else {
                return super.getURI();
          }
    }
    
    @Override
    public File getFile() throws IOException {
          if (this.uri != null) {
                return super.getFile(this.uri);
          }
          else {
                return super.getFile();
          }
    }
    
    @Override
    public String getFilename() {
          return new File(this.url.getFile()).getName();
    }
    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }
    @Override
    public boolean equals(Object obj) {
          return (obj == this ||
                (obj instanceof URLResource && this.cleanedUrl.equals(((URLResource) obj).cleanedUrl)));
    }

    
    @Override
    public int hashCode() {
          return this.cleanedUrl.hashCode();
    }

}
