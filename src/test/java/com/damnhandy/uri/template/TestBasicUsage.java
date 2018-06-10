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

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Simple tests to validate the UriTemplate API.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestBasicUsage
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

   /**
    * Test that validates issue #12
    *
    * https://github.com/damnhandy/Handy-URI-Templates/issues/12
    *
    * @throws Exception
    */
   @Test
   public void testNoExpressionAndNoVars() throws Exception {
       String result = UriTemplate.expand("https://foo.com:8080", new HashMap<String, Object>());
       Assert.assertEquals("https://foo.com:8080", result);
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


    @Test
    public void testFragmentWithPrecentEncodedValues() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate("http://myhost")
                                          .fragment("message")
                                          .build().set("message","Hello%20World");

        Assert.assertEquals("http://myhost{#message}",template.getTemplate());
        Assert.assertEquals("http://myhost#Hello%20World",template.expand());

    }

    @Test
    public void testReservedWithPrecentEncodedValues() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate("http://myhost/")
                                            .reserved("message")
                                            .build().set("message","Hello%20World");

        Assert.assertEquals("http://myhost/{+message}",template.getTemplate());
        Assert.assertEquals("http://myhost/Hello%20World",template.expand());

    }

    @Test
    public void testFragmentWithMixedPrecentEncodedValues() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate("http://myhost")
                                          .fragment("message")
                                          .build().set("message","Hello%20World,everything is cool!");

        Assert.assertEquals("http://myhost{#message}",template.getTemplate());
        Assert.assertEquals("http://myhost#Hello%20World,everything%20is%20cool!",template.expand());
    }

    @Test
    public void testReservedWithMixedPrecentEncodedValues() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate("http://myhost/")
                                           .reserved("message")
                                           .build().set("message","Hello%20World,everything is cool!");

        Assert.assertEquals("http://myhost/{+message}",template.getTemplate());
        Assert.assertEquals("http://myhost/Hello%20World,everything%20is%20cool!",template.expand());
    }

    @Test
    public void testReservedExpansionTest1() throws Exception
    {
        UriTemplate t = UriTemplate.fromTemplate("{+half}");
        t.set("half","50%25");
        Assert.assertEquals("50%25",t.expand());
    }

    @Test
    public void testReservedExpansionTest2() throws Exception
    {
        UriTemplate t = UriTemplate.fromTemplate("{+half}");
        t.set("half","50%");
        Assert.assertEquals("50%25",t.expand());
    }

    /**
     * <p>
     *     Test case to verify issue #60 with the undesired result
     * </p>
     */
    @Test
    public void testWithCommadelimitedListEscaped() {
        String actual = UriTemplate.fromTemplate("http://localhost/thing{?things}")
            .set("things", "1,2,3")
            .expand();
        assertThat(actual, equalTo("http://localhost/thing?things=1%2C2%2C3"));
    }

    /**
     * <p>
     *     Test case to verify issue #60 with the desired result
     * </p>
     */
    @Test
    public void testWithCommadelimitedListNoteEscaped() {
        String actual = UriTemplate.fromTemplate("http://localhost/thing{?things}")
        .set("things", "1,2,3".split(","))
        .expand();
        assertThat(actual, equalTo("http://localhost/thing?things=1,2,3"));
    }


    //@Test
    public void testRegEx() throws Exception
    {

        String sourceValue = "This%20is a%30test";
        Pattern p = Pattern.compile("%[0-9A-Fa-f]{2}");
        Matcher m = p.matcher(sourceValue);


        if(m.find())
        {
            List<int[]> positions = new ArrayList<int[]>();
            while (m.find())
            {
                positions.add(new int[] {m.start(), m.end()});
            }
            StringBuilder b = new StringBuilder();
            int offset = 0;
            for(int[] pos : positions)
            {
                b.append(UriUtil.encodeFragment(sourceValue.substring(offset,pos[0])));
                b.append(sourceValue.substring(pos[0],pos[1]));
                offset = pos[1];
            }
            b.append(UriUtil.encodeFragment(sourceValue.substring(offset,sourceValue.length())));
            System.out.println(b.toString());
        }

    }
}
