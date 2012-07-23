/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.util.LinkedList;
import java.util.List;

import com.damnhandy.uri.template.impl.ExpressionImpl;
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
 * @see ExpressionImpl
 * @since 1.2
 */
public abstract class Expression
{
   /**
    * Creates a new {@link ExpressionBuilder} to create a simple expression according 
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
   public static ExpressionBuilder simple()
   {
      return ExpressionBuilder.create(Operator.NUL);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression that will use reserved expansion
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
   public static ExpressionBuilder reserved()
   {
      return ExpressionBuilder.create(Operator.RESERVED);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression with a fragment operator
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
   public static ExpressionBuilder fragment()
   {
      return ExpressionBuilder.create(Operator.FRAGMENT);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression using label expansion
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
   public static ExpressionBuilder label()
   {
      return ExpressionBuilder.create(Operator.NAME_LABEL);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression using path expansion
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
   public static ExpressionBuilder path()
   {
      return ExpressionBuilder.create(Operator.PATH);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression using path-style parameter 
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
   public static ExpressionBuilder matrix()
   {
      return ExpressionBuilder.create(Operator.MATRIX);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression using form-style query string expansion
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
   public static ExpressionBuilder query()
   {
      return ExpressionBuilder.create(Operator.QUERY);
   }

   /**
    * Creates a new {@link ExpressionBuilder} to create an expression using form-style query continuation expansion
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
   public static ExpressionBuilder continuation()
   {
      return ExpressionBuilder.create(Operator.CONTINUATION);
   }

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
   
   public static class ExpressionBuilder
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
      private ExpressionBuilder(Operator operator)
      {
         this.operator = operator;
      }

      static ExpressionBuilder create(Operator operator)
      {
         return new ExpressionBuilder(operator);
      }

      /**
       * FIXME Comment this
       * 
       * @param varName
       * @return
       */
      public ExpressionBuilder var(String varName)
      {
         return var(varName, Modifier.NONE, null);
      }

      /**
       * FIXME Comment this
       * 
       * @param varName
       * @param explode
       * @return
       */
      public ExpressionBuilder var(String varName, boolean explode)
      {
         if (explode)
         {
            return var(varName, Modifier.EXPLODE, null);
         }
         return var(varName, Modifier.NONE, null);
      }

      /**
       * FIXME Comment this
       * 
       * @param varName
       * @param prefix
       * @return
       */
      public ExpressionBuilder var(String varName, int prefix)
      {
         return var(varName, Modifier.PREFIX, prefix);
      }

      /**
       * FIXME Comment this
       * 
       * @param varName
       * @param modifier
       * @param position
       * @return
       */
      private ExpressionBuilder var(String varName, Modifier modifier, Integer position)
      {
         varSpecs.add(new VarSpec(varName, modifier, position));
         return this;
      }

      public Expression build() 
      {
         return new ExpressionImpl(null, operator, varSpecs);
      }
   }

}