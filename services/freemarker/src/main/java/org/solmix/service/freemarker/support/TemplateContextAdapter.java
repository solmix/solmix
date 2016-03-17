package org.solmix.service.freemarker.support;

import org.solmx.service.template.TemplateContext;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;


public class TemplateContextAdapter extends SimpleHash
{

    /**
     * 
     */
    private static final long serialVersionUID = 9096079724119521242L;

    public TemplateContextAdapter(TemplateContext context, ObjectWrapper wrapper) {
        super(wrapper);

        for (String key : context.keySet()) {
            super.put(key, context.get(key));
        }
    }
}
