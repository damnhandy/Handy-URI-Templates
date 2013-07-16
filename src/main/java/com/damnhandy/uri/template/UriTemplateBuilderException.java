/*
 *
 *
 */
package com.damnhandy.uri.template;

/**
 * Raised when there is invalid rule in the construction of a URI template, such as applying multiple
 * fragment expressions to a URI template. 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class UriTemplateBuilderException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -3011472988386059575L;

   /**
    * Create a new UriTemplateBuilderException.
    * 
    */
   public UriTemplateBuilderException()
   {
 
   }

   /**
    * Create a new UriTemplateBuilderException.
    * 
    * @param msg
    */
   public UriTemplateBuilderException(String msg)
   {
      super(msg);
   }

   /**
    * Create a new UriTemplateBuilderException.
    * 
    * @param e
    */
   public UriTemplateBuilderException(Throwable e)
   {
      super(e);
   }

   /**
    * Create a new UriTemplateBuilderException.
    * 
    * @param msg
    * @param e
    */
   public UriTemplateBuilderException(String msg, Throwable e)
   {
      super(msg, e);
   }

}
