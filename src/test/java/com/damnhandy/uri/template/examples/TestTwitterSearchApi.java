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

import com.damnhandy.uri.template.UriTemplate;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestTwitterSearchApi extends AbstractExampleTest
{

   private static final String SEARCH_BASE = "http://search.twitter.com/search.{format}";

   private static final String SEARCH_PARAMS = "{?q,callback,geocode,lang,locale,page,result_type,rpp,show_user,until,since_id,max_id,include_entities,result_type}";

   /**
    * Check to make sure that search.twitter.com is reachable
    *
    * @throws Exception
    */
   @BeforeClass
   public static void setUp() throws Exception
   {
       try {
           Assume.assumeTrue(InetAddress.getByName("search.twitter.com").isReachable(1000));
       }
       catch (UnknownHostException e)
       {
           Assume.assumeFalse(true);
       }

   }
   /**
    *
    *
    * @throws Exception
    */
   @Test
   public void testSearch() throws Exception
   {
      RequestBuilder builder = new RequestBuilder("GET");
      String uri = UriTemplate.buildFromTemplate(SEARCH_BASE)
                                     .literal(SEARCH_PARAMS)
                                     .build()
                                     .set("format", "json")
                                     .set("q", "URI Templates")
                                     .set("rpp", "5")
                                     .set("include_entities", true)
                                     .set("result_type", "mixed")
                                     .expand();
      Request request = builder.setUrl(uri).build();
      Assert.assertEquals("http://search.twitter.com/search.json?q=URI%20Templates&result_type=mixed&rpp=5&include_entities=true&result_type=mixed", uri);
      executeRequest(createClient(), request);
   }


}
