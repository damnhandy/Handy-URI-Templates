/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
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
   public void testBasicExample() throws Exception {
      String uri =  UriTemplate.fromTemplate("/{foo:1}{/foo,thing*}{?query,test2}")
                               .set("foo", "houses")
                               .set("query", "Ask something")
                               .set("test2", "someting else")
                               .set("thing", "A test")
                               .expand();

      Assert.assertEquals("/h/houses/A%20test?query=Ask%20something&test2=someting%20else", uri);
   }
   
   @Test
   public void testFromTemplate() throws Exception
   {
      UriTemplate base = UriTemplate.fromTemplate("http://myhost{/version,myId}")
                                    .set("myId","damnhandy")
                                    .set("version","v1"); // URI versioning is silly, but this is just for examples

      Assert.assertEquals(2, base.getValues().size());
      UriTemplate child = UriTemplate.buildFromTemplate(base)
                                            .literal("/things")
                                            .path("thingId")
                                            .build()
                                            .set("thingId","123öä");


      Assert.assertEquals(3, child.getValues().size());
      Map<String, Object> childValues = child.getValues();
      for(Entry<String, Object> e : base.getValues().entrySet())
      {
         Assert.assertTrue(childValues.containsKey(e.getKey()));
         Assert.assertEquals(e.getValue(), childValues.get(e.getKey()));
      }
      Assert.assertEquals("http://myhost/v1/damnhandy/things/123%C3%B6%C3%A4", child.expand());
   }


   @Test
   public void testMultpleExpressions() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate("http://myhost")
                                               .path("version")
                                               .path("myId")
                                               .literal("/things/")
                                               .simple("thingId")
                                               .build()
                                               .set("myId","damnhandy")
                                               .set("version","v1")
                                               .set("thingId","12345");

      Assert.assertEquals(3, template.getValues().size());
      Assert.assertEquals("http://myhost{/version}{/myId}/things/{thingId}", template.getTemplate());
      Assert.assertEquals("http://myhost/v1/damnhandy/things/12345", template.expand());
   }
}
