/*
 *
 *
 */
package com.damnhandy.uri.template;

import org.junit.Test;

/**
 * A TestUriTemplateBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateBuilder
{

   @Test
   public void testCreateBasicTemplate() throws Exception
   {
      UriTemplateBuilder builder = UriTemplateBuilder.fromTemplate("") ;
      builder.append("foo");
   }
}
