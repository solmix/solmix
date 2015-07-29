
package org.solmx.service.velocity.support;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.solmx.service.template.Renderable;

/**
 * 渲染<code>Renderable</code>的event handler。
 *
 * @author Michael Zhou
 */
public class RenderableHandler implements ReferenceInsertionEventHandler {
    public Object referenceInsert(String reference, Object value) {
        if (value instanceof Renderable) {
            return ((Renderable) value).render();
        }

        return value;
    }
}
