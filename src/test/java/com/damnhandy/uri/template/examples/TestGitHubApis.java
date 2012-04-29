/*
 * 
 */
package com.damnhandy.uri.template.examples;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.ning.http.client.AsyncCompletionHandlerBase;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

/**
 * <p>
 * Some simple tests to format UriTemplateFactory for the various GitHub APIs. This test assumes
 * that the following system properties are set:
 * </p>
 * <pre>
 * -Dgithub.username=<your github username>
 * -Dgithub.password=<your github password>
 * </pre>
 * <p>
 * If the properties are set, the test will execute.
 * </p>
 * <p>This test demonstrates the utility of a single URI template that can be used
 * to express the majority of the functionality offered by the GitHub API. For this test,
 * the following template expression is used:
 * </p>
 * <pre>
 * /repos{/user,repo,function,id}
 * </pre>
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestGitHubApis
{

   public static final String BASE = "https://api.github.com";

   public static final String PATH_EXPRESSION = "/repos{/user,repo,function,id}"; 
   /**
    * Checks assumptions that the username and password properties are set.
    *
    */
   @BeforeClass
   public static void setUp()
   {
      Assume.assumeNotNull(System.getProperty("github.username"), 
                           System.getProperty("github.password"));
   }
   
   /**
    * This test demonstrates that while the expression defined 4 variables but
    * we only supply 3 values, the expanded URI will not render a dangling "/" 
    * and generate a proper URI for the GitHub API.
    * 
    * @throws Exception
    */
   @Test
   public void testCommitsApi() throws Exception
   {
      
      final CountDownLatch l = new CountDownLatch(1);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
             UriTemplate.expressionWithBase(BASE, PATH_EXPRESSION)
                        .set("user", "damnhandy")
                        .set("repo", "Handy-URI-Templates")
                        .set("function","commits")
                        .expand()).build();
      Assert.assertEquals("https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits", request.getRawUrl());
      AsyncHttpClient client = createClient();
      client.executeRequest(request, new AsyncCompletionHandlerBase()
      {
         @Override
         public Response onCompleted(Response response) throws Exception
         {
            Assert.assertEquals(200, response.getStatusCode());
            //System.out.println(response.getResponseBody());
            l.countDown();
            return super.onCompleted(response);
         }
      }).get(3, TimeUnit.SECONDS);

      if (!l.await(1000, TimeUnit.SECONDS))
      {
         Assert.fail("Timeout out");
      }
      client.close();
   }
   
   @Test
   public void testCommitsApiWithSpecificCommit() throws Exception
   {
      
      final CountDownLatch l = new CountDownLatch(1);
      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
            UriTemplate.expressionWithBase(BASE, PATH_EXPRESSION)
                       .set("user", "damnhandy")
                       .set("repo", "Handy-URI-Templates")
                       .set("function","commits")
                       .set("id","7cdf7ff75f8ede138228ceff7f5a1c18a5835b94")
                       .expand()).build();
      Assert.assertEquals("https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits/7cdf7ff75f8ede138228ceff7f5a1c18a5835b94", request.getRawUrl());
      AsyncHttpClient client = createClient();
      client.executeRequest(request, new AsyncCompletionHandlerBase()
      {
         @Override
         public Response onCompleted(Response response) throws Exception
         {
            Assert.assertEquals(200, response.getStatusCode());
            //System.out.println(response.getResponseBody());
            l.countDown();
            return super.onCompleted(response);
         }
      }).get(3, TimeUnit.SECONDS);

      if (!l.await(1000, TimeUnit.SECONDS))
      {
         Assert.fail("Timeout out");
      }
      client.close();
   }

   private AsyncHttpClient createClient()
   {
      Builder builder = new AsyncHttpClientConfig.Builder();
      Realm realm = new Realm.RealmBuilder()
            .setPrincipal(System.getProperty("github.username"))
            .setPassword(System.getProperty("github.password"))
            .setUsePreemptiveAuth(true)
            .setScheme(AuthScheme.BASIC).build();
      builder.setRealm(realm);
      return new AsyncHttpClient(builder.build());
   }
}
