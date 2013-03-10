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
package com.damnhandy.uri.template;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.damnhandy.uri.template.impl.Modifier;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;

/**
 * <p>
 * An Expression represents the text between '{' and '}', including the enclosing
 * braces, as defined in <a href="ietf.org/html/rfc6570#section-2">Section 2 of RFC6570</a>.
 * </p>
 * <pre>
 * http://www.example.com/foo{?query,number}
                              \___________/
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
 * @see ExpressionImpl
 * @since 1.2
 */
public class Expression
{

   /**
    * Regex to validate the variable name.
    */
   private static final Pattern VARNAME_REGEX = Pattern.compile("([\\w\\_\\.]|%[A-Fa-f0-9]{2})+");
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
    * Creates a new {@link Builder} to create a simple expression according
    * to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.2">3.2.2</a>.
    * Calling:
    * <pre>
    * String exp = Expression.simple().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {var}
    * </pre>
    *
    * @return
    */
   public static Builder simple()
   {
      return Builder.create(Operator.NUL);
   }

   /**
    * Creates a new {@link Builder} to create an expression that will use reserved expansion
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.3">3.2.3</a>.
    * Calling:
    * <pre>
    * String exp = Expression.reserved().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {+var}
    * </pre>
    *
    * @return
    */
   public static Builder reserved()
   {
      return Builder.create(Operator.RESERVED);
   }

   /**
    * Creates a new {@link Builder} to create an expression with a fragment operator
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.4">3.2.4</a>.
    * Calling:
    * <pre>
    * String exp = Expression.fragment().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {#var}
    * </pre>
    *
    * @return
    */
   public static Builder fragment()
   {
      return Builder.create(Operator.FRAGMENT);
   }

   /**
    * Creates a new {@link Builder} to create an expression using label expansion
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.5">3.2.5</a>.
    * Calling:
    * <pre>
    * String exp = Expression.label().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {.var}
    * </pre>
    *
    * @return
    */
   public static Builder label()
   {
      return Builder.create(Operator.NAME_LABEL);
   }

   /**
    * Creates a new {@link Builder} to create an expression using path expansion
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.6">3.2.6</a>.
    * Calling:
    * <pre>
    * String exp = Expression.path().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {/var}
    * </pre>
    *
    * @return
    */
   public static Builder path()
   {
      return Builder.create(Operator.PATH);
   }

   /**
    * Creates a new {@link Builder} to create an expression using path-style parameter
    * (a.k.a. matrix parameter) expansion according to
    * section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.7">3.2.7</a>.
    * Calling:
    * <pre>
    * String exp = Expression.matrix().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {;var}
    * </pre>
    *
    * @return
    */
   public static Builder matrix()
   {
      return Builder.create(Operator.MATRIX);
   }

   /**
    * Creates a new {@link Builder} to create an expression using form-style query string expansion
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.8">3.2.8</a>.
    * Calling:
    * <pre>
    * String exp = Expression.query().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {?var}
    * </pre>
    *
    * @return
    */
   public static Builder query()
   {
      return Builder.create(Operator.QUERY);
   }

   /**
    * Creates a new {@link Builder} to create an expression using form-style query continuation expansion
    * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.9">3.2.9</a>.
    * Calling:
    * <pre>
    * String exp = Expression.continuation().var("var").build().toString();
    * </pre>
    * <p>
    * Will return the following expression:
    * </p>
    * <pre>
    * {&var}
    * </pre>
    *
    * @return
    */
   public static Builder continuation()
   {
      return Builder.create(Operator.CONTINUATION);
   }


   /**
    * Create a new Expression.
    *
    * @param replacementToken
    * @param op
    * @param varSpecs
    */
   public Expression(final String rawExpression) throws MalformedUriTemplateException
   {
      this.parseRawExpression(rawExpression);
   }

   /**
    *
    * Create a new Expression
    *
    * @param op
    * @param varSpecs
    * @throws MalformedUriTemplateException
    */
   public Expression(final Operator op, final List<VarSpec> varSpecs) throws MalformedUriTemplateException
   {
      this.op = op;
      this.varSpecs = varSpecs;
   }

