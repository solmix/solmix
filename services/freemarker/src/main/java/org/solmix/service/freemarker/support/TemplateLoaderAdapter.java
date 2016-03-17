
package org.solmix.service.freemarker.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;

import freemarker.cache.TemplateLoader;

public class TemplateLoaderAdapter implements TemplateLoader
{

    private ResourceManager resourceManager;

    private String path;

    public TemplateLoaderAdapter(ResourceManager resourceManager, String path)
    {
        this.resourceManager = resourceManager;
        path = FileUtils.normalizeAbsolutePath(path, true);
        Assert.assertNotNull(path);
        if(!path.endsWith("/"))
            path = path + '/';
        this.path=path;
    }
    
    public String getPath() {
        return path;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        InputStreamResource resource= resourceManager.getResourceAsStream(path+normalizeTemplateName(name));
        
        if(resource==null||!resource.exists()){
            return null;
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        long lastModified;

        try {
            lastModified = getTemplateSource(templateSource).getResource().lastModified();

            if (lastModified <= 0) {
                lastModified = -1; // not supported
            }
        } catch (IOException e) {
            lastModified = -1;
        }

        return lastModified;
    }
    
    private TemplateSource getTemplateSource(Object templateSource) {
        return (TemplateSource) Assert.assertNotNull(templateSource, "templateSource");
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new InputStreamReader(getTemplateSource(templateSource).getInputStream(), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        getTemplateSource(templateSource).close();
    }
    
    private String normalizeTemplateName(String templateName) {
        templateName = Assert.assertNotNull(StringUtils.trimToNull(templateName), "templateName");

        if (templateName.startsWith("/")) {
            templateName = templateName.substring(1);
        }

        return templateName;
    }
    /** 保存resource已经打开的流，以便关闭。 */
    public static class TemplateSource {
        private final InputStreamResource    resource;
        private       InputStream istream;

        public TemplateSource(InputStreamResource resource) {
            this.resource = Assert.assertNotNull(resource, "resource");
        }

        public InputStreamResource getResource() {
            return resource;
        }

        public InputStream getInputStream() throws IOException {
            if (istream == null) {
                istream = resource.getInputStream();
            }

            return istream;
        }

        public void close() {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                }

                istream = null;
            }
        }

        @Override
        public int hashCode() {
            return 31 + (resource == null ? 0 : resource.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            TemplateSource other = (TemplateSource) obj;

            if (resource == null) {
                if (other.resource != null) {
                    return false;
                }
            } else if (!resource.equals(other.resource)) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return resource.toString();
        }
    }
}
