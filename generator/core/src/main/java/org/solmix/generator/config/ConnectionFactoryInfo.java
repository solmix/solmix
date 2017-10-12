package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;


public class ConnectionFactoryInfo extends TypedPropertyHolder
{

    public void validate(List<String> errors) {
        if (getType() == null || "DEFAULT".equals(getType())) { //$NON-NLS-1$
            if (!StringUtils.stringHasValue(getProperty("driverClass"))) { //$NON-NLS-1$
                errors.add(Messages.getString("ValidationError.18", "connectionFactory", "driverClass")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            if (!StringUtils.stringHasValue(getProperty("connectionURL"))) { //$NON-NLS-1$
                errors.add(Messages.getString("ValidationError.18", "connectionFactory", "connectionURL")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("connectionFactory"); //$NON-NLS-1$

        if (StringUtils.stringHasValue(getType())) {
            xmlElement.addAttribute(new Attribute("type", getType())); //$NON-NLS-1$
        }

        addPropertyXmlElements(xmlElement);

        return xmlElement;
    }

}