   /**
    * FIXME Comment this
    *
    * @param rawExpression
    * @throws MalformedUriTemplateException
    */
   private void parseRawExpression(String rawExpression) throws MalformedUriTemplateException
   {
      final String expressionReplacement = Pattern.quote(rawExpression);
      String token = rawExpression.substring(1, rawExpression.length() - 1);
      // Check for URI operators
      Operator operator = Operator.NUL;
      String firstChar = token.substring(0, 1);
      if (UriTemplate.containsOperator(firstChar))
      {
         operator = Operator.fromOpCode(firstChar);
         token = token.substring(1, token.length());
      }
      String[] varspecStrings = token.split(",");
      List<VarSpec> varspecs = new ArrayList<VarSpec>();

      for (String varname : varspecStrings)
      {
         int subStrPos = varname.indexOf(Modifier.PREFIX.getValue());
         /*
          * Prefix variable
          */
         if (subStrPos > 0)
         {
            String[] pair = varname.split(Modifier.PREFIX.getValue());
            try
            {
               validateVarname(pair[0]);
               Integer pos = Integer.valueOf(varname.substring(subStrPos + 1));
               varspecs.add(new VarSpec(pair[0], Modifier.PREFIX, pos));
            }
            catch (NumberFormatException e)
            {
               throw new MalformedUriTemplateException("The prefix value for "+ pair[0]+ " was not a number", e);
            }
         }

         /*
          * Variable will be exploded
          */
         else if (varname.lastIndexOf(Modifier.EXPLODE.getValue()) > 0)
         {
            validateVarname(varname.substring(0, varname.length() - 1));
            varspecs.add(new VarSpec(varname, Modifier.EXPLODE));
         }
         /*
          * Standard Value
          */
         else
         {
            validateVarname(varname);
            varspecs.add(new VarSpec(varname, Modifier.NONE));
         }
      }
      this.replacementPattern = expressionReplacement;
      this.op = operator;
      this.varSpecs = varspecs;
   }

   /**
    * Validates that the varname conforms to the spec.
    *
    * @param varname
    */
   private void validateVarname(String varname) throws MalformedUriTemplateException {
      Matcher matcher = VARNAME_REGEX.matcher(varname);
      if(!matcher.matches())
      {
         throw new MalformedUriTemplateException("The variable name "+varname+" contains invalid characters");
      }

      if(varname.contains(" "))
      {
         throw new MalformedUriTemplateException("The variable name "+varname+" cannot contain spaces (leading or trailing)");
      }
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
   public String getReplacementPattern()
   {
      return replacementPattern;
   }

   /**
    * Get the {@link Operator} value for this expression.
    *
    * @return the operator value.
    */
   public Operator getOperator()
   {
      return op;
   }

   /**
    * Get the varSpecs.
    *
    * @return the varSpecs.
    */
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
      Expression other = (Expression) obj;
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


   /**
    *
    * A ExpressionBuilder.
    *
    * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
    * @version $Revision: 1.1 $
    */
   public static class Builder
   {
      /**
       *
       */
      private Operator operator;

      /**
       *
       */
      private List<VarSpec> varSpecs = new LinkedList<VarSpec>();

      /**
       *
       * Create a new ExpressionBuilder.
       *
       * @param operator
       */
      private Builder(Operator operator)
      {
         this.operator = operator;
      }

      /**
       *
       *
       * @param operator
       * @return
       */
      static Builder create(Operator operator)
      {
         return new Builder(operator);
      }

      /**
       * Adds a variable name to the expression.
       *
       * <pre>
       * builder.var("foo");
       * </pre>
       *
       * Will yield the following expression:
       * <pre>
       * {foo}
       * </pre>
       *
       * @param varName
       * @return
       */
      public Builder var(String varName)
      {
         return var(varName, Modifier.NONE, null);
      }

      /**
       * Adds a variable name to the expression with an explode modifier.
       *
       * <pre>
       * builder.var("foo",true);
       * </pre>
       *
       * Will yield the following expression:
       * <pre>
       * {foo*}
       * </pre>
       *
       * @param varName
       * @param explode
       * @return
       */
      public Builder var(String varName, boolean explode)
      {
         if (explode)
         {
            return var(varName, Modifier.EXPLODE, null);
         }
         return var(varName, Modifier.NONE, null);
      }

      /**
       * Adds a variable name to the expression with a prefix modifier.
       *
       * <pre>
       * builder.var("foo",2);
       * </pre>
       *
       * Will yield the following expression:
       * <pre>
       * {foo:2}
       * </pre>
       * @param varName
       * @param prefix
       * @return
       */
      public Builder var(String varName, int prefix)
      {
         return var(varName, Modifier.PREFIX, prefix);
      }

      /**
       *
       *
       * @param varName
       * @param modifier
       * @param position
       * @return
       */
      private Builder var(String varName, Modifier modifier, Integer position)
      {
         varSpecs.add(new VarSpec(varName, modifier, position));
         return this;
      }

      public Expression build() throws MalformedUriTemplateException
      {
         return new Expression(operator, varSpecs);
      }
   }

}