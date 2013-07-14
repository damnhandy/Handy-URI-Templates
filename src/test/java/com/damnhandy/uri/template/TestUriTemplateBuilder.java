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

import static com.damnhandy.uri.template.UriTemplateBuilder.var;

import java.net.URI;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A TestUriTemplateBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateBuilder
{

   private static final String BASE_URI = "http://example.com/";

   @Test
   public void testCreateBasicTemplate() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
                                        .literal("/foo")
                                        .path(var("thing1"), var("explodedThing", true))
                                        .fragment(var("prefix", 2))
                                        .build();

      Assert.assertEquals("http://example.com/foo{/thing1,explodedThing*}{#prefix:2}", template.getTemplate());
   }

   private void print(UriTemplate template) throws Exception
   {
      System.out.println(template.getTemplate());
      System.out.println(template.set("foo", "boo").expand());
      System.out.println(" ");
   }
   
   
   @Test
   @Ignore
   public void testMessedUpUri() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                        .reserved("foo")
                                        .path("foo")
                                        .query("foo")
                                        .fragment("foo")
                                        .matrix("foo")
                                        .path("foo")
                                        .path("foo")
                                        .path("foo")
                                        .fragment("foo").build();
      print(template);
      
      String uri = template.set("foo", "boo").expand();
      URI u = new URI(uri);
   }
   
   
   
   @Test
   public void testReservedExpression() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved("foo").build();
      print(template);
   }

   @Test
   public void testReservedExpressionWithExplode() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved(var("foo",true)).build();
      print(template);
   }
   
   @Test
   public void testReservedExpressionWithExplodeAndPre() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved(var("foo",2)).build();
      print(template);
   }
}
