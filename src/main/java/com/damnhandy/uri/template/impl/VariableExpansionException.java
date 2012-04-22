/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 *
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class VariableExpansionException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -1927979719672747848L;

   /**
    * Create a new VariableExpansionException.
    * 
    */
   public VariableExpansionException()
   {
      

   }

   /**
    * Create a new VariableExpansionException.
    * 
    * @param message
    */
   public VariableExpansionException(String message)
   {
      super(message);
   }

   /**
    * Create a new VariableExpansionException.
    * 
    * @param cause
    */
   public VariableExpansionException(Throwable cause)
   {
      super(cause);
   }

   /**
    * Create a new VariableExpansionException.
    * 
    * @param message
    * @param cause
    */
   public VariableExpansionException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
