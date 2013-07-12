/*
 *
 *
 */
package com.damnhandy.uri.template.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplateComponent;

/**
 * A TestCharacterScanner.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateParser
{

   private static final Pattern URI_TEMPLATE_REGEX = Pattern.compile("\\{[^{}]+\\}");

   @Test
   public void testWithNoExpressions() throws Exception
   {
      String template = "http://example.com/";
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan(template);
      Assert.assertEquals(1, expressions.size());
      Assert.assertEquals("http://example.com/", expressions.get(0).getValue());
   }

   @Test
   public void testGoodTemplate() throws Exception
   {
      String template = "http://example.com/{expr}/thing/{other}";
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan(template);
      List<String> regExExpr = scanWithRegEx(template);
      Assert.assertEquals(4, expressions.size());
      Assert.assertEquals("http://example.com/", expressions.get(0).getValue());
      Assert.assertEquals(regExExpr.get(0), expressions.get(1).getValue());
      Assert.assertEquals("/thing/", expressions.get(2).getValue());
      Assert.assertEquals(regExExpr.get(1), expressions.get(3).getValue());
   }


   @Test
   public void testGoodTemplateWithOperators() throws Exception
   {
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan("http://example.com/{expr}/thing/{?other,thing}");
      Assert.assertEquals(4, expressions.size());
      Assert.assertEquals("http://example.com/", expressions.get(0).getValue());
      Assert.assertEquals("{expr}", expressions.get(1).getValue());
      Assert.assertEquals("/thing/", expressions.get(2).getValue());
      Assert.assertEquals("{?other,thing}", expressions.get(3).getValue());
   }

   /**
    * Checking that we correctly catch an unbalanced expression
    * 
    * @throws Exception
    */
   @Test(expected = MalformedUriTemplateException.class)
   public void testStartExpressionWithNoTermination() throws Exception
   {
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan("http://example.com/{expr/thing");
      Assert.assertEquals(expressions.size(), 2);
      System.out.println(expressions);
   }

   /**
    * Since the expression is never opened, we should not find any expressions.
    * 
    * @throws Exception
    */
   @Test(expected = MalformedUriTemplateException.class)
   public void testStartExpressionWithTerminationButNoStartBrace() throws Exception
   {
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan("http://example.com/expr}/thing");
      Assert.assertEquals(expressions.size(), 2);
      System.out.println(expressions);
   }

   /**
    * Checking that we correctly catch an unbalanced expression
    * 
    * @throws Exception
    */
   @Test(expected = MalformedUriTemplateException.class)
   public void testUnbalanceExpression() throws Exception
   {
      UriTemplateParser e = new UriTemplateParser();
      List<UriTemplateComponent> expressions = e.scan("http://example.com/{expr/thing/{other}");
      Assert.assertNotNull(expressions);
   }

   
   private List<String> scanWithRegEx(String templateString)
   {
      Matcher matcher = URI_TEMPLATE_REGEX.matcher(templateString);
      List<String> expressionList = new LinkedList<String>();
      while (matcher.find())
      {
         String e = matcher.group();
         expressionList.add(e);
      }
      return expressionList;
   }
}
