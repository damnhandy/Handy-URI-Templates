/*
 * 
 */
package com.damnhandy.uri.template.impl;

public enum Modifier {

   NONE         ("na"),
   PREFIX       (":" ), 
   EXPLODE      ("*" ),
   DEFAULT_VALUE("=" );

   /**
    * 
    */
   private String value;

   /**
    * 
    * Create a new Modifier.
    * 
    * @param value
    */
   private Modifier(String value)
   {
      this.value = value;
   }

   /**
    * 
    * 
    * @return
    */
   public String getValue()
   {
      return value;
   }

   /**
    * 
    * 
    * @param value
    * @return
    */
   public static Modifier fromValue(String value)
   {
      for (Modifier m : Modifier.values())
      {
         if (m.getValue().equalsIgnoreCase(value))
         {
            return m;
         }
      }
      return null;
   }
}