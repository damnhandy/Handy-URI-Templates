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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

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
public abstract class UriTemplate implements java.io.Serializable
{


   /** The serialVersionUID */
   private static final long serialVersionUID = -1964234955599113321L;

   public static enum Encoding {
      U, UR;
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

   protected String template;

   /**
    * The collection of values that will be applied to the URI expression in the
    * expansion process.
    */
   protected Map<String, Object> values = new HashMap<String, Object>();

   /**
    *
    */
   protected Expression[] expressions;


   /**
    * Creates a new {@link UriTemplate} from the template.
    *
    * @param templateString
    * @return
    * @since 1.2
    */
   public static final UriTemplate fromTemplate(final String templateString) throws MalformedUriTemplateException
   {
      return new RFC6570UriTemplate(templateString);
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
   public static UriTemplateBuilder fromTemplate(UriTemplate base)
   {
      return new UriTemplateBuilder(base.getTemplate());
   }

   /**
    * Initializes the internal URI template model.
    *
    *
    */
   protected abstract void initExpressions() throws MalformedUriTemplateException;


   /**
    * Returns the number of expressions found in this template
    *
    * @return
    */
   public int expressionCount()
   {
      return expressions.length;
   }

   /**
    * FIXME Comment this
    *
    * @return
    */
   public Expression[] getExpressions()
   {
      return expressions;
   }

   /**
    * Expands the given template string using the variable replacements
    * in the supplied {@link Map}.
    *
    * @param templateString
    * @param values
    * @since 1.0
    * @throws MalformedUriTemplateException
    * @throws VariableExpansionException
    * @return
    */
   public static String expand(final String templateString, Map<String, Object> values) throws MalformedUriTemplateException, VariableExpansionException{
       UriTemplate template = new RFC6570UriTemplate(templateString);
       template.set(values);
       return template.expand();
   }


   /**
    * Returns the original URI template expression.
    *
    * @return
    * @since 1.1.4
    */
   public String getTemplate()
   {
      return template;
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
      if(values != null)
      {
         if(!values.isEmpty())
         {
            this.values.putAll(values);
         }
      }
      return this;
   }

   /**
    * Expand the URI template using the supplied values
    *
    * @param vars
    *            The values that will be used in the expansion
    *
    * @return the expanded URI as a String
    * @throw VariableExpansionException
    * @since 1.0
    */
   public abstract String expand(Map<String, Object> vars) throws VariableExpansionException;

   /**
    * Applies variable substitution the URI Template and returns the expanded
    * URI.
    *
    * @return
    * @throw VariableExpansionException
    * @since 1.0
    */
   public abstract String expand() throws VariableExpansionException;

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

   public String getRegexString()
   {
      return null;
   }


}