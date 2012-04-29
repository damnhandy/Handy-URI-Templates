/*
 * 
 */
package com.damnhandy.uri.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

/**
 * <p>
 * 
 * </p>
 *
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public abstract class UriTemplate
{
   /**
    * 
    */
   private DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

   /**
    * 
    */
   public static final char[] OPERATORS = {'+', '#', '.', '/', ';', '?', '&'};

   /**
    * 
    */
   private static final BitSet OPERATOR_BITSET = new BitSet();

   static
   {
      for (int i = 0; i < OPERATORS.length; i++)
      {
         OPERATOR_BITSET.set(OPERATORS[i]);
      }
   }
   
   /**
    * The URI expression
    */
   protected String expression;

   /**
    * The collection of values that will be applied to the URI expression in the
    * expansion process.
    */
   protected Map<String, Object> values = new HashMap<String, Object>();

   
   /**
    * Creates a new {@link UriTemplate} from the expression.
    * 
    * @param expression
    * @return
    * @since 1.0
    */
   public static final UriTemplate expression(String expression)
   {
      return new RFC6570UriTemplate(expression);
   }
   
   /**
    * <p>
    * Creates a {@link UriTemplate} from a base URI template expression, such as:
    * </p>
    * <pre>
    * http://api.github.com
    * </pre>
    * <p>
    * And appending a child path such as:
    * </p>
    * <pre>
    * /repos/{user}/{repo}/commits
    * </pre>
    * <p>The resulting expression would result in:</p>
    * <pre>
    * http://api.github.com/repos/{user}/{repo}/commits
    * </pre>
    * @param baseExpression
    * @param expression
    * @return
    * @since 1.0
    */
   public static UriTemplate expressionWithBase(String baseExpression, String expression)
   {
      StringBuilder b = new StringBuilder(baseExpression);
      b.append(expression);
      return expression(b.toString());
   }
   
   /**
    * Creates a new {@link UriTemplate} from a root {@link UriTemplate}. This
    * method work similar to {@link UriTemplateFactory#expressionWithBase(String, String)} with
    * the difference being that the variables from the base template will be
    * copied to the new {@link UriTemplate}. 
    * 
    * This method is useful when the base expression is less volatile than the child
    * expression. 
    * 
    * @param base
    * @param expression
    * @return
    * @since 1.0
    */
   public static UriTemplate expressionWithBase(UriTemplate base, String expression)
   {
      UriTemplate template = expressionWithBase(base.getExpression(), expression);
      template.set(base.getValues());
      return template;
   }

   /**
    * Expands the given expression string using the variable replacements
    * in the supplied {@link Map}.
    * 
    * @param expression
    * @param values
    * @since 1.0
    * @return
    */
   public static String expand(String expression, Map<String, Object> values) {
       UriTemplate template = expression(expression);
       template.set(values);
       return template.expand();
   }
   
   /**
    * Returns the original URI template expression.
    * 
    * @return
    * @since 1.0
    */
   public String getExpression()
   {
      return expression;
   }

   /**
    * Returns the collection of name/value pairs contained in the instance.
    * 
    * @return
    * @since 1.0
    */
   public Map<String, Object> getValues()
   {
      return this.values;
   }

   /**
    * 
    * @param dateFormatString
    * @return
    * @since 1.0
    */
   public UriTemplate setDefaultDateFormat(String dateFormatString)
   {
      return this.setDefaultDateFormat(new SimpleDateFormat(dateFormatString));
   }

   /**
    * 
    * @param dateFormat
    * @return
    * @since 1.0
    */
   public UriTemplate setDefaultDateFormat(DateFormat dateFormat)
   {
      defaultDateFormat = dateFormat;
      return this;
   }

   /**
    * Sets a value on the URI expression.
    * 
    * @param variableName
    * @param value
    * @return
    * @since 1.0
    */
   public UriTemplate set(String variableName, Object value)
   {
      values.put(variableName, value);
      return this;
   }

   /**
    * Sets a Date value into the list of variable substitutions using the
    * default {@link DateFormat}.
    * 
    * @param variableName
    * @param value
    * @return
    * @since 1.0
    * 
    */
   public UriTemplate set(String variableName, Date value)
   {
      String date = defaultDateFormat.format(value);
      values.put(variableName, date);
      return this;
   }

   /**
    * Sets a Date value into the list of variable substitutions using a user
    * defined {@link DateFormat}.
    * 
    * @param variableName
    * @param value
    * @param format
    * @return
    * @since 1.0
    */
   public UriTemplate set(String variableName, Date value, DateFormat format)
   {
      values.put(variableName, format.format(value));
      return this;
   }

   /**
    * Adds the name/value pairs in the supplied {@link Map} to the collection
    * of values within this expression instance.
    * 
    * @param values
    * @return
    * @since 1.0
    */
   public UriTemplate set(Map<String, Object> values)
   {
      this.values.putAll(values);
      return this;
   }

   /**
    * Expand the URI expression using the supplied values
    * 
    * @param vars
    *            The values that will be used in the expansion
    * 
    * @return the expanded URI as a String
    * @since 1.0
    */
   public abstract String expand(Map<String, Object> vars);

   /**
    * Applies variable substitution the URI Template and returns the expanded
    * URI.
    * 
    * @return
    * @since 1.0
    */
   public abstract String expand();

   /**
    * 
    * 
    * @param op
    * @return
    */
   protected boolean containsOperator(String op)
   {
      return OPERATOR_BITSET.get(op.toCharArray()[0]);
   }

}