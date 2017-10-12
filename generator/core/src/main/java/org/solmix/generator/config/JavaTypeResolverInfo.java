package org.solmix.generator.config;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;


public class JavaTypeResolverInfo extends TypedPropertyHolder
{

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaTypeResolver"); //$NON-NLS-1$
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); //$NON-NLS-1$
        }

        addPropertyXmlElements(answer);

        return answer;
    }

}
