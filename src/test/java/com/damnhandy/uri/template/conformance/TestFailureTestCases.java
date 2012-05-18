/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.impl.ExpressionParseException;

/**
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestFailureTestCases extends AbstractUriTemplateConformanceTest
{
   @Parameters
   public static Collection<Object[]> testData() throws Exception
   {
      File file = new File("./uritemplate-test/negative-tests.json");
      return loadTestData(file);
   }

   public TestFailureTestCases(Map<String, Object> vars, String template, Object expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

   /**
    * 
    * @throws Exception
    */
   @Test(expected = ExpressionParseException.class)
   public void test() throws Exception
   {
      UriTemplate t = UriTemplate.fromExpression(template);
      String actual = t.expand(variables);
      System.out.println(actual);
   }
}
