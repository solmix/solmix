package org.solmix.service.freemarker.support;

import java.io.IOException;
import java.io.InputStream;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.resource.InputStreamResource;

public class TemplateSource
{
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
