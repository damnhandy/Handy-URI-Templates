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
package com.damnhandy.uri.template.examples;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;

/**
 * A collection of tests that demonstrate how URI Templates can be used to
 * interact with the Facebook Graph API. These tests use some of the examples
 * found on the Facebook Graph API reference:
 *
 * https://developers.facebook.com/docs/reference/api/
 *
 * You will need your Facebook access token in a System property:
 * <pre>
 * -Dfb.access_token=<your facebook access_token>
 * </pre>
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
@Ignore
public class TestFacebookGraphApi extends AbstractExampleTest
{

   private static final String GRAPH_API_EXPRESSION =
            "https://graph.facebook.com{/id*}{?q,ids,fields,type,center,distance,limit,offset,until,since,access_token}";

   /**
    * Checks assumptions that the Facebook Access token is set.
    *
    */
   @BeforeClass
   public static void setUp()
   {
      Assume.assumeNotNull(System.getProperty("fb.access_token"));
   }

   @Test
   public void facebookGraphApiFQL() throws Exception
   {
      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id","fql")
                              .set("q", "SELECT uid2 FROM friend WHERE uid1=me()")
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();
      System.out.println(uri);
      Assert.assertEquals("https://graph.facebook.com/fql?q=SELECT%20uid2%20FROM%20friend%20WHERE%20uid1%3Dme%28%29&access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");

      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   @Test
   public void facebookGraphApiAllFields() throws Exception
   {
      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id", "bgolub")
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();

      Assert.assertEquals("https://graph.facebook.com/bgolub?access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   @Test
   public void facebookGraphApiGetSubResource() throws Exception
   {
      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id", new String[] {"bgolub","albums"})
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();

      Assert.assertEquals("https://graph.facebook.com/bgolub/albums?access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   /**
    *
    *
    * @throws Exception
    */
   @Test
   public void facebookGraphApiSelectiveFields() throws Exception
   {
      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id", "bgolub")
                              .set("fields", new String[] {"id", "name", "picture"})
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();

      Assert.assertEquals("https://graph.facebook.com/bgolub?fields=id,name,picture&access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   /**
    * Same as the previous test, but demonstrates how a {@link List} can be used
    * rather than an array.
    *
    * @throws Exception
    */
   @Test
   public void facebookGraphApiSelectiveFieldsWithListWithAlbums() throws Exception
   {
      List<String> fields = new ArrayList<String>(3);
      fields.add("id");
      fields.add("name");
      fields.add("count");

      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id", new String[] {"bgolub","albums"})
                              .set("fields", fields)
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();
      System.out.println(uri);
      Assert.assertEquals("https://graph.facebook.com/bgolub/albums?fields=id,name,count&access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   @Test
   public void facebookGraphApiSelectiveFieldsWithList() throws Exception
   {
      List<String> fields = new ArrayList<String>(3);
      fields.add("id");
      fields.add("name");
      fields.add("picture");

      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id", "bgolub")
                              .set("fields", fields)
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();

      Assert.assertEquals("https://graph.facebook.com/bgolub?fields=id,name,picture&access_token="+System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(uri).build();
      executeRequest(createClient(), request);
   }

   /**
    *
    *
    * @throws Exception
    */
   @Test
   public void facebookGraphApiPlacesSearch() throws Exception
   {
      String uri = UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                              .set("id","search")
                              .set("q", "coffee")
                              .set("type","place")
                              .set("center", new float[] {37.76f,-122.427f})
                              .set("distance", 1000)
                              .set("limit", 5)
                              .set("offset", 10)
                              .set("access_token", System.getProperty("fb.access_token"))
                              .expand();

      Assert.assertEquals(
            "https://graph.facebook.com/search?q=coffee&type=place&center=37.76,-122.427&distance=1000&limit=5&offset=10&access_token="
                  + System.getProperty("fb.access_token"), uri);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request =
            builder.setUrl(UriTemplate.fromTemplate(GRAPH_API_EXPRESSION)
                                      .set("id","search")
                                      .set("q", "coffee")
                                      .set("type","place")
                                      .set("center", new float[] {37.76f,-122.427f})
                                      .set("distance", 1000)
                                      .set("limit", 5)
                                      .set("offset", 10)
                                      .set("access_token", System.getProperty("fb.access_token"))
                                      .expand()).build();
      executeRequest(createClient(), request);
   }
}
