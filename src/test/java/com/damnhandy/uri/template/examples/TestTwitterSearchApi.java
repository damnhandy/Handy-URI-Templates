/*
 * 
 */
package com.damnhandy.uri.template.examples;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;

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
      Assume.assumeTrue(InetAddress.getByName("search.twitter.com").isReachable(4000));  
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
      String uri = UriTemplate.fromExpression(SEARCH_BASE)
                              .expression(SEARCH_PARAMS)
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
