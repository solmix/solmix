package org.solmix.generator.config;



public class ConfigParserContext
{
    private ConfigNodeParserProvider provider;
    private  ConfigurationParser parser;

    public ConfigParserContext(ConfigNodeParserProvider provider,ConfigurationParser parser)
    {
        this.provider=provider;
        this.parser=parser;
    }

    public String resolveProperty(String key){
        return parser.resolveProperty(key);
    }

}
