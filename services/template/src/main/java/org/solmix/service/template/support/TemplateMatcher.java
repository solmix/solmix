
package org.solmix.service.template.support;

import org.solmix.service.template.TemplateEngine;


public abstract class TemplateMatcher implements TemplateMatchResult {
    private final String         originalTemplateName;
    private final String         originalTemplateNameWithoutExtension;
    private final String         originalExtension;
    private       String         templateNameWithoutExtension;
    private       String         extension;
    private       TemplateEngine engine;

    public TemplateMatcher(TemplateKey key) {
        this.originalTemplateName = key.getTemplateName();
        this.originalTemplateNameWithoutExtension = key.getTemplateNameWithoutExtension();
        this.originalExtension = key.getExtension();

        this.templateNameWithoutExtension = key.getTemplateNameWithoutExtension();
        this.extension = key.getExtension();
    }

    public String getOriginalTemplateName() {
        return originalTemplateName;
    }

    public String getOriginalTemplateNameWithoutExtension() {
        return originalTemplateNameWithoutExtension;
    }

    public String getOriginalExtension() {
        return originalExtension;
    }

    public String getTemplateName() {
        return TemplateKey.getTemplateName(templateNameWithoutExtension, extension);
    }

    public String getTemplateNameWithoutExtension() {
        return templateNameWithoutExtension;
    }

    public void setTemplateNameWithoutExtension(String templateNameWithoutExtension) {
        this.templateNameWithoutExtension = templateNameWithoutExtension;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public TemplateEngine getEngine() {
        return engine;
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    public abstract boolean findTemplate();

    @Override
    public String toString() {
        return "TemplateMatcher[" + originalTemplateName + "]";
    }
}
