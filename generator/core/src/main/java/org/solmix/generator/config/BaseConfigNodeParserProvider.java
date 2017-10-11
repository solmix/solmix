package org.solmix.generator.config;

import java.util.HashMap;
import java.util.Map;

import org.solmix.commons.util.Reflection;
import org.solmix.runtime.Container;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;


public class BaseConfigNodeParserProvider implements ConfigNodeParserProvider
{
   private boolean init;
    
    private Map<String,Class<?>> registryClasses= new HashMap<String, Class<?>>();
    
    private Map<String,XmlNodeParser<?>> registry=new HashMap<String, XmlNodeParser<?>>();
    
    Container container;
    public BaseConfigNodeParserProvider(Container container){
        this.container=container;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> XmlNodeParser<T> getXmlNodeParser(String path, Class<T> clz) throws XMLParserException {
        ensureInit();
        XmlNodeParser<?> parser=registry.get(path);
        if(parser!=null){
            return XmlNodeParser.class.cast(parser);
        }
        Class<?> parserClass=registryClasses.get(path);
        if(parserClass!=null){
            try {
               Object instance= Reflection.newInstance(parserClass);
               if(instance instanceof XmlNodeParser){
                   XmlNodeParser<?> p=(XmlNodeParser<?>)instance;
                   configParser(p);
                   registry.put(path, p);
                   return XmlNodeParser.class.cast(p);
               }
            } catch (Exception e) {
                throw new XMLParserException("Can't instance xmlNodeParser for path:"+path, e);
            }
        }
        return null;
    }
    
    /**
     * @param p
     */
    private void configParser(XmlNodeParser<?> p) {
       if(this.container!=null){
           ResourceManager resourceManager = container.getExtension(ResourceManager.class);
           if (resourceManager != null) {
               ResourceInjector injector = new ResourceInjector(resourceManager);
               injector.injectAware(p);
               injector.inject(p);
               injector.construct(p);
           }
       }
        
    }

    protected synchronized void ensureInit(){
        if(!init){
            config();
            init=true;
        }
    }
    
    /**
     * 子类可以通过该方法实现{@link XmlNodeParser}的配置
     */
    protected  void config(){
        
    }
    
    protected <T> void bind(String path,Class<? extends XmlNodeParser<T>> parser){
        registryClasses.put(path, parser);
    }
}
