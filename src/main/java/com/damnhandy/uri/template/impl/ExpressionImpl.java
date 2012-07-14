/*
 *
 *
 */
package com.damnhandy.uri.template.impl;

import java.util.List;

import com.damnhandy.uri.template.Expression;

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
 * This class models this representation and adds helper functions for replacement and reverse matching.
 * </p>
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.2
 */
public final class ExpressionImpl implements Expression
{

   /**
    * A regex quoted string that matches the expression. For example:
    * <pre>
    *          \Q{?query,number}\E
    * </pre>
    */
   private String replacementPattern;
   
   /**
    * That {@link Operator} value that is associated with this Expression
    */
   private Operator op;

   /**
    * The the parsed {@link VarSpec} instances found in the expression. 
    */
   private List<VarSpec> varSpecs;
   

   /**
    * Create a new Expression.
    * 
    * @param replacementToken
    * @param op
    * @param varSpecs
    */
   public ExpressionImpl(final String replacementToken, final Operator op, final List<VarSpec> varSpecs)
   {
      this.replacementPattern = replacementToken;
      this.op = op;
      this.varSpecs = varSpecs;
   }

   /**
    * Get the replacementToken.
    * 
    * @return the replacementToken.
    */
   @Override
   public String getReplacementPattern()
   {
      return replacementPattern;
   }

   /**
    * Get the {@link Operator} value for this expression.
    * 
    * @return the operator value.
    */
   @Override
   public Operator getOperator()
   {
      return op;
   }

   /**
    * Get the varSpecs.
    * 
    * @return the varSpecs.
    */
   @Override
   public List<VarSpec> getVarSpecs()
   {
      return varSpecs;
   }

   /**
    * 
    */
   public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("Expression:");
      b.append("\n\tPattern: ").append(getReplacementPattern());
      b.append("\n\tOperator: ").append(this.getOperator().getOperator());
      b.append("\n\tVarSpecs: ");
      for (int i = 0; i < varSpecs.size(); i++)
      {
         if (i == 0)
         {
            b.append("{");
         }
         VarSpec v = varSpecs.get(i);
         b.append("\n\t\t{").append(v).append("},");
         if (i == (varSpecs.size() - 1))
         {
            b.append("\n\t}");
         }
      }
      return b.toString();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((op == null) ? 0 : op.hashCode());
      result = prime * result + ((varSpecs == null) ? 0 : varSpecs.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      ExpressionImpl other = (ExpressionImpl) obj;
      if (op != other.op)
      {
         return false;
      }
      if (varSpecs == null)
      {
         if (other.varSpecs != null)
         {
            return false;
         }
      }
      else if (!varSpecs.equals(other.varSpecs))
      {
         return false;
      }
      return true;
   }

}