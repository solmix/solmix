package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;


public class SqlMapGeneratorInfo extends PropertyHolder
{

    private String targetPackage;

    private String targetProject;

    /**
     *  
     */
    public SqlMapGeneratorInfo() {
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
        XmlElement answer = new XmlElement("sqlMapGenerator"); 

        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage)); 
        }

        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject)); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

    public void validate(List<String> errors, String contextId) {
        if (!StringUtils.stringHasValue(targetProject)) {
            errors.add(Messages.getString("ValidationError.1", contextId)); 
        }

        if (!StringUtils.stringHasValue(targetPackage)) {
            errors.add(Messages.getString("ValidationError.12", 
                    "SQLMapGenerator", contextId)); 
        }
    }

}
