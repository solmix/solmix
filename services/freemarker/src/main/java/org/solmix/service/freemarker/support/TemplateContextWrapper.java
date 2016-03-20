
package org.solmix.service.freemarker.support;

import org.solmix.service.template.Renderable;
import org.solmix.service.template.TemplateContext;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateContextWrapper extends BeansWrapper
{

    private final ObjectWrapper userDefinedWrapper;

    public TemplateContextWrapper(ObjectWrapper userDefinedWrapper)
    {
        super(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        this.userDefinedWrapper = userDefinedWrapper;
    }

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj instanceof TemplateContext) {
            return new TemplateContextAdapter((TemplateContext) obj, this);
        }

        if (obj instanceof Renderable) {
            return new RenderableModel((Renderable) obj, this);
        }

        if (userDefinedWrapper != null) {
            return userDefinedWrapper.wrap(obj);
        } else {
            return super.wrap(obj);
        }
    }

}
