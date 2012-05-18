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
public class ExpressionParseException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -3912835265423887511L;

   /**
    * Create a new InvalidExpressionException.
    * 
    */
   public ExpressionParseException()
   {
      super();
   }

   /**
    * Create a new InvalidExpressionException.
    * 
    * @param message
    * @param cause
    */
   public ExpressionParseException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Create a new InvalidExpressionException.
    * 
    * @param message
    */
   public ExpressionParseException(String message)
   {
      super(message);
   }

   /**
    * Create a new InvalidExpressionException.
    * 
    * @param cause
    */
   public ExpressionParseException(Throwable cause)
   {
      super(cause);
   }

   
}
