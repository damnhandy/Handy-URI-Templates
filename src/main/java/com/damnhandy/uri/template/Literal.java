/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.util.regex.Pattern;

/**
 * Represents the non-expression parts of a URI Template
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
public class Literal extends UriTemplateComponent
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 6011009312823496878L;

   private final String value;

   private final Pattern matchPattern;

   /**
    * Create a new Literal.
    * 
    */
   public Literal(final String value, int startPosition)
   {
      super(startPosition);
      this.value = value;
      this.matchPattern = Pattern.compile(Pattern.quote(getValue()));
   }

   @Override
   public String getValue()
   {
      return value;
   }

   @Override
   public String toString()
   {
      return value;
   }

   @Override
   public Pattern getMatchPattern()
   {
      return this.matchPattern;
   }

}
