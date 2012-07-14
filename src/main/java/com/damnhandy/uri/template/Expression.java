/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.util.List;

import com.damnhandy.uri.template.impl.ExpressionImpl;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;
/**
 * <p>
 * An Expression represents the text between '{' and '}', including the enclosing
 * braces, as defined in <a href="ietf.org/html/rfc6570#section-2">Section 2 of RFC6570</a>.
 * </p>
 * <pre>
 * http://www.example.com/foo{?query,number}
                            \____________/
                                  ^
                                  |
                                  |
                            The expression
 * </pre>
 * <p>
 * This interface models this representation and adds helper functions for replacement and reverse matching.
 * </p>
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @see ExpressionImpl
 * @since 1.2
 */
public interface Expression
{

   /**
    * Get the replacementToken.
    * 
    * @return the replacementToken.
    */
   public abstract String getReplacementPattern();

   /**
    * Get the {@link Operator} value for this expression.
    * 
    * @return the operator value.
    */
   public abstract Operator getOperator();

   /**
    * Get the varSpecs.
    * 
    * @return the varSpecs.
    */
   public abstract List<VarSpec> getVarSpecs();

}