package org.solmix.generator.config;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;


public class JavaTypeResolverInfo extends TypedPropertyHolder
{

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaTypeResolver"); 
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

}
