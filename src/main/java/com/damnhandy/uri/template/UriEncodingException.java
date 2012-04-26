/*
 * 
 */
package com.damnhandy.uri.template;

/**
 * A UriEncodingException.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class UriEncodingException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 51633959333775593L;

   /**
    * Create a new UriEncodingException.
    * 
    */
   public UriEncodingException()
   {
      super();
   }

   /**
    * Create a new UriEncodingException.
    * 
    * @param message
    * @param cause
    */
   public UriEncodingException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Create a new UriEncodingException.
    * 
    * @param message
    */
   public UriEncodingException(String message)
   {
      super(message);
   }

   /**
    * Create a new UriEncodingException.
    * 
    * @param cause
    */
   public UriEncodingException(Throwable cause)
   {
      super(cause);
   }

}
