package org.solmix.generator.config;

import java.util.ArrayList;
import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Document;
import org.solmix.commons.xml.dom.XmlElement;


public class ConfigurationInfo
{

    private List<String> classPathEntries;
    private List<DomainInfo> domains;
    public ConfigurationInfo() {
        super();
        domains = new ArrayList<DomainInfo>();
        classPathEntries = new ArrayList<String>();
    }
    public void addClasspathEntry(String entry) {
        classPathEntries.add(entry);
    }
    public List<String> getClassPathEntries() {
        return classPathEntries;
    }
    public void validate() throws InvalidConfigurationException {
        List<String> errors = new ArrayList<String>();

        for (String classPathEntry : classPathEntries) {
            if (!StringUtils.isEmail(classPathEntry)) {
                errors.add("classPathEntry is empty"); 
                // only need to state this error once
                break;
            }
        }

        if (domains.size() == 0) {
            errors.add("no <domain>"); 
        } else {
            for (DomainInfo context : domains) {
                context.validate(errors);
            }
        }

        if (errors.size() > 0) {
            throw new InvalidConfigurationException(errors);
        }
    }

    /**
     * Gets the contexts.
     *
     * @return the contexts
     */
    public List<DomainInfo> getDomains() {
        return domains;
    }
    
    /**
     * Adds the context.
     *
     * @param context
     *            the context
     */
    public void addDomain(DomainInfo context) {
        domains.add(context);
    }

    /**
     * Gets the context.
     *
     * @param id
     *            the id
     * @return the context
     */
    public DomainInfo getDomain(String id) {
        for (DomainInfo domain : domains) {
            if (id.equals(domain.getId())) {
                return domain;
            }
        }

        return null;
    }
    
    public Document toDocument() {
        Document document = new Document( GeneratorEntityResolver.ID,GeneratorEntityResolver.ID);
    XmlElement rootElement = new XmlElement("configuration"); 
    document.setRootElement(rootElement);

    for (String classPathEntry : classPathEntries) {
        XmlElement cpeElement = new XmlElement("classPathEntry"); 
        cpeElement.addAttribute(new Attribute("location", classPathEntry)); 
        rootElement.addElement(cpeElement);
    }

    for (DomainInfo context : domains) {
        rootElement.addElement(context.toXmlElement());
    }

    return document;
        
    }
}
