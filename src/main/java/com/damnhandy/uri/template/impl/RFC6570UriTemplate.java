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
   private String expression;

   /**
    * 
    */
   private Map<String, Object> variables;

   /**
    * 
    */
   private URLCodec urlCodec = new URLCodec("UTF-8");

   /**
    * Create a new RFC6570UriTemplate.
    * 
    * @param expression
    */
   public RFC6570UriTemplate(String template)
   {
      this.expression = template;
   }

   /**
    * Returns the original URI expression string.
    * 
    * @return
    */
   @Override
   public String getExpression()
   {
      return expression;
   }

   /**
    * Expand the URI expression using the supplied variables
    * 
    * @param variables The variables that will be used in the expansion
    * @return the expanded URI as a String
    */
   @Override
   public String expand(Map<String, Object> vars)
   {
      this.variables = vars;
      Matcher matcher = URI_TEMPLATE_REGEX.matcher(expression);
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
   private String expandMatch(Op operator, List<VarSpec> varSpecs)
   {
      List<String> replacements = getReplacementValues(operator, varSpecs);
      String result = joinParts(operator.getJoiner(), replacements);
      if (result != null)
      {
         return operator.getPrefix() + result;
      }
      return "";
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
   private List<String> getReplacementValues(Op operator, List<VarSpec> varSpecs)
   {
      List<String> replacements = new ArrayList<String>();

      for (VarSpec varSpec : varSpecs)
      {
         if (variables.containsKey(varSpec.getVariableName()) || varSpec.hasDefaultValue())
         {
            Object var = variables.get(varSpec.getVariableName());
            String expanded = null;
            /*
             * The there is no supplied value for the variable and the default
             * value is used.
             */
            if (var == null)
            {
               var = varSpec.getDefaultValue();
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
            else
            {
               expanded = this.expandStringValue(operator, varSpec, var.toString());
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
   private String expandCollection(Op operator, VarSpec varSpec, Collection<?> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      Iterator<?> i = variable.iterator();
      String pairJoiner = ",";
      if (varSpec.getModifier().equals("*"))
      {
         if (operator == Op.QUERY || operator == Op.CONTINUATION)
         {
            {
               pairJoiner = "&";
            }
         }
         else if (operator != Op.NAME_LABEL)
         {
            {
               pairJoiner = operator.getJoiner();
            }
         }
      }

      while (i.hasNext())
      {
         String value = i.next().toString();
         if (varSpec.getModifier().equals("*") && operator != Op.NONE)
         {
            if (operator.equals("/"))
            {
               stringValues.add(expandStringValue(operator, varSpec, value));
               pairJoiner = operator.getJoiner();
            }
            else
            {
               value = varSpec.getVariableName() + "=" + expandStringValue(operator, varSpec, value);
               stringValues.add(value);
            }
         }
         else
         {
            stringValues.add(expandStringValue(operator, varSpec, value));
         }

      }

      if (operator != Op.NONE && operator != Op.NAME_LABEL)
      {
         if (operator == Op.MATRIX || varSpec.getModifier() == Modifier.EXPLODE)
         {
            return joinParts(pairJoiner, stringValues);
         }
         return varSpec.getVariableName() + "=" + joinParts(pairJoiner, stringValues);
      }
      return joinParts(operator.getJoiner(), stringValues);
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
   private String expandMap(Op operator, VarSpec varSpec, Map<String, Object> variable)
   {
      List<String> stringValues = new ArrayList<String>();
      for (Entry<String, Object> entry : variable.entrySet())
      {

         String key = entry.getKey();
         String pairJoiner = operator.getJoiner();
         if (varSpec.getModifier() == Modifier.EXPLODE)
         {
            pairJoiner = "=";
         }
         else if (varSpec.getModifier() != Modifier.EXPLODE && (operator == Op.PATH || operator == Op.MATRIX))
         {
            pairJoiner = ",";
         }

         String pair = expandStringValue(operator, varSpec, key) + pairJoiner
               + expandStringValue(operator, varSpec, entry.getValue().toString());

         stringValues.add(pair);
      }

      return joinParts(operator.getJoiner(), stringValues);
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
   private String expandStringValue(Op operator, VarSpec varSpec, String variable)
   {
      String expanded = "";
      // TODO: if the value is not a string, it needs to raise an exception.
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
         if (operator == Op.NAME_LABEL || operator == Op.FRAGMENT)
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

      if (operator.useQueryString())
      {
         if (expanded.isEmpty() && !operator.getJoiner().equals("&"))
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
    * FIXME Comment this
    * 
    * @param token
    * @return
    */
   private String buildVarSpecs(String token)
   {
      // Check for URI operators
      Op operator = Op.NONE;
      String firstChar = token.substring(0, 1);
      if (containsOperator(firstChar))
      {
         operator = Op.valueOfOpCode(firstChar);
         token = token.substring(1, token.length());
      }

      String[] values = token.split(",");
      List<VarSpec> vars = new ArrayList<VarSpec>();

      for (String value : values)
      {
         value = value.trim();
         int subStrPos = value.indexOf(Modifier.PREFIX.getValue());
         int defaultPos = value.indexOf(Modifier.DEFAULT_VALUE.getValue());
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
          * If the variable contains a default value
          */
         else if (defaultPos > 0)
         {
            String[] pair = value.split(Modifier.DEFAULT_VALUE.getValue());
            VarSpec v = new VarSpec(pair[0], Modifier.DEFAULT_VALUE);
            v.setDefaultValue(pair[1]);
            vars.add(v);
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
