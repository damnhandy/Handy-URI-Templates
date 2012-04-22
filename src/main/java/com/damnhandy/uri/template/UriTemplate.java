/*
 * 
 */
package com.damnhandy.uri.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

/**
 * 
 * A UriTemplate.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
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
   public static final String[] OPERATORS = {"+", "#", ".", "/", ";", "?", "&"};

   /**
    * 
    */
   protected String template;

   /**
    * 
    */
   protected Map<String, Object> variables = new HashMap<String, Object>();

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
    * 
    * 
    * @param template
    * @param variables
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
    * @return
    */
   public UriTemplate set(String var, Object value)
   {
      variables.put(var, value);
      return this;
   }

   /**
    * Sets a Date value into the list of variable substi
    * 
    * @param var
    * @param value
    * @return
    */
   public UriTemplate set(String var, Date value)
   {
      String date = defaultDateFormat.format(value);
      variables.put(var, date);
      return this;
   }

   /**
    * 
    * 
    * @param var
    * @param value
    * @param format
    * @return
    */
   public UriTemplate set(String var, Date value, DateFormat format)
   {
      variables.put(var, format.format(value));
      return this;
   }

   /**
    * 
    * 
    * @param variables
    * @return
    */
   public UriTemplate setVars(Map<String, Object> variables)
   {
      this.variables = variables;
      return this;
   }

   /**
    * Expand the URI template using the supplied variables
    * 
    * @param vars The variables that will be used in the expansion
    * @return the expanded URI as a String
    */
   public abstract String expand(Map<String, Object> vars);

   /**
    * Applies variable substitution the URI Template and returns the expanded URI.
    * 
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
      for (String operator : UriTemplate.OPERATORS)
      {
         if (operator.equals(op))
         {
            return true;
         }
      }
      return false;
   }

}