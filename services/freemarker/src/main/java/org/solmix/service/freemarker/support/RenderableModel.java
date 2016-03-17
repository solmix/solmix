package org.solmix.service.freemarker.support;

import org.solmx.service.template.Renderable;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;


public class RenderableModel extends StringModel
{
    public RenderableModel(Renderable value, BeansWrapper wrapper) {
        super(value, wrapper);
    }

    @Override
    public String getAsString() {
        return ((Renderable) object).render();
    }
}
