
package org.solmix.common.pmd;


import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * Look for new String(byte[]) or new String(byte[], start, end)
 * and complain.
 */
public class UnsafeStringConstructorRule extends AbstractJavaRule {

    /** {@inheritDoc} */
    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
            return data;
        }

        if (!TypeHelper.isA((ASTClassOrInterfaceType)node.jjtGetChild(0), String.class)) {
            return data;
        }
        
        ASTArgumentList arglist = node.getFirstChildOfType(ASTArgumentList.class);
        if (arglist == null) { // unlikely
            return data;
        }
        
        // one of the two possibilities ...
        if (arglist.jjtGetNumChildren() == 1 || arglist.jjtGetNumChildren() == 3) {
            ASTExpression firstArgExpr = arglist.getFirstChildOfType(ASTExpression.class);
            Class<?> exprType = firstArgExpr.getType();
            // pmd reports the type as byte, not byte[]. But since
            // there is no such thing as new String(byte), it seems
            // safe enough to take that as good enough.
            if (exprType != null) {
                if (exprType == Byte.TYPE || 
                    (exprType.isArray() && exprType.getComponentType() == Byte.TYPE)) {
                    addViolation(data, node);
                }
            }
        }
        return data;

    }

}
