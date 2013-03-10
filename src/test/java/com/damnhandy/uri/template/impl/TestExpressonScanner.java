/*
 *
 *
 */
package com.damnhandy.uri.template.impl;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import com.damnhandy.uri.template.MalformedUriTemplateException;

/**
 * A TestCharacterScanner.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExpressonScanner
{

   private static final Pattern URI_TEMPLATE_REGEX = Pattern.compile("\\{[^{}]+\\}");



   @Test
   public void testGoodTemplate() throws Exception
   {
      String template = "http://example.com/{expr}/thing/{other}";
      ExpressionScanner e = new ExpressionScanner();
      List<String> rawExpr = e.scan(template);
      List<String> regExExpr = scanWithRegEx(template);
      Assert.assertEquals(rawExpr.size(), 2);
      Assert.assertEquals(regExExpr, rawExpr);
   }


   @Test
   public void testGoodTemplateWithOperators() throws Exception
   {
      ExpressionScanner e = new ExpressionScanner();
      List<String> rawExpr = e.scan("http://example.com/{expr}/thing/{?other, thing}");
      Assert.assertEquals(rawExpr.size(), 2);
      System.out.println(rawExpr);
   }


   @Test(expected = MalformedUriTemplateException.class)
   public void testStartExpressionWithNoTermination() throws Exception
   {
      ExpressionScanner e = new ExpressionScanner();
      List<String> rawExpr = e.scan("http://example.com/{expr/thing");
      Assert.assertEquals(rawExpr.size(), 2);
      System.out.println(rawExpr);
   }


   @Test(expected = MalformedUriTemplateException.class)
   public void testStartExpressionWithTerminationButNoStartBrace() throws Exception
   {
      ExpressionScanner e = new ExpressionScanner();
      List<String> rawExpr = e.scan("http://example.com/expr}/thing");
      Assert.assertEquals(rawExpr.size(), 2);
      System.out.println(rawExpr);
   }


   @Test(expected = MalformedUriTemplateException.class)
   public void testUnbalanceExpression() throws Exception
   {
      ExpressionScanner e = new ExpressionScanner();
      List<String> rawExpr = e.scan("http://example.com/{expr/thing/{other}");
      Assert.assertNotNull(rawExpr);
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
