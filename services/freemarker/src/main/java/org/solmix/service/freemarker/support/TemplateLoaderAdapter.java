
package org.solmix.service.freemarker.support;

import java.io.IOException;
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
        path = FileUtils.normalizePath(path, true);
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
        return  new TemplateSource(resource);
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

}
