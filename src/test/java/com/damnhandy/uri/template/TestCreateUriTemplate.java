/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Simple tests to validate the UriTemplate API.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestCreateUriTemplate
{

   @Test
   public void testFromTemplate() throws Exception
   {
      UriTemplate base = UriTemplate.fromExpression("http://myhost{/version,myId}")
                                    .set("myId","damnhandy")
                                    .set("version","v1"); // URI versioning is silly, but this is just for examples
      
      UriTemplate child = UriTemplate.fromTemplate(base)
                                     .expression("/things/{thingId}")
                                     .set("thingId","12345");
      
      
      Assert.assertEquals(3, child.getValues().size());
      Map<String, Object> childValues = child.getValues();
      for(Entry<String, Object> e : base.getValues().entrySet())
      {
         Assert.assertTrue(childValues.containsKey(e.getKey()));
         Assert.assertEquals(e.getValue(), childValues.get(e.getKey()));
      }
      Assert.assertEquals("http://myhost/v1/damnhandy/things/12345", child.expand());
   }
   
   
   @Test
   public void testMultpleExpressions() throws Exception
   {
      UriTemplate template = UriTemplate.fromExpression("http://myhost")
                                        .expression("{/version}")
                                        .expression("{/myId}")
                                        .expression(null)
                                        .expression(" ")
                                        .expression("/things/{thingId}")
                                        .set("myId","damnhandy")
                                        .set("version","v1")
                                        .set("thingId","12345");
      
      Assert.assertEquals(3, template.getValues().size());
      Assert.assertEquals("http://myhost{/version}{/myId}/things/{thingId}", template.getExpression());
      Assert.assertEquals("http://myhost/v1/damnhandy/things/12345", template.expand());
   }
}
