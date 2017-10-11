package org.solmix.generator.config;


public interface ConfigNodeParserProvider
{

    
    public static final String ROOT="/configuration";

   <T> XmlNodeParser<T> getXmlNodeParser(String path, Class<T> clz) throws XMLParserException;
}
