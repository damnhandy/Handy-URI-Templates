/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.impl.UriTemplateParseException;

/**
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestNegativeTests extends AbstractUriTemplateConformanceTest
{
   @Parameters
   public static Collection<Object[]> testData() throws Exception
   {
      File file = new File("./uritemplate-test/negative-tests.json");
      return loadTestData(file);
   }

   public TestNegativeTests(Map<String, Object> vars, String template, Object expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

   /**
    * 
    * @throws Exception
    */
   @Test
   public void test() throws Exception
   {
      boolean pass = true;
      String actual = "";
      try
      {
         UriTemplate t = UriTemplate.fromTemplate(template);
         actual = t.expand(variables);
         System.out.println(actual);
      }
      catch (UriTemplateParseException e)
      {
         pass = false;
      }
      Assert.assertFalse("Expected "+template+" to fail but got "+actual,pass);
   }
}
