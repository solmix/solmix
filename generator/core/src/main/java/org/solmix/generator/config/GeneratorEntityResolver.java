package org.solmix.generator.config;

import java.io.IOException;
import java.net.URL;

import org.solmix.commons.util.ClassLoaderUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class GeneratorEntityResolver implements EntityResolver
{

    public static final String XSD = "org/solmix/generator/config/generator-config-1.0.0.xsd";


    public static final String ID = "http://www.solmix.org/schema/generator-datax/v1.0.0";


    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        URL url = null;
        if (ID.equals(systemId)) {
            url = ClassLoaderUtils.getResource(XSD, GeneratorEntityResolver.class);
        } 
        if (url == null) {
            throw new IllegalArgumentException("Can't found validate XSD for systemId:" + systemId);
        }
        InputSource in = new InputSource(url.openStream());
        return in;
    }

}
