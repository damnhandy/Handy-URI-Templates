/*
 * 
 */
package com.damnhandy.uri.template;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Map.Entry;

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
      UriTemplate base = UriTemplate.fromTemplate("http://myhost{/version,myId}")
                                    .set("myId","damnhandy")
                                    .set("version","v1"); // URI versioning is silly, but this is just for examples
      
      UriTemplate child = UriTemplate.fromTemplate(base)
                                     .append("/things/{thingId}")
                                     .set("thingId","123öä|\\");
      
      
      Assert.assertEquals(3, child.getValues().size());
      Map<String, Object> childValues = child.getValues();
      for(Entry<String, Object> e : base.getValues().entrySet())
      {
         Assert.assertTrue(childValues.containsKey(e.getKey()));
         Assert.assertEquals(e.getValue(), childValues.get(e.getKey()));
      }
      Assert.assertEquals("http://myhost/v1/damnhandy/things/123%C3%B6%C3%A4%7C%5C", child.expand());
   }
   
   
   @Test
   public void testMultpleExpressions() throws Exception
   {
      UriTemplate template = UriTemplate.fromTemplate("http://myhost")
                                        .append("{/version}")
                                        .append("{/myId}")
                                        .append(null)
                                        .append(" ")
                                        .append("/things/{thingId}")
                                        .set("myId","damnhandy")
                                        .set("version","v1")
                                        .set("thingId","12345");
      
      Assert.assertEquals(3, template.getValues().size());
      Assert.assertEquals("http://myhost{/version}{/myId}/things/{thingId}", template.getTemplate());
      Assert.assertEquals("http://myhost/v1/damnhandy/things/12345", template.expand());
   }
}
