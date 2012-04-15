/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.Map;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

/**
 * 
 * A UriTemplate.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public abstract class UriTemplate
{

   /**
    * FIXME Comment this
    * 
    * @param template
    * @return
    */
   public static UriTemplate create(String template) {
      return new RFC6570UriTemplate(template);
   }
   /**
    * Expand the URI template using the supplied variables
    * 
    * @param vars The variables that will be used in the expansion
    * @return the expanded URI as a String
    */
   public abstract String expand(Map<String, Object> vars);

   /**
    * Returns the original URI template string.
    * 
    * @return
    */
   public abstract String getTemplate();

}