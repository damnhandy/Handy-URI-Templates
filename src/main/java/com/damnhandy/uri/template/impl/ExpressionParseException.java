/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 * This {@link RuntimeException} is raised when the the template processor
 * encounters an issue parsing the URI template expression. It indicates 
 * the expression is malformed. 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public class ExpressionParseException extends UriTemplateParseException
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
