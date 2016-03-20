
package org.solmix.service.velocity.support;

import java.lang.reflect.Field;

import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class SimpleNodeUtil {
    private static final Field childrenField;

    static {
        try {
            childrenField = SimpleNode.class.getDeclaredField("children");
            childrenField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Could not reflect on SimpleNode", e);
        }
    }

    public static void jjtSetChild(SimpleNode parent, Node child, int index) {
        Node[] children;

        try {
            children = (Node[]) childrenField.get(parent);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not get children for node", e);
        }

        children[index] = child;
    }
}
