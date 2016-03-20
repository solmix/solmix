
package org.solmix.service.template.support;

import org.solmix.commons.util.Assert;


public class DefaultExtensionStrategy implements TemplateSearchingStrategy {
    private final String defaultExtension;

    public DefaultExtensionStrategy(String defaultExtension) {
        this.defaultExtension = Assert.assertNotNull(defaultExtension, "defaultExtension");
    }

    public Object getKey(String templateName) {
        return null;
    }

    public boolean findTemplate(TemplateMatcher matcher) {
        if (matcher.getExtension() == null) {
            matcher.setExtension(defaultExtension);
        }

        return matcher.findTemplate();
    }
}
