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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.damnhandy.uri.template.UriTemplateBuilder.var;

/**
 * A TestUriTemplateBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateBuilder
{

   private static final String VAR_NAME = "foo";
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

    @Test
    public void testCreateBasicTemplateWithUnderScores() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
        .literal("/foo")
        .path(var("thing1"), var("exploded_thing", true))
        .fragment(var("prefix", 2))
        .build();

        Assert.assertEquals("http://example.com/foo{/thing1,exploded_thing*}{#prefix:2}", template.getTemplate());
    }

    @Test
    public void testCreateFromBaseTemplate() throws Exception
    {

        UriTemplate rootTemplate = UriTemplate.fromTemplate("http://example.com/foo{/thing1}");

        UriTemplate template = UriTemplate.buildFromTemplate(rootTemplate)
                .path(var("explodedThing", true))
                .fragment(var("prefix", 2))
                .build();

        Assert.assertEquals("http://example.com/foo{/thing1}{/explodedThing*}{#prefix:2}", template.getTemplate());
    }


   @Test
   public void testLiteral() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
                                        .literal("/foo")
                                        .literal(null)
                                        .build();

      print(template);
      Assert.assertEquals("http://example.com/foo", template.getTemplate());
   }


   @Test
   public void testWithDateFormat() throws Exception
   {
      Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-04:00"));
      cal.set(Calendar.YEAR, 2012);
      cal.set(Calendar.MONTH, Calendar.APRIL);
      cal.set(Calendar.DAY_OF_MONTH, 20);
      cal.set(Calendar.HOUR_OF_DAY, 16);
      cal.set(Calendar.MINUTE, 20);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date date = cal.getTime();


      UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .literal("/foo")
                                        .path("date")
                                        .build();

      //print(template);
      template.set("date", date);

      Assert.assertEquals("http://example.com/foo{/date}", template.getTemplate());
      Assert.assertEquals("http://example.com/foo/2012-04-20", template.expand());
   }

   @Test
   public void testWithSimpleDateFormat() throws Exception
   {
      Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-04:00"));
      cal.set(Calendar.YEAR, 2012);
      cal.set(Calendar.MONTH, Calendar.APRIL);
      cal.set(Calendar.DAY_OF_MONTH, 20);
      cal.set(Calendar.HOUR_OF_DAY, 16);
      cal.set(Calendar.MINUTE, 20);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date date = cal.getTime();


      UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .literal("/foo")
                                        .path("date")
                                        .build();

      template.set("date", date);

      Assert.assertEquals("http://example.com/foo{/date}", template.getTemplate());
      Assert.assertEquals("http://example.com/foo/2012-04-20", template.expand());
   }

   private void print(UriTemplate template) throws Exception
   {
      System.out.println(template.getTemplate());
      System.out.println(template.set(VAR_NAME, "boo").expand());
      System.out.println(" ");
   }


   /**
    * This test fails as you shouldn't be able to create multiple fragment identifiers
    * in a URI.
    *
    * @throws Exception
    */
   @Test(expected = UriTemplateBuilderException.class)
   public void testMessedUpUri() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                        .reserved(VAR_NAME)
                                        .path(VAR_NAME)
                                        .query(VAR_NAME)
                                        .fragment(VAR_NAME)
                                        .matrix(VAR_NAME)
                                        .path(VAR_NAME)
                                        .path(VAR_NAME)
                                        .path(VAR_NAME)
                                        .fragment(VAR_NAME)
                                        .build();
   }

    @Test
    public void testSimpleExpression() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).simple(VAR_NAME).build();
        Assert.assertEquals("http://example.com/{foo}", template.getTemplate());
    }

   @Test
   public void testReservedExpression() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved(VAR_NAME).build();
      Assert.assertEquals("http://example.com/{+foo}", template.getTemplate());
   }

   @Test
   public void testReservedExpressionWithExplode() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved(var(VAR_NAME,true)).build();
      Assert.assertEquals("http://example.com/{+foo*}", template.getTemplate());
   }

   @Test
   public void testReservedExpressionWithExplodeAndPre() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).reserved(var(VAR_NAME,2)).build();
      Assert.assertEquals("http://example.com/{+foo:2}", template.getTemplate());
   }

   @Test
   public void testLabelExpression() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).label(VAR_NAME).build();
      Assert.assertEquals("http://example.com/{.foo}", template.getTemplate());
   }

   @Test
   public void testLabelExpressionWithExplode() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).label(var(VAR_NAME,true)).build();
      Assert.assertEquals("http://example.com/{.foo*}", template.getTemplate());
   }

   @Test
   public void testLabelExpressionWithExplodeAndPre() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).label(var(VAR_NAME,2)).build();
      Assert.assertEquals("http://example.com/{.foo:2}", template.getTemplate());
   }


   @Test
   public void testFragmentExpression() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).fragment("foo").build();
      Assert.assertEquals("http://example.com/{#foo}", template.getTemplate());
   }

   @Test
   public void testFragmentExpressionWithExplode() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).fragment(var("foo",true)).build();
      Assert.assertEquals("http://example.com/{#foo*}", template.getTemplate());
   }

   @Test
   public void testFragmentExpressionWithExplodeAndPre() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).fragment(var("foo",2)).build();
      Assert.assertEquals("http://example.com/{#foo:2}", template.getTemplate());
   }

    @Test
    public void testQueryContinuation() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).continuation("foo").build();
        Assert.assertEquals("http://example.com/{&foo}", template.getTemplate());
    }

    @Test
    public void testQueryContinuationWithExplode() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).continuation(var("foo",true)).build();
        Assert.assertEquals("http://example.com/{&foo*}", template.getTemplate());
    }

    @Test
    public void testQueryContinuationWithExplodeAndPre() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).continuation(var("foo",2)).build();
        Assert.assertEquals("http://example.com/{&foo:2}", template.getTemplate());
    }

   @Test
   public void testQueryExpression() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).query("foo").build();
      Assert.assertEquals("http://example.com/{?foo}", template.getTemplate());
   }

   @Test
   public void testQueryExpressionWithExplode() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).query(var("foo",true)).build();
      Assert.assertEquals("http://example.com/{?foo*}", template.getTemplate());
   }

   @Test
   public void testQueryExpressionWithExplodeAndPre() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).query(var("foo",2)).build();
      Assert.assertEquals("http://example.com/{?foo:2}", template.getTemplate());
   }

   @Test
   public void testTemplateExpression() throws Exception {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).template("bar/{id}{?filter}").build();

      Assert.assertEquals("http://example.com/bar/{id}{?filter}", template.getTemplate());
   }

   @Test
   public void testTemplateExpressionWithUriTemplate() throws Exception {
      UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI).template(UriTemplate.fromTemplate("bar/{id}{?filter}")).build();

      Assert.assertEquals("http://example.com/bar/{id}{?filter}", template.getTemplate());
   }


    @Test
    public void testCreateNew() throws Exception
    {
        UriTemplate template = UriTemplate.createBuilder()
                                          .literal("http://example.com")
                                          .path("something").query(var("foo",2)).build();
        Assert.assertEquals("http://example.com{/something}{?foo:2}", template.getTemplate());
    }


    @Test(expected = MalformedUriTemplateException.class)
    public void testInvalidVariableName() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                          .query("invalid-variable").build();
        Assert.fail();
    }

    @Test(expected = MalformedUriTemplateException.class)
    public void testInvalidVariableNameWithSpaces() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                          .fragment("invalid variable").build();
        Assert.fail();
    }

    @Test(expected = MalformedUriTemplateException.class)
    public void testInvalidVarNameUsingExpressionSyntax() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                          .query("{?invalid_variable}").build();
        Assert.fail();
    }

    @Test(expected = MalformedUriTemplateException.class)
    public void testInvalidVarNameWithTilde() throws Exception
    {
        UriTemplate template = UriTemplate.buildFromTemplate(BASE_URI)
                                          .path("~foo").build();
        Assert.fail();
    }


}
