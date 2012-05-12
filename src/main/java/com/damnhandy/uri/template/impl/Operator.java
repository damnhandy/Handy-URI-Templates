/*
 * 
 */
package com.damnhandy.uri.template.impl;

import static com.damnhandy.uri.template.UriTemplate.DEFAULT_SEPARATOR;

/**
 * <p>
 * An enum representing an operator in a URI Template.
 * </p>
 *       Type    Separator
                 ","     (default)
        +        ","
        #        ","
        .        "."
        /        "/"
        ;        ";"
        ?        "&"
        &        "&"
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public enum Operator {

   
   
   NONE        ("",  DEFAULT_SEPARATOR,  false), 
   RESERVED    ("+", DEFAULT_SEPARATOR,  false), 
   FRAGMENT    ("#", DEFAULT_SEPARATOR,  false), 
   NAME_LABEL  (".", ".",  false), 
   PATH        ("/", "/",  false), 
   MATRIX      (";", ";",  true), 
   QUERY       ("?", "&",  true), 
   CONTINUATION("&", "&",  true);

   
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
    * Create a new Operator.
    * 
    * @param operator
    * @param separator
    */
   private Operator(String operator, String separator, boolean queryString)
   {
      this.operator = operator;
      this.separator = separator;
      this.queryString = queryString;
   }

   public String getOperator()
   {
      return this.operator;
   }

   public String getSeparator()
   {
      return this.separator;
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
      return DEFAULT_SEPARATOR;
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
      return queryString;
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