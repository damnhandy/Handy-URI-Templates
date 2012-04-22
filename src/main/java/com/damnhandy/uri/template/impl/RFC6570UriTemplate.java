/*
 *
 */
package com.damnhandy.uri.template.impl;

import info.aduna.net.UriUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.VarExploder;

/**
 * A {@link UriTemplate} implementation that supports <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a>
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class RFC6570UriTemplate extends UriTemplate
{

   private static final Pattern URI_TEMPLATE_REGEX = Pattern.compile("\\{[^{}]+\\}");

   /**
    * Create a new RFC6570UriTemplate.
    * 
    * @param template
    */
   public RFC6570UriTemplate(String template)
   {
      this.template = template;
   }

   /**
    * Expand the URI template using the supplied values
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
      Matcher matcher = URI_TEMPLATE_REGEX.matcher(template);
      StringBuffer buffer = new StringBuffer();
      while (matcher.find())
      {
         String token = matcher.group();
         String value = buildVarSpecs(token.substring(1, token.length() - 1));
         matcher.appendReplacement(buffer, value);
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
         if (operator == Operator.RESERVED || operator == Operator.NAME_LABEL)
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
            Object var = values.get(varSpec.getVariableName());
            String expanded = null;
            
            boolean literal = true;
            if (var != null)
            {
               literal = var.getClass().isPrimitive();
               if (var.getClass().isArray())
               {
                  var = arrayToList(var);
               }
            }

            if (!literal && varSpec.getModifier() == Modifier.EXPLODE)
            {
               VarExploder exploder = VarExploderFactory.getExploder(var, varSpec);
               expanded = expandMap(operator, varSpec, exploder.getNameValuePairs());
            }
            if (varSpec.getModifier() != Modifier.EXPLODE && var instanceof VarExploder)
            {
               throw new VariableExpansionException(varSpec.getVariableName() + " was passed a "
                     + VarExploder.class.getSimpleName() + " but the variable did not include the explode modifer.");
            }
            /*
             * The variable value contains a list of values
             */
            if (var instanceof Collection)
            {
               expanded = this.expandCollection(operator, varSpec, (Collection) var);
            }
            /*
             * The variable value contains a list of key-value pairs
             */
            else if (var instanceof Map)
            {
               expanded = expandMap(operator, varSpec, (Map) var);
            }
            /*
             * The variable value is null or has o value.
             */
            else if (var == null)
            {
               expanded = null;
            }
            else if (expanded == null)
            {
               expanded = this.expandStringValue(operator, varSpec, var.toString(), VarSpec.VarFormat.SINGLE);
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
    * @param operator
    * @param varSpec
    * @param variable
    * @return
    */
   private String expandCollection(Operator operator, VarSpec varSpec, Collection<?> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      Iterator<?> i = variable.iterator();
      String joiner = operator.getSeparator();
      if (varSpec.getModifier() == Modifier.EXPLODE)
      {
         joiner = operator.getExplodeSeparator();
      }
      else
      {
         joiner = operator.getListSeparator();
      }
      while (i.hasNext())
      {
         String value = i.next().toString();
         stringValues.add(expandStringValue(operator, varSpec, value, VarSpec.VarFormat.ARRAY));
      }

      if (varSpec.getModifier() != Modifier.EXPLODE && operator.useVarNameWhenExploded())
      {
         return varSpec.getVariableName() + "=" + joinParts(joiner, stringValues);
      }
      return joinParts(joiner, stringValues);
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

         String pair = expandStringValue(operator, varSpec, key, VarSpec.VarFormat.PAIRS) + pairJoiner
               + expandStringValue(operator, varSpec, entry.getValue().toString(), VarSpec.VarFormat.PAIRS);

         stringValues.add(pair);
      }

      if (varSpec.getModifier() != Modifier.EXPLODE
            && (operator == Operator.MATRIX || operator == Operator.QUERY || operator == Operator.CONTINUATION))
      {
         return varSpec.getVariableName() + "=" + joinParts(joiner, stringValues);
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
      // TODO: The URI encoding logic here is pretty hackish, needs help!
      if (operator == Operator.NAME_LABEL || operator == Operator.RESERVED)
      {
         expanded = variable.replaceAll("%", "%25").replaceAll(" ", "%20");
      }
      else if (operator == Operator.FRAGMENT)
      {
         expanded = UriUtil.encodeUri(variable);
      }
      else
      {
         try
         {
            expanded = URLEncoder.encode(variable, "UTF-8").replaceAll("\\+", "%20");
         }
         catch (UnsupportedEncodingException e)
         {
            throw new VariableExpansionException(e);
         }
      }

      if (operator.useQueryString())
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
            if (expanded.isEmpty())
            {
               expanded = varSpec.getValue();
            }
            else if (varSpec.getModifier() == Modifier.EXPLODE)
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
      Operator operator = Operator.NONE;
      String firstChar = token.substring(0, 1);
      if (containsOperator(firstChar))
      {
         operator = Operator.fromOpCode(firstChar);
         token = token.substring(1, token.length());
      }

      String[] values = token.split(",");
      List<VarSpec> vars = new ArrayList<VarSpec>();

      for (String value : values)
      {
         value = value.trim();
         int subStrPos = value.indexOf(Modifier.PREFIX.getValue());
         /*
          * Prefex variable 
          */
         if (subStrPos > 0)
         {
            String[] pair = value.split(Modifier.PREFIX.getValue());
            Integer pos = Integer.valueOf(value.substring(subStrPos + 1));
            vars.add(new VarSpec(pair[0], Modifier.PREFIX, pos));
         }

         /*
          * Variable will be exploded
          */
         else if (value.lastIndexOf(Modifier.EXPLODE.getValue()) > 0)
         {
            vars.add(new VarSpec(value, Modifier.EXPLODE));
         }
         /*
          * Standard Value
          */
         else
         {
            vars.add(new VarSpec(value, Modifier.NONE));
         }
      }
      return findExpressions(operator, vars);

   }

   /**
    * Takes an array of objects and convertes them to a {@link List}.
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
         list.add(Array.get(array, i));
      }
      return list;
   }
}
