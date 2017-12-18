package org.solmix.service.jackson;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;


public class CustomAnnotationIntrospector extends NopAnnotationIntrospector
{

    private static final long serialVersionUID = 1L;
    
    private CustomJsonSerializer custom = new CustomJsonSerializer();
    @Override
    public Object findSerializer(Annotated am) {
        Custom ann = _findAnnotation(am, Custom.class);
        if(ann==null){
            return null;
        }
        String type = ann.value();
        
        return custom;
    }

}
