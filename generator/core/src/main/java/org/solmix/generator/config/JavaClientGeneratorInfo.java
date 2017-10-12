package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;


public class JavaClientGeneratorInfo extends TypedPropertyHolder
{

    private String targetPackage;
    private String implementationPackage;
    private String targetProject;

    /**
     *  
     */
    public JavaClientGeneratorInfo() {
        super();
    }

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaClientGenerator"); //$NON-NLS-1$
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); //$NON-NLS-1$
        }

        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage)); //$NON-NLS-1$
        }

        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject)); //$NON-NLS-1$
        }

        if (implementationPackage != null) {
            answer.addAttribute(new Attribute(
                    "implementationPackage", targetProject)); //$NON-NLS-1$
        }

        addPropertyXmlElements(answer);

        return answer;
    }

    public String getImplementationPackage() {
        return implementationPackage;
    }

    public void setImplementationPackage(String implementationPackage) {
        this.implementationPackage = implementationPackage;
    }

    public void validate(List<String> errors, String contextId) {
        if (!StringUtils.stringHasValue(targetProject)) {
            errors.add(Messages.getString("ValidationError.2", contextId)); //$NON-NLS-1$
        }

        if (!StringUtils.stringHasValue(targetPackage)) {
            errors.add(Messages.getString("ValidationError.12", //$NON-NLS-1$
                    "javaClientGenerator", contextId)); //$NON-NLS-1$
        }

        if (!StringUtils.stringHasValue(getType())) {
            errors.add(Messages.getString("ValidationError.20", //$NON-NLS-1$
                    contextId));
        }
    }
}
