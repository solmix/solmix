package org.solmix.generator.config;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Element;
import org.solmix.commons.xml.dom.XmlElement;


public class CommentGeneratorInfo extends TypedPropertyHolder
{

    public Element toXmlElement() {
        XmlElement answer = new XmlElement("commentGenerator"); //$NON-NLS-1$
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); //$NON-NLS-1$
        }

        addPropertyXmlElements(answer);

        return answer;
    }

}
