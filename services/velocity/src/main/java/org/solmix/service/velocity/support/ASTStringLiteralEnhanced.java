
package org.solmix.service.velocity.support;


import static org.solmix.service.velocity.support.InterpolationUtil.INTERPOLATE_KEY;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;

public class ASTStringLiteralEnhanced extends ASTStringLiteral {
    private static final Field[] fields;
    private              boolean interpolate;

    static {
        List<Field> fieldList = new LinkedList<Field>();

        for (Class<?> c = ASTStringLiteral.class; c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                fieldList.add(field);
            }
        }

        fields = fieldList.toArray(new Field[fieldList.size()]);
    }

    public ASTStringLiteralEnhanced(ASTStringLiteral src) {
        super(-1);

        for (Field field : fields) {
            try {
                Object value = field.get(src);

                if ("interpolate".equals(field.getName()) && value instanceof Boolean) {
                    interpolate = (Boolean) value;
                }

                field.set(this, value);
            } catch (Exception e) {
                throw new RuntimeException("Could not copy ASTStringLiteral", e);
            }
        }
    }

    @Override
    public Object value(InternalContextAdapter context) {
        if (interpolate) {
            Object savedInterpolate = context.localPut(INTERPOLATE_KEY, Boolean.TRUE);

            try {
                return super.value(context);
            } finally {
                if (savedInterpolate == null) {
                    context.remove(INTERPOLATE_KEY);
                } else {
                    context.localPut(INTERPOLATE_KEY, savedInterpolate);
                }
            }
        }

        return super.value(context);
    }
}
