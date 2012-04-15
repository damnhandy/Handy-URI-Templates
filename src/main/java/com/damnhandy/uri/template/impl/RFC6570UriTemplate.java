/*
 *
 */
package com.damnhandy.uri.template.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import com.damnhandy.uri.template.UriTemplate;

/**
 * A RFC6570UriTemplate.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class RFC6570UriTemplate extends UriTemplate
{

   private static final Pattern URI_TEMPLATE_REGEX = Pattern.compile("\\{[^{}]+\\}");

   /**
    * 
    */
   private static final String[] OPERATORS =
   {"+", "#", ".", "/", ";", "?", "&"};

   /**
    * 
    */
   private String template;

   /**
    * 
    */
   private Map<String, Object> vars;

   /**
    * 
    */
   private URLCodec urlCodec = new URLCodec("UTF-8");

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
    * Expand the URI template using the supplied variables
    * 
    * @param vars The variables that will be used in the expansion
    * @return the expanded URI as a String
    */
   @Override
   public String expand(Map<String, Object> vars)
   {
      this.vars = vars;
      return findMatches();
   }

   /**
    * Returns the original URI template string.
    * 
    * @return
    */
   @Override
   public String getTemplate()
   {
      return template;
   }

   /**
    * FIXME Comment this
    * 
    * @return
    */
   private String findMatches()
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
    * FIXME Comment this
    * 
    * @param operator
    * @param varSpecs
    * @return
    */
   private String expandMatch(String operator, List<VarSpec> varSpecs)
   {
      String result = "";
      String prefix = operator;
      String joiner = operator;
      boolean useQueryString = false;
      if (operator.equals("?"))
      {
         joiner = "&";
         useQueryString = true;
      }
      else if (operator.equals("&"))
      {
         useQueryString = true;
      }
      else if (operator.endsWith("#"))
      {
         joiner = ",";

      }
      else if (operator.endsWith(";"))
      {
         useQueryString = true;
      }
      else if (operator.equals("") || operator.endsWith("+"))
      {
         joiner = ",";
         prefix = "";
      }

      List<String> replacements = getReplacementValues(operator, varSpecs, joiner, useQueryString);

      result = joinParts(joiner, replacements);
      if (result != null && prefix != "")
      {
         result = prefix + result;
      }
      else
      {
         result = "";
      }
      return result;
   }

   /** FIXME Comment this
    * 
    * @param operator
    * @param varSpecs
    * @param joiner
    * @param useQueryString
    * @return
    */
   @SuppressWarnings(
   {"rawtypes", "unchecked"})
   private List<String> getReplacementValues(String operator, List<VarSpec> varSpecs, String joiner,
         boolean useQueryString)
   {
      List<String> replacements = new ArrayList<String>();

      for (VarSpec varSpec : varSpecs)
      {
         if (vars.containsKey(varSpec.getVariableName()) || varSpec.hasDefaultValue())
         {
            Object var = vars.get(varSpec.getVariableName());
            String expanded = null;
            if (var == null)
            {
               var = varSpec.getDefaultValue();
            }

            if (var instanceof Collection)
            {
               expanded = this.extractCollection(operator, joiner, useQueryString, varSpec, (Collection) var);
            }

            else if (var instanceof Map)
            {
               expanded = extractMap(operator, joiner, useQueryString, varSpec, (Map) var);
            }
            else if (var == null)
            {
               expanded = null;
            }
            else
            {
               String variable = var.toString();

               expanded = this.extractStringValue(operator, joiner, useQueryString, varSpec, variable);
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
    * FIXME Comment this
    * 
    * @param operator
    * @param joiner
    * @param useQueryString
    * @param varSpec
    * @param variable
    * @return
    */
   private String extractCollection(String operator, String joiner, boolean useQueryString, VarSpec varSpec,
         Collection<?> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      Iterator<?> i = variable.iterator();
      String pairJoiner = ",";
      if (varSpec.getModifier().equals("*"))
      {
         if (operator.equalsIgnoreCase("?") || operator.equalsIgnoreCase("&"))
         {
            {
               pairJoiner = "&";
            }
         }
         else if (!operator.equalsIgnoreCase(""))
         {
            {
               pairJoiner = operator;
            }
         }
      }

      while (i.hasNext())
      {
         String value = i.next().toString();
         if (varSpec.getModifier().equals("*") && !operator.equalsIgnoreCase(""))
         {
            if (operator.equals("/"))
            {
               stringValues.add(extractStringValue(operator, operator, false, varSpec, value));
               pairJoiner = operator;
            }
            else
            {
               value = varSpec.getVariableName() + "=" + extractStringValue(operator, joiner, false, varSpec, value);
               stringValues.add(value);
            }
         }
         else
         {
            stringValues.add(extractStringValue(operator, ",", false, varSpec, value));
         }

      }

      if (!operator.equalsIgnoreCase("") && !operator.equals("+"))
      {
         if (operator.equals("/") || varSpec.getModifier().equals("*"))
         {
            return joinParts(pairJoiner, stringValues);
         }
         return varSpec.getVariableName() + "=" + joinParts(pairJoiner, stringValues);
      }
      return joinParts(joiner, stringValues);
   }

   /**
    * FIXME Comment this
    * 
    * @param operator
    * @param joiner
    * @param useQueryString
    * @param varSpec
    * @param variable
    * @return
    */
   private String extractMap(String operator, String joiner, boolean useQueryString, VarSpec varSpec,
         Map<String, Object> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      for (Entry<String, Object> entry : variable.entrySet())
      {

         String key = entry.getKey();

         if (varSpec.getModifier().equals("+"))
         {
            key = varSpec.getVariableName() + "." + key;
         }
         String pairJoiner = joiner;;
         if (varSpec.getModifier().equals("*"))
         {
            pairJoiner = "=";
         }
         else if (!varSpec.getModifier().equals("*") && (operator.equals("/") || operator.equals(";")))
         {
            joiner = ",";
            pairJoiner = ",";
         }

         String pair = extractStringValue(operator, joiner, useQueryString, varSpec, key) + pairJoiner
               + extractStringValue(operator, joiner, useQueryString, varSpec, entry.getValue().toString());

         stringValues.add(pair);
      }

      return joinParts(joiner, stringValues);
   }

   /** FIXME Comment this
    * 
    * @param operator
    * @param joiner
    * @param useQueryString
    * @param varSpec
    * @param variable
    * @return
    */
   private String extractStringValue(String operator, String joiner, boolean useQueryString, VarSpec varSpec,
         String variable)
   {
      String expanded = "";
      if (varSpec.getModifier().equals(":"))
      {
         int position = varSpec.getPosition();
         if (position < variable.length())
         {
            variable = variable.substring(0, position);
         }
      }
      try
      {
         if (operator.equals("+") || operator.equals("#"))
         {

            expanded = variable.replaceAll(" ", "%20");
         }
         else
         {
            //TODO: Find a better encoder
            expanded = urlCodec.encode(variable).replaceAll("\\+", "%20");
         }
      }

      catch (EncoderException e)
      {
         throw new RuntimeException(e);
      }

      if (useQueryString)
      {
         if (expanded.isEmpty() && !joiner.equals("&"))
         {
            expanded = varSpec.getValue();
         }
         else
         {
            if (expanded.isEmpty())
            {
               expanded = varSpec.getValue();
            }
            else
            {
               expanded = varSpec.getValue() + "=" + expanded;
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
   private String joinParts(String joiner, List<String> parts)
   {
      if (parts.size() == 0)
      {
         return null;
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
    * FIXME Comment this
    * 
    * @param token
    * @return
    */
   private String buildVarSpecs(String token)
   {
      // Check for URI operators
      String operator = "";
      String firstChar = token.substring(0, 1);
      if (containsOperator(firstChar))
      {
         operator = firstChar;
         token = token.substring(1, token.length());
      }

      String[] values = token.split(",");
      List<VarSpec> vars = new ArrayList<VarSpec>();

      for (String value : values)
      {
         value = value.trim();
         int subStrPos = value.indexOf(':');
         int defaultPos = value.indexOf('=');
         if (subStrPos > 0)
         {
            String[] pair = value.split(":");
            Integer pos = Integer.valueOf(value.substring(subStrPos + 1));
            vars.add(new VarSpec(pair[0], ":", pos));
         }
         else if (defaultPos > 0)
         {
            String[] pair = value.split("=");
            VarSpec v = new VarSpec(pair[0], "=");
            v.setDefaultValue(pair[1]);
            vars.add(v);
         }
         else if (value.lastIndexOf("*") > 0)
         {
            vars.add(new VarSpec(value, "*"));
         }
         else if (value.lastIndexOf("+") > 0)
         {
            vars.add(new VarSpec(value, "+"));
         }
         else
         {
            vars.add(new VarSpec(value, ""));
         }
      }
      return expandMatch(operator, vars);

   }

   /**
    * FIXME Comment this
    * 
    * @param op
    * @return
    */
   private boolean containsOperator(String op)
   {
      for (String operator : OPERATORS)
      {
         if (operator.equals(op))
         {
            return true;
         }
      }
      return false;
   }
}
