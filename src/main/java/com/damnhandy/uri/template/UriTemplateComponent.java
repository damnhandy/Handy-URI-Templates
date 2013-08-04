/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A Component.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public abstract class UriTemplateComponent implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -3653287624355221784L;
   
   /**
    * 
    */
   private final int startPosition;
   
   public UriTemplateComponent(final int startPoistion)
   {
      this.startPosition = startPoistion;
   }
   
   public abstract String getValue();

   /**
    * Get the startPosition of the component within the template string.
    * 
    * @return the startPosition.
    */
   public int getStartPosition()
   {
      return startPosition;
   }

   /**
    * Returns a string that contains a regular expression that matches the
    * URI template expression.
    * 
    * @return
    */
   public abstract Pattern getMatchPattern();
   /**
    * Get the endPosition.
    * 
    * @return the endPosition.
    */
   public int getEndPosition()
   {
      return startPosition + getValue().length();
   }
}
