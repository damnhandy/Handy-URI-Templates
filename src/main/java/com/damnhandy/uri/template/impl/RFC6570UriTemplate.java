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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.damnhandy.uri.template.Expression;
import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.UriUtil;
import com.damnhandy.uri.template.VarExploder;
import com.damnhandy.uri.template.VariableExpansionException;

/**
 * A {@link UriTemplate} implementation that supports <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class RFC6570UriTemplate extends UriTemplate
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 1948906277756641217L;

   /**
    * Create a new RFC6570UriTemplate.
    *
    * @param template
    */
   public RFC6570UriTemplate(final String template) throws MalformedUriTemplateException
   {
      this.template = template;
      this.parseTemplateString();
   }

   /**
    * Expand the URI expression using the supplied values.
    *
    * @param vars The values that will be used in the expansion
    * @return the expanded URI as a String
    * @throws VariableExpansionException
    */
   @Override
   public String expand(Map<String, Object> vars) throws VariableExpansionException
   {
      this.values = vars;
      return expand();
   }


   /**
    * FIXME Comment this
    *
    */
   protected void parseTemplateString() throws MalformedUriTemplateException
   {
      final String templateString = getTemplate();
      final ExpressionScanner scanner = new ExpressionScanner();
      final List<String> rawExpressions = scanner.scan(templateString);
      final List<Expression> expressionList = new LinkedList<Expression>();
      for(String expr : rawExpressions)
      {
         expressionList.add(new Expression(expr));
      }
      expressions = expressionList.toArray(new Expression[expressionList.size()]);
   }

   /**
    *
    *
    * @return
    * @throws VariableExpansionException
    */
   public String expand() throws VariableExpansionException
   {
      String template = getTemplate();
      for(Expression expression : expressions)
      {
         final String replacement = expressionReplacementString(expression);
         template = template.replaceAll(expression.getReplacementPattern(), replacement);
      }
      return template;
   }


   /**
    *
    *
    * @param operator
    * @param varSpecs
    * @throws VariableExpansionException
    * @return
    */
   private String expressionReplacementString(Expression expression) throws VariableExpansionException
   {
      final Operator operator = expression.getOperator();
      final List<String> replacements = expandVariables(expression);
      String result = joinParts(operator.getSeparator(), replacements);
      if (result != null)
      {
         if (operator != Operator.RESERVED)
         {
            result = operator.getPrefix() + result;
         }
      }
      else
      {
         result = "";
      }
      return result;
   }

   /**
    *
    *
    * @param operator
    * @param varSpecs
    * @throws VariableExpansionException
    * @return
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   private List<String> expandVariables(Expression expression)
         throws VariableExpansionException
   {
      final List<String> replacements = new ArrayList<String>();
      final Operator operator = expression.getOperator();
      for (VarSpec varSpec : expression.getVarSpecs())
      {
         if (values.containsKey(varSpec.getVariableName()))
         {
            Object value = values.get(varSpec.getVariableName());
            String expanded = null;


            if (value != null)
            {
               if (value.getClass().isArray())
               {
                  if (value instanceof char[][])
                  {
                     final char[][] chars = (char[][]) value;
                     final List<String> strings = new ArrayList<String>();
                     for (char[] c : chars)
                     {
                        strings.add(String.valueOf(c));
                     }
                     value = strings;
                  }
                  else if (value instanceof char[])
                  {
                     value = String.valueOf((char[]) value);
                  }
                  else
                  {
                     value = arrayToList(value);
                  }

               }
            }
            final boolean explodable = isExplodable(value);
            if(explodable && varSpec.getModifier() == Modifier.PREFIX)
            {
               throw new VariableExpansionException("Prefix modifiers are not applicable to variables that have composite values.");
            }

            if (explodable)
            {
               final VarExploder exploder;
               if (value instanceof VarExploder)
               {
                  exploder = (VarExploder) value;
               }
               else
               {
                  exploder = VarExploderFactory.getExploder(value, varSpec);
               }
               if(varSpec.getModifier() == Modifier.EXPLODE)
               {
                  expanded = expandMap(operator, varSpec, exploder.getNameValuePairs());
               }
               else if (varSpec.getModifier() != Modifier.EXPLODE)
               {
                  expanded = expandCollection(operator, varSpec, exploder.getValues());
               }
            }





            /*
             * Format the date if we have a java.util.Date
             */
            if (value instanceof Date)
            {
               value = defaultDateFormat.format((Date) value);
            }
            /*
             * The variable value contains a list of values
             */
            if (value instanceof Collection)
            {
               expanded = this.expandCollection(operator, varSpec, (Collection) value);
            }
            /*
             * The variable value contains a list of key-value pairs
             */
            else if (value instanceof Map)
            {
               expanded = expandMap(operator, varSpec, (Map) value);
            }
            /*
             * The variable value is null or has o value.
             */
            else if (value == null)
            {
               expanded = null;
            }
            else if (expanded == null)
            {
               expanded = this.expandStringValue(operator, varSpec, value.toString(), VarSpec.VarFormat.SINGLE);
            }
            if (expanded != null)
            {
               replacements.add(expanded);
            }

         }
      }
      return replacements;
   }

   /**
    *
    *
    * @param value
    * @return
    */
   private boolean isExplodable(Object value)
   {
      if(value == null)
      {
         return false;
      }
      if (value instanceof Collection || value instanceof Map || value.getClass().isArray())
      {
         return true;
      }
      if (!isSimpleType(value))
      {
         return true;
      }
      return false;
   }

   /**
    * Returns true of the object is:
    *
    * <ul>
    * <li>a primitive type</li>
    * <li>an instance of {@link CharSequence}</li>
    * <li>an instance of {@link Number} <li>
    * <li>an instance of {@link Date} <li>
    * </ul>
    *
    * @param value
    * @return
    */
   private boolean isSimpleType(Object value)
   {

      if(value.getClass().isPrimitive() ||
            value instanceof Number ||
            value instanceof CharSequence ||
            value instanceof Date ||
            value instanceof Boolean)
      {
         return true;
      }

      return false;
   }
   /**
    *
    *
    * @param operator
    * @param varSpec
    * @param variable
    * @return
    */
   private String expandCollection(Operator operator, VarSpec varSpec, Collection<?> variable) throws VariableExpansionException
   {
      final List<String> stringValues = new ArrayList<String>();
      final Iterator<?> i = variable.iterator();
      String separator = operator.getSeparator();
      if (varSpec.getModifier() != Modifier.EXPLODE)
      {
         separator = operator.getListSeparator();
      }
      while (i.hasNext())
      {
         final Object obj = i.next();
         checkValue(obj);
         final String value = obj.toString();
         stringValues.add(expandStringValue(operator, varSpec, value, VarSpec.VarFormat.ARRAY));
      }

      if (varSpec.getModifier() != Modifier.EXPLODE && operator.useVarNameWhenExploded())
      {
    	  final String values = joinParts(separator, stringValues);
    	  if(operator == Operator.QUERY && values == null)
    	  {
    		  return varSpec.getVariableName() + "=";
    	  }
    	  return varSpec.getVariableName() + "=" + values;
      }
      return joinParts(separator, stringValues);
   }

   /**
    * Check to ensure that the values being passed down do not contain nested data structures.
    * @param obj
    */
   private void checkValue(Object obj) throws VariableExpansionException
   {
      if (obj instanceof Collection || obj instanceof Map || obj.getClass().isArray())
      {
         throw new VariableExpansionException("Nested data structures are not supported.");
      }
   }

   /**
    *
    *
    * @param operator
    * @param varSpec
    * @param variable
    * @return
    */
   private String expandMap(Operator operator, VarSpec varSpec, Map<String, Object> variable) throws VariableExpansionException
   {
      List<String> stringValues = new ArrayList<String>();
      String pairJoiner = "=";
      if (varSpec.getModifier() != Modifier.EXPLODE)
      {
         pairJoiner = ",";
      }
      String joiner = operator.getSeparator();
      if (varSpec.getModifier() != Modifier.EXPLODE)
      {
         joiner = operator.getListSeparator();
      }
      for (Entry<String, Object> entry : variable.entrySet())
      {

         String key = entry.getKey();
         checkValue(entry.getValue());
         String pair = expandStringValue(operator, varSpec, key, VarSpec.VarFormat.PAIRS) + pairJoiner
               + expandStringValue(operator, varSpec, entry.getValue().toString(), VarSpec.VarFormat.PAIRS);

         stringValues.add(pair);
      }

      if (varSpec.getModifier() != Modifier.EXPLODE
            && (operator == Operator.MATRIX || operator == Operator.QUERY || operator == Operator.CONTINUATION))
      {
    	  String joinedValues = joinParts(joiner, stringValues);
    	  if(operator == Operator.QUERY && joinedValues == null)
    	  {
    		  return varSpec.getVariableName() + "=";
    	  }
    	  return varSpec.getVariableName() + "=" + joinedValues;
      }

      return joinParts(joiner, stringValues);
   }

   /**
    *
    *
    * @param operator
    * @param varSpec
    * @param variable
    * @param format
    * @return
    */
   private String expandStringValue(Operator operator, VarSpec varSpec, String variable, VarSpec.VarFormat format) throws VariableExpansionException
   {
      String expanded = "";

      if (varSpec.getModifier() == Modifier.PREFIX)
      {
         int position = varSpec.getPosition();
         if (position < variable.length())
         {
            variable = variable.substring(0, position);
         }
      }

      try
      {
         if (operator.getEncoding() == Encoding.UR)
         {
            expanded = UriUtil.encodeFragment(variable);
         }
         else
         {
            expanded = UriUtil.encode(variable);
         }
      }
      catch (UnsupportedEncodingException e)
      {
        throw new VariableExpansionException("Could not expand variable due to a problem URI encoding the value.", e);
      }

      if (operator.isNamed())
      {
         if (expanded.isEmpty() && !operator.getSeparator().equals("&"))
         {
            expanded = varSpec.getValue();
         }
         else if (format == VarSpec.VarFormat.SINGLE)
         {
            expanded = varSpec.getVariableName() + "=" + expanded;
         }

         else
         {
            if (varSpec.getModifier() == Modifier.EXPLODE)
            {
               if (operator.useVarNameWhenExploded() && format != VarSpec.VarFormat.PAIRS)
               {
                  expanded = varSpec.getVariableName() + "=" + expanded;
               }
            }
         }
      }
      return expanded;
   }

   /**
    * FIXME Comment this
    *
    * @param joiner
    * @param parts
    * @return
    */
   private String joinParts(final String joiner, List<String> parts)
   {
      if (parts.size() == 0)
      {
         return null;
      }

      if (parts.size() == 1)
      {
         return parts.get(0);
      }

      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i < parts.size(); i++)
      {
         final String part = parts.get(i);
         if (!part.isEmpty())
         {
            builder.append(part);
            if (parts.size() > 0 && i != (parts.size() - 1))
            {
               builder.append(joiner);
            }
         }

      }
      return builder.toString();
   }




   /**
    * Takes an array of objects and converts them to a {@link List}.
    *
    * @param array
    * @return
    */
   private List<Object> arrayToList(Object array) throws VariableExpansionException
   {
      List<Object> list = new ArrayList<Object>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++)
      {
         final Object element = Array.get(array, i);
         if (element.getClass().isArray())
         {
            throw new VariableExpansionException("Multi-dimenesional arrays are not supported.");
         }
         list.add(element);
      }
      return list;
   }



}
