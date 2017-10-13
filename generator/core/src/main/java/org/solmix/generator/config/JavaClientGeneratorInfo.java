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
        XmlElement answer = new XmlElement("javaClientGenerator"); 
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); 
        }

        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage)); 
        }

        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject)); 
        }

        if (implementationPackage != null) {
            answer.addAttribute(new Attribute(
                    "implementationPackage", targetProject)); 
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
            errors.add(Messages.getString("ValidationError.2", contextId)); 
        }

        if (!StringUtils.stringHasValue(targetPackage)) {
            errors.add(Messages.getString("ValidationError.12", 
                    "javaClientGenerator", contextId)); 
        }

        if (!StringUtils.stringHasValue(getType())) {
            errors.add(Messages.getString("ValidationError.20", 
                    contextId));
        }
    }
}
