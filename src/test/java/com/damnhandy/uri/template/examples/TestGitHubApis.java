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
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * Some simple tests to format {@link UriTemplate} for the various GitHub APIs. This test assumes
 * that the following system properties are set:
 * </p>
 * <pre>
 * -Dgithub.username=<your github username>
 * -Dgithub.password=<your github password>
 * </pre>
 * <p>
 * If the properties are set, the test will not fail the build.
 * </p>
 * <p>This test demonstrates the utility of a single URI template that can be used
 * to express the majority of the functionality offered by the GitHub API. For this test,
 * the following template expression is used:
 * </p>
 * <pre>
 * /repos{/user,repo,function,id}{?page,per_page}
 * </pre>
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */

public class TestGitHubApis extends AbstractExampleTest
{

   public static final String BASE = "https://api.github.com";

   public static final String PATH_EXPRESSION = "/repos{/user,repo,function,id}";

   public static final String PAGINATION = "{?page,per_page}";
   /**
    * Checks assumptions that the username and password properties are set and that
    * the host is reachable.
    *
    */
   @BeforeClass
   public static void setUp() throws Exception
   {

      try {
          Assume.assumeTrue(InetAddress.getByName("api.github.com").isReachable(100));
          Assume.assumeNotNull(System.getProperty("github.username"),
                  System.getProperty("github.password"));
      }
      catch (UnknownHostException e)
      {
           Assume.assumeFalse(true);
      }


   }


   @Test
   public void testUserRepos() throws Exception
   {


      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
            UriTemplate.buildFromTemplate(BASE)
                               .literal("/users/{user}/repos")
                               .literal(PAGINATION)
                               .build()
                               .set("user", "damnhandy")
                               .set("repo", "repos")
                               .set("page", 1)
                               .set("per_page", 4)
                               .expand()).build();
      Assert.assertEquals("https://api.github.com/users/damnhandy/repos?page=1&per_page=4", request.getUrl());
      executeRequest(createClient(), request);
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


      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
            UriTemplate.buildFromTemplate(BASE)
                               .literal(PATH_EXPRESSION)
                               .build()
                               .set("user", "damnhandy")
                               .set("repo", "Handy-URI-Templates")
                               .set("function","commits")
                               .expand()).build();
      Assert.assertEquals("https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits", request.getUrl());
      executeRequest(createClient(), request);
   }


   /**
    * Looks at a specific commit
    *
    * @throws Exception
    */
   @Test
   public void testCommitsApiWithSpecificCommit() throws Exception
   {


      RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
            UriTemplate.buildFromTemplate(BASE)
                              .literal(PATH_EXPRESSION)
                              .build()
                              .set("user", "damnhandy")
                              .set("repo", "Handy-URI-Templates")
                              .set("function","commits")
                              .set("id","7cdf7ff75f8ede138228ceff7f5a1c18a5835b94")
                              .expand()).build();
      Assert.assertEquals("https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits/7cdf7ff75f8ede138228ceff7f5a1c18a5835b94", request.getUrl());
      executeRequest(createClient(), request);
   }

   protected AsyncHttpClient createClient() {
      return this.createClient(System.getProperty("github.username"), System.getProperty("github.password"));
   }


}
