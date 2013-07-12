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

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;

import com.ning.http.client.AsyncCompletionHandlerBase;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Realm.AuthScheme;

/**
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractExampleTest
{

   /**
    *
    * @param request
    * @throws InterruptedException
    * @throws ExecutionException
    * @throws TimeoutException
    * @throws IOException
    */
   protected void executeRequest(AsyncHttpClient client, Request request) throws InterruptedException, ExecutionException, TimeoutException,
         IOException
   {
      final CountDownLatch l = new CountDownLatch(1);
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

   /**
    *
    *
    * @return
    */
   protected AsyncHttpClient createClient(String principal, String password)
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



   protected AsyncHttpClient createClient()
   {
      return new AsyncHttpClient();
   }
}
