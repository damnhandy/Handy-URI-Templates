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
 * 
 * A generalized API for interacting with multiple variations of URI templates.
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
    * The URI template
    */
   protected String template;

   /**
    * The collection of values that will be applied to the URI template in the expansion process.
    */
   protected Map<String, Object> values = new HashMap<String, Object>();

   /**
    * 
    * 
    * @param template
    * @return
    */
   public static UriTemplate create(String template)
   {
      return new RFC6570UriTemplate(template);
   }

   /**
    * Returns the original URI template string.
    * 
    * @return
    */
   public String getTemplate()
   {
      return template;
   }

   /**
    * Helper method to set an expand a URI template.
    * 
    * @param template
    * @param values
    * @since 1.0
    * @return
    */
   public static String expand(String expression, Map<String, Object> variables)
   {
      UriTemplate template = create(expression);
      template.setVars(variables);
      return template.expand();
   }

   /**
    * Sets a value on the URI template.
    * 
    * @param var
    * @param value
    * @since 1.0
    * @return
    */
   public UriTemplate set(String var, Object value)
   {
      values.put(var, value);
      return this;
   }

   /**
    * Sets a Date value into the list of variable substitutions using the
    * default {@link DateFormat}.
    * 
    * @param var
    * @param value
    * @since 1.0
    * @return
    * 
    */
   public UriTemplate set(String var, Date value)
   {
      String date = defaultDateFormat.format(value);
      values.put(var, date);
      return this;
   }

   /**
    * Sets a Date value into the list of variable substitutions using a
    * user defined {@link DateFormat}.
    * 
    * @param var
    * @param value
    * @param format
    * @since 1.0
    * @return
    */
   public UriTemplate set(String var, Date value, DateFormat format)
   {
      values.put(var, format.format(value));
      return this;
   }

   /**
    * Uses the supplied {@link Map} of values
    * 
    * @param values
    * @since 1.0
    * @return
    */
   public UriTemplate setVars(Map<String, Object> values)
   {
      this.values = values;
      return this;
   }

   /**
    * Expand the URI template using the supplied values
    * 
    * @param vars The values that will be used in the expansion
    * @since 1.0
    * @return the expanded URI as a String
    */
   public abstract String expand(Map<String, Object> vars);

   /**
    * Applies variable substitution the URI Template and returns the expanded URI.
    * @since 1.0
    * @return
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