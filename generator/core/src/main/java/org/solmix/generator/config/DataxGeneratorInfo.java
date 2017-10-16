package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;


public class DataxGeneratorInfo extends PropertyHolder
{
    private String targetPackage;

    private String targetProject;
    
    private String xmlPackage;

    /**
     * 
     */
    public DataxGeneratorInfo() {
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
    

    
    public String getXmlPackage() {
        return xmlPackage;
    }

    
    public void setXmlPackage(String xmlPackage) {
        this.xmlPackage = xmlPackage;
    }

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaModelGenerator"); 

        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage)); 
        }
        if (xmlPackage != null) {
            answer.addAttribute(new Attribute("xmlPackage", xmlPackage)); 
        }
        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject)); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

    public void validate(List<String> errors, String contextId) {
        if (!StringUtils.stringHasValue(targetProject)) {
            errors.add(Messages.getString("ValidationError.0", contextId)); 
        }

        if (!StringUtils.stringHasValue(targetPackage)) {
            errors.add(Messages.getString("ValidationError.12", 
                    "JavaModelGenerator", contextId)); 
        }
    }
}
