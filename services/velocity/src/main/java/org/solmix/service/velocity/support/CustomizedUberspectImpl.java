
package org.solmix.service.velocity.support;

import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.runtime.parser.node.BooleanPropertyExecutor;
import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.apache.velocity.runtime.parser.node.MapGetExecutor;
import org.apache.velocity.runtime.parser.node.PropertyExecutor;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.VelPropertyGet;

/**
 * 修改velocity默认的uberspect，改变默认的get property方法的顺序：
 * <ul>
 * <li><code>getFoo()</code>或<code>getfoo()</code>。</li>
 * <li><code>isFoo()</code>或<code>isfoo()</code>。</li>
 * <li><code>Map.get(String)</code>。</li>
 * <li><code>AnyType.get(String)</code>。</li>
 * </ul>
 *
 */
public class CustomizedUberspectImpl extends org.apache.velocity.util.introspection.UberspectImpl {
    @Override
    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        if (obj == null) {
            return null;
        }

        Class<?> claz = obj.getClass();

        /*
         * first try for a getFoo() type of property (also getfoo() )
         */
        AbstractExecutor executor = new PropertyExecutor(log, introspector, claz, identifier);

        /*
         * if that didn't work, look for boolean isFoo()
         */
        if (!executor.isAlive()) {
            executor = new BooleanPropertyExecutor(log, introspector, claz, identifier);
        }

        /*
         * Let's see if we are a map...
         */
        if (!executor.isAlive()) {
            executor = new MapGetExecutor(log, claz, identifier);
        }

        /*
         * finally, look for get("foo")
         */
        if (!executor.isAlive()) {
            executor = new GetExecutor(log, introspector, claz, identifier);
        }

        return executor.isAlive() ? new VelGetterImpl(executor) : null;
    }
}
