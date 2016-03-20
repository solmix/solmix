
package org.solmix.service.template.support;


import java.util.ArrayList;
import java.util.List;

import org.solmix.commons.util.Assert;
public class SearchExtensionsStrategy implements TemplateSearchingStrategy {
    private final String[] availableExtensions;

    public SearchExtensionsStrategy(String[] extensions) {
        this.availableExtensions = Assert.assertNotNull(extensions, "extensions");
    }

    public Object getKey(String templateName) {
        return null;
    }

    public boolean findTemplate(TemplateMatcher matcher) {
        List<String> testedExtensions = new ArrayList<String>(availableExtensions.length);
        boolean found = false;
        String ext = matcher.getExtension();

        if (ext != null) {
            testedExtensions.add(ext);
            found = matcher.findTemplate();
        }

        for (int i = 0; !found && i < availableExtensions.length; i++) {
            ext = availableExtensions[i];

            if (!testedExtensions.contains(ext)) {
                testedExtensions.add(ext);
                matcher.setExtension(ext);
                found = matcher.findTemplate();
            }
        }

        return found;
    }
}
