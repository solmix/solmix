package org.solmix.generator.config;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Element;
import org.solmix.commons.xml.dom.XmlElement;


public class CommentGeneratorInfo extends TypedPropertyHolder
{

    public Element toXmlElement() {
        XmlElement answer = new XmlElement("commentGenerator"); 
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

}
