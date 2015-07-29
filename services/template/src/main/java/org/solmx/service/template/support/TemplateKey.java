
package org.solmx.service.template.support;

import java.util.Arrays;

import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;

public final class TemplateKey {
    private final String   templateNameWithoutExtension;
    private final String   extension;
    private final Object[] strategyKeys;

    public TemplateKey(String templateName, TemplateSearchingStrategy[] strategies) {
        templateName = Assert.assertNotNull(StringUtils.trimToNull(Files.normalizeAbsolutePath(templateName,false)), "illegal templateName: %s",
                                     templateName);

        org.solmix.commons.util.Files.FileNameAndExtension names = Files.getFileNameAndExtension(templateName, true);

        this.templateNameWithoutExtension = names.getFileName();
        this.extension = names.getExtension();

        if (ArrayUtils.isEmptyArray(strategies)) {
            this.strategyKeys = ObjectUtils.EMPTY_OBJECT_ARRAY;
        } else {
            this.strategyKeys = new Object[strategies.length];

            for (int i = 0; i < strategies.length; i++) {
                strategyKeys[i] = strategies[i].getKey(getTemplateName());
            }
        }
    }

    public String getTemplateName() {
        return getTemplateName(templateNameWithoutExtension, extension);
    }

    public String getTemplateNameWithoutExtension() {
        return templateNameWithoutExtension;
    }

    public String getExtension() {
        return extension;
    }

    public Object[] getStrategyKeys() {
        return strategyKeys.clone();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (!(other instanceof TemplateKey)) {
            return false;
        }

        TemplateKey otherKey = (TemplateKey) other;

        if (!ObjectUtils.isEquals(templateNameWithoutExtension, otherKey.templateNameWithoutExtension)) {
            return false;
        }

        if (!ObjectUtils.isEquals(extension, otherKey.extension)) {
            return false;
        }

        if (!Arrays.equals(strategyKeys, otherKey.strategyKeys)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (extension == null ? 0 : extension.hashCode());
        result = prime * result + Arrays.hashCode(strategyKeys);
        result = prime * result + (templateNameWithoutExtension == null ? 0 : templateNameWithoutExtension.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return getTemplateName() + Arrays.toString(strategyKeys);
    }

    public static String getTemplateName(String templateNameWithoutExtension, String extension) {
        String templateName = templateNameWithoutExtension;

        if (!StringUtils.isEmpty(extension)) {
            templateName = templateNameWithoutExtension + "." + extension;
        }

        return templateName;
    }
}
