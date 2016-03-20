
package org.solmix.service.velocity.support;

import org.apache.velocity.context.Context;

public class InterpolationUtil {
    public static final String INTERPOLATE_KEY = "_INTERPOLATION_";

    /**
     * 如果当前正在解析<code>StringLiteral</code>，则返回<code>true</code>。
     * <p>
     * 此特性需要打开velocity configuration：
     * <code>runtime.interpolate.string.literals.hack == true</code>。
     * </p>
     */
    public static boolean isInInterpolation(Context context) {
        return context.get(INTERPOLATE_KEY) instanceof Boolean;
    }
}
