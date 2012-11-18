/*
 * 
 */
package com.damnhandy.uri.template;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This is the main class for creating and manipulating URI templates. This project implements
 * <a href="http://tools.ietf.org/html/rfc6570">RFC6570 URI Templates</a> and produces output 
 * that is compliant with the spec. The template processor supports <a href="http://tools.ietf.org/html/rfc6570#section-1.2">levels
 * 1 through 4</a> as well as supports composite types. In addition to supporting {@link Map} 
 * and {@link List} values as composite types, the library also supports the use of Java objects
 * as well. Please see the {@link VarExploder} and {@link DefaultVarExploder} for more info. 
 * </p>
 * <h3>Basic Usage:</h3>
 * <p>
 * There are many ways to use this library. The simplest way is to create a template from a
 * URI template  string:
 * </p>
 * <pre>
 * UriTemplate template = UriTemplate.fromTemplate("http://example.com/search{?q,lang}");
 * </pre>
 * <p>
 * Replacement values are added by calling the {@link #set(String, Object)} method on the template:
 * </p>
 * <pre>
 * template.set("q","cat")
 *         .set("lang","en");                               
 * String uri = template.expand();
 * </pre>
 * <p>The {@link #expand()} method will replace the variable names with the supplied values
 * and return the following URI:</p>
 * <pre>
 * http://example.com/search?q=cat&lang=en
 * </pre>
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public abstract class UriTemplate
{
   public static enum Encoding {
      U, UR,UF;
   }
   
   public static final String DEFAULT_SEPARATOR = ",";
   /**
    * 
    */
   protected DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

   /**
    * 
    */
   static final char[] OPERATORS = {'+', '#', '.', '/', ';', '?', '&'};

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
   private StringBuilder templateBuffer;

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
    * @deprecated use {@link #fromTemplate(String)} instead
    */
   @Deprecated
   public static final UriTemplate fromExpression(String expression)
   {
      return new RFC6570UriTemplate(expression);
   }
   
   /**
    * Creates a new {@link UriTemplate} from the template.
    * 
    * @param expression
    * @return
    * @since 1.2
    */
   public static final UriTemplate fromTemplate(String template)
   {
      return new RFC6570UriTemplate(template);
   }
   
   /**
    * <p>
    * Creates a new {@link UriTemplate} from a root {@link UriTemplate}. This
    * method will create a new {@link UriTemplate} from the base and copy the variables  
    * from the base template to the new {@link UriTemplate}. 
    * </p>
    * <p>
    * This method is useful when the base template is less volatile than the child
    * expression and you want to merge the two.
    * </p>
    * @param base
    * @return
    * @since 1.0
    */
   public static UriTemplate fromTemplate(UriTemplate base)
   {
      UriTemplate template = fromTemplate(base.getTemplate());
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
       UriTemplate template = fromTemplate(expression);
       template.set(values);
       return template.expand();
   }
   
   /**
    * Returns the original URI template expression.
    * 
    * @return
    * @since 1.0
    * @deprecated use {@link #getTemplate()} instead.
    */
   @Deprecated
   public String getExpression()
   {
      return getTemplate();
   }
   
   /**
    * Returns the original URI template expression.
    * 
    * @return
    * @since 1.1.4
    */
   public String getTemplate() 
   {
      return templateBuffer.toString();
   }
   
   /**
    * <p>
    * Appends the expression from a base URI template expression, such as:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com");
    * </pre>
    * 
    * <p>
    * A child expression can be appended by:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com")
    *                                   .expression("/repos/{user}/{repo}/commits");
    * 
    * </pre>
    * <p>The resulting expression would result in:</p>
    * <pre>
    * http://api.github.com/repos/{user}/{repo}/commits
    * </pre>
    * <p>
    * Multiple expressions can be appended to the template as follows:
    * </p>
    * <pre>
    *  UriTemplate template = UriTemplate.fromTemplate("http://myhost")
    *                                    .append("{/version}")
    *                                    .append("{/myId}")
    *                                    .append("/things/{thingId}")
    *                                    .set("myId","damnhandy")
    *                                    .set("version","v1")
    *                                    .set("thingId","12345");
    * </pre>
    * <p>This will result in the following template and URI:</p>
    * <pre>
    * Template: http://myhost{/version}{/myId}/things/{thingId}
    * URI:      http://myhost/v1/damnhandy/things/12345
    * </pre>
    * <p>
    * Since a URI template is not a URI, there no way to accurately determine 
    * how the variable expression should be delimited. Therefore, it is up to 
    * the expression author to include the necessary delimiters in each sub 
    * expression.
    * </p>
    * @param template
    * @return
    * @since 1.1.4
    * 
    */
   public UriTemplate append(String template)
   {
      if(template == null)
      {
         return this;
      }
      this.templateBuffer.append(template.trim());
      return this;
   }

   /**
    * FIXME Comment this
    * 
    * @param expression
    * @return
    * @deprecated use {@link #template(String)} instead
    */
   @Deprecated
   public UriTemplate expression(String expression)
   {
      return this.append(expression);
   }
   
   /**
    * FIXME Comment this
    * 
    * @param template
    */
   protected void setTemplate(String template)
   {
      this.templateBuffer = new StringBuilder(template);
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
   public UriTemplate withDefaultDateFormat(String dateFormatString)
   {
      return this.withDefaultDateFormat(new SimpleDateFormat(dateFormatString));
   }

   /**
    * 
    * @param dateFormat
    * @return
    * @since 1.0
    */
   public UriTemplate withDefaultDateFormat(DateFormat dateFormat)
   {
      defaultDateFormat = dateFormat;
      return this;
   }

   /**
    * Sets a value on the URI template expression variable.
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
      values.put(variableName, value);
      return this;
   }


   /**
    * Adds the name/value pairs in the supplied {@link Map} to the collection
    * of values within this URI template instance.
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
    * Expand the URI template using the supplied values
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
   public static boolean containsOperator(String op)
   {
      return OPERATOR_BITSET.get(op.toCharArray()[0]);
   }

}