/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 * 
 *
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public enum Modifier {

   NONE         (""),
   PREFIX       (":"), 
   EXPLODE      ("*");

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
}