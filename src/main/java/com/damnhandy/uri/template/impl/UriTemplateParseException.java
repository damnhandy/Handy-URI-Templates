/*
 *
 *
 */
package com.damnhandy.uri.template.impl;

/**
 * This {@link RuntimeException} is raised when the the template processor
 * encounters an issue parsing the URI template string. It indicates 
 * the template contains either no expressions or that an expression is
 * malformed. 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.2
 */
public class UriTemplateParseException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 5883174281977078450L;

   /**
    * Create a new UriTemplateParseException.
    * 
    */
   public UriTemplateParseException()
   {
      super();
   }

   /**
    * Create a new UriTemplateParseException.
    * 
    * @param message
    * @param cause
    */
   public UriTemplateParseException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Create a new UriTemplateParseException.
    * 
    * @param message
    */
   public UriTemplateParseException(String message)
   {
      super(message);
   }

   /**
    * Create a new UriTemplateParseException.
    * 
    * @param cause
    */
   public UriTemplateParseException(Throwable cause)
   {
      super(cause);
   }

}
