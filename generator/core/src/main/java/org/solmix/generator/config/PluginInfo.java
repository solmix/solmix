
package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Element;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;

public class PluginInfo extends TypedPropertyHolder
{

    public void validate(List<String> errors, String id) {
        if (!StringUtils.stringHasValue(getType())) {
            errors.add(Messages.getString("ValidationError.17", 
                id));
        }

    }

    public Element toXmlElement() {
        XmlElement answer = new XmlElement("plugin"); 
        if (getType() != null) {
            answer.addAttribute(new Attribute("type", getType())); 
        }

        addPropertyXmlElements(answer);

        return answer;
    }

}
