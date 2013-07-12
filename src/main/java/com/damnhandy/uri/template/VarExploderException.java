/*
 *
 *
 */
package com.damnhandy.uri.template;

/**
 * A VarExploderException.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.2
 */
public class VarExploderException extends VariableExpansionException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -3859548780063196996L;

   /**
    * Create a new VarExploderException.
    *
    * @param message
    * @param cause
    */
   public VarExploderException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Create a new VarExploderException.
    *
    * @param cause
    */
   public VarExploderException(Throwable cause)
   {
      super(cause);
   }


}
