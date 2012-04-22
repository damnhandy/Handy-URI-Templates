/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 * <p>
 * Represents an operator in a URI Template.
 * </p>
 */
public enum Operator {

   NONE        ("",  ",", ",", false, false), 
   RESERVED    ("+", ",", ",", false, false), 
   FRAGMENT    ("#", ",", ",", false, false), 
   NAME_LABEL  (".", ",", ".", false, false), 
   PATH        ("/", "/", "/", false, false), 
   MATRIX      (";", ";", ";", true, true), 
   QUERY       ("?", "&", "&", true, true), 
   CONTINUATION("&", "&", "&", true, true);

   /**
    * 
    */
   private String operator;

   /**
    * 
    */
   private String separator;

   /**
    * 
    */
   private boolean queryString;

   /**
    * 
    */
   private String explodeSeparator;

   /**
    * 
    */
   private boolean varNameWhenExploded;

   /**
    * 
    * Create a new Operator.
    * 
    * @param operator
    * @param separator
    */
   private Operator(String operator, String separator, String explodeJoiner, boolean queryString,
         boolean varNameWhenExploded)
   {
      this.operator = operator;
      this.separator = separator;
      this.queryString = queryString;
      this.explodeSeparator = explodeJoiner;
      this.varNameWhenExploded = varNameWhenExploded;
   }

   public String getOperator()
   {
      return this.operator;
   }

   public String getSeparator()
   {
      return this.separator;
   }

   public String getExplodeSeparator()
   {
      return explodeSeparator;
   }

   /**
    * 
    * 
    * @return
    */
   public boolean useQueryString()
   {
      return queryString;
   }

   /**
    */
   public String getListSeparator()
   {
      return ",";
   }

   /**
    * When the variable is a Collection, this flag determines if we use 
    * the VarSpec name to prefix values. For example:
    * 
    * {&list} return false
    * 
    * {&list*} will return true 
    * 
    * @return
    */
   public boolean useVarNameWhenExploded()
   {
      return varNameWhenExploded;
   }

   /**
    * 
    * 
    * @return
    */
   public String getPrefix()
   {
      return operator;
   }

   /**
    * FIXME Comment this
    * 
    * @param opCode
    * @return
    */
   public static Operator fromOpCode(String opCode)
   {
      for (Operator op : Operator.values())
      {
         if (op.getOperator().equalsIgnoreCase(opCode))
         {
            return op;
         }
      }
      return null;
   }
}