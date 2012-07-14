/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 * <p>
 * This {@link UriTemplateParseException} is raised when the the template processor
 * encounters an issue expanding the value into the variable at expansion time.
 * Typical scenarios are:
 * </p>
 * <ul>
 *  <li>The variable specification declares a prefix but the value is an array</li>
 *  <li>A value is a {@link java.util.List} of {@link java.util.List} or an array of array</li>
 * </ul>
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class VariableExpansionException extends UriTemplateParseException
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
