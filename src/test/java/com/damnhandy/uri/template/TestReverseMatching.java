/*
 *
 *
 */
package com.damnhandy.uri.template;



import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * A TestReverseMatching.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestReverseMatching
{

   @Test
   public void testLevel1RevsereMatch() throws Exception
   {
      UriTemplateResolver resolver = new UriTemplateResolver();
      resolver.addUriTemplates("http://example.com/{foo}", 
                               "http://example.com/{foo}/{bar}",
                               "http://example.com/{foo}/{bar}{?things}");
      
      Map<String, Object> values = resolver.match("http://example.com/boo/moo");
      Assert.assertNotNull(values);
   }

}
