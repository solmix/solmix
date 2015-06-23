package org.solmix.runtime.resource.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ClassLoaderResource extends AbstractFileStreamResource {

	private String path;
	private ClassLoader classLoader;
	private Class<?> clazz;

	public ClassLoaderResource(String path, ClassLoader classLoader) {
		Assert.assertNotNull(path, "Path must not be null");
		String pathToUse =Files.normalizeAbsolutePath(path, true);
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		this.path = pathToUse;
		this.classLoader = (classLoader != null ? classLoader : ClassLoaderUtils.getDefaultClassLoader());
	}
	protected ClassLoaderResource(String path, ClassLoader classLoader,
			Class<?> clazz) {
		this.path = Files.normalizeAbsolutePath(path, true);
		this.classLoader = classLoader;
		this.clazz = clazz;
	}
	@Override
      public ClassLoaderResource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
            return new ClassLoaderResource(pathToUse, this.classLoader, this.clazz);
      }
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		} else if (this.classLoader != null) {
			is = this.classLoader.getResourceAsStream(this.path);
		} else {
			is = ClassLoader.getSystemResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be opened because it does not exist");
		}
		return is;
	}

	@Override
	public URL getURL() throws IOException {
		URL url = resolveURL();
		if (url == null) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	protected URL resolveURL() {
		if (this.clazz != null) {
			return this.clazz.getResource(this.path);
		} else if (this.classLoader != null) {
			return this.classLoader.getResource(this.path);
		} else {
			return ClassLoader.getSystemResource(this.path);
		}
	}

	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder("class path resource [");
		String pathToUse = path;
		if (this.clazz != null && !pathToUse.startsWith("/")) {
			builder.append(this.clazz.getName());
			builder.append('/');
		}
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		builder.append(pathToUse);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ClassLoaderResource) {
			ClassLoaderResource otherRes = (ClassLoaderResource) obj;
			return (this.path.equals(otherRes.path)
					&& ObjectUtils.nullSafeEquals(this.classLoader,
							otherRes.classLoader) && ObjectUtils
						.nullSafeEquals(this.clazz, otherRes.clazz));
		}
		return false;
	}

	public String getFilename() {
		return Files.getFilename(this.path);
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public boolean exists() {
		return (resolveURL() != null);
	}

	public final String getPath() {
		return this.path;
	}


	public final ClassLoader getClassLoader() {
		return (this.clazz != null ? this.clazz.getClassLoader()
				: this.classLoader);
	}
}
