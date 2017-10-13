package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;


public class ConnectionFactoryInfo extends TypedPropertyHolder
{

    public void validate(List<String> errors) {
        if (getType() == null || "DEFAULT".equals(getType())) { 
            if (!StringUtils.stringHasValue(getProperty("driverClass"))) { 
                errors.add(Messages.getString("ValidationError.18", "connectionFactory", "driverClass"));  //$NON-NLS-2$ //$NON-NLS-3$
            }

            if (!StringUtils.stringHasValue(getProperty("connectionURL"))) { 
                errors.add(Messages.getString("ValidationError.18", "connectionFactory", "connectionURL"));  //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("connectionFactory"); 

        if (StringUtils.stringHasValue(getType())) {
            xmlElement.addAttribute(new Attribute("type", getType())); 
        }

        addPropertyXmlElements(xmlElement);

        return xmlElement;
    }

}
