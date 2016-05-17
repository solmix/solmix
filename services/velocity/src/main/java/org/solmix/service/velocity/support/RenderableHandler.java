
package org.solmix.service.velocity.support;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.solmix.service.template.Renderable;

/**
 * 渲染<code>Renderable</code>的event handler。
 *
 */
public class RenderableHandler implements ReferenceInsertionEventHandler {
    @Override
    public Object referenceInsert(String reference, Object value) {
        if (value instanceof Renderable) {
            return ((Renderable) value).render();
        }

        return value;
    }
}
