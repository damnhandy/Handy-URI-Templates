/*
 *
 */
package com.damnhandy.uri.template.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.UriUtil;
import com.damnhandy.uri.template.VarExploder;

/**
 * A {@link UriTemplate} implementation that supports <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a>
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class RFC6570UriTemplate extends UriTemplate
{
   /**
    * Regex to locate the variable lists
    */
   private static final Pattern URI_TEMPLATE_REGEX = Pattern.compile("\\{[^{}]+\\}");

   /**
    * Regex to validate the variable name.
    */
   static final Pattern VARNAME_REGEX = Pattern.compile("([\\w\\_\\.]|%[A-Fa-f0-9]{2})+");

   /**
    * Create a new RFC6570UriTemplate.
    * 
    * @param template
    */
   public RFC6570UriTemplate(String template)
   {
      this.setTemplate(template);
   }

   /**
    * Expand the URI expression using the supplied values
    * 
    * @param values The values that will be used in the expansion
    * @return the expanded URI as a String
    */
   @Override
   public String expand(Map<String, Object> vars)
   {
      this.values = vars;
      return expand();
   }

   /**
    * 
    * 
    * @return
    */
   public String expand()
   {
      
      final String template = getTemplate();
      Matcher matcher = URI_TEMPLATE_REGEX.matcher(template);
      StringBuffer buffer = new StringBuffer();
      int count = 0;
      while (matcher.find())
      {
         
         String token = matcher.group();
         String value = buildVarSpecs(token.substring(1, token.length() - 1));
         matcher.appendReplacement(buffer, value);
         count++;
      }
      if(count == 0)
      {
         throw new ExpressionParseException("no variables found");
      }
      matcher.appendTail(buffer);
      return buffer.toString();
   }
   
   
   /**
    * 
    * 
    * @param operator
    * @param varSpecs
    * @return
    */
   private String findExpressions(Operator operator, List<VarSpec> varSpecs)
   {
      List<String> replacements = expandVariables(operator, varSpecs);
      String result = joinParts(operator.getSeparator(), replacements);
      if (result != null)
      {
         if (operator == Operator.RESERVED)
         {
            return result;
         }
         else
         {
            return operator.getPrefix() + result;
         }
      }
      return "";
   }

   /**
    * 
    * 
    * @param operator
    * @param varSpecs
    * @return
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   private List<String> expandVariables(Operator operator, List<VarSpec> varSpecs)
   {
      List<String> replacements = new ArrayList<String>();

      for (VarSpec varSpec : varSpecs)
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
                     char[][] chars = (char[][]) value;
                     List<String> strings = new ArrayList<String>();
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
            boolean explodable = isExplodable(value);
            if(explodable && varSpec.getModifier() == Modifier.PREFIX)
            {
               throw new VariableExpansionException("Prefix modifiers are not applicable to variables that have composite values.");
            }
            
            if (explodable) {
               VarExploder exploder;
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
   private String expandCollection(Operator operator, VarSpec varSpec, Collection<?> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      Iterator<?> i = variable.iterator();
      String separator = operator.getSeparator();
      if (varSpec.getModifier() != Modifier.EXPLODE)
      {
         separator = operator.getListSeparator();
      }
      while (i.hasNext())
      {
         Object obj = i.next();
         checkValue(obj);
         String value = obj.toString();
         stringValues.add(expandStringValue(operator, varSpec, value, VarSpec.VarFormat.ARRAY));
      }

      if (varSpec.getModifier() != Modifier.EXPLODE && operator.useVarNameWhenExploded())
      {
    	  String values = joinParts(separator, stringValues);
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
   private void checkValue(Object obj)
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
   private String expandMap(Operator operator, VarSpec varSpec, Map<String, Object> variable)
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
   private String expandStringValue(Operator operator, VarSpec varSpec, String variable, VarSpec.VarFormat format)
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

      if (operator.getEncoding() == Encoding.UR)
      {
         expanded = UriUtil.encodeFragment(variable);
      }
      else
      {
         expanded = UriUtil.encode(variable);
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
    * 
    * 
    * @param joiner
    * @param parts
    * @return
    */
   private String joinParts(String joiner, List<String> parts)
   {
      if (parts.size() == 0)
      {
         return null;
      }

      if (parts.size() == 1)
      {
         return parts.get(0);
      }

      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < parts.size(); i++)
      {
         String part = parts.get(i);
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
    * 
    * 
    * @param token
    * @return
    */
   private String buildVarSpecs(String token)
   {
      // Check for URI operators
      Operator operator = Operator.NUL;
      String firstChar = token.substring(0, 1);
      if (containsOperator(firstChar))
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
               throw new ExpressionParseException("The prefix value for "+ pair[0]+ " was not a number", e);
            }
         }

         /*
          * Variable will be exploded
          */
         else if (varname.lastIndexOf(Modifier.EXPLODE.getValue()) > 0)
         {
            validateVarname(varname.substring(0, (varname.length() - 1)));
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
      return findExpressions(operator, varspecs);
   }

   private void validateVarname(String varname) {
      Matcher matcher = VARNAME_REGEX.matcher(varname);
      if(!matcher.matches())
      {
         throw new ExpressionParseException("The variable name "+varname+" contains invalid characters");
      }
      
      if(varname.contains(" "))
      {
         throw new ExpressionParseException("The variable name "+varname+" cannot contain spaces (leading or trailing)");
      }
   }
   /**
    * Takes an array of objects and converts them to a {@link List}.
    * 
    * @param array
    * @return
    */
   private List<Object> arrayToList(Object array)
   {
      List<Object> list = new ArrayList<Object>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++)
      {
         Object element = Array.get(array, i);
         if (element.getClass().isArray())
         {
            throw new VariableExpansionException("Multi-dimenesional arrays are not supported.");
         }
         list.add(element);
      }
      return list;
   }
   
  
}
