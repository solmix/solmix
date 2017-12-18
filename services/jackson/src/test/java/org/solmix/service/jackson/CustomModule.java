package org.solmix.service.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;


public class CustomModule extends SimpleModule
{

    @Override
    public void setupModule(SetupContext context)
    {
       
        context.insertAnnotationIntrospector(new CustomAnnotationIntrospector());
        super.setupModule(context);
    }
}
