/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template.impl;

import java.util.List;

import com.damnhandy.uri.template.Expression;

/**
 *
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class ExpressionImpl extends Expression
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
    * Returns a string that contains a regular expression that matches the
    * URI template expression.
    *
    * @return
    */
   public String buildMatchingPattern()
   {
      StringBuilder builder = new StringBuilder();
      return builder.toString();
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
    * Returns the string representation of the expression.
    *
    * @see Object#toString()
    */
   public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("{").append(this.getOperator().getOperator());
      for (int i = 0; i < varSpecs.size(); i++)
      {
         VarSpec v = varSpecs.get(i);
         b.append(v.getValue());
         b.append(v.getModifier().getValue());
         if (v.getModifier() == Modifier.PREFIX)
         {
            b.append(v.getPosition());
         }
         if (i != (varSpecs.size() - 1))
         {
            b.append(",");
         }
      }
      return b.append("}").toString();
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