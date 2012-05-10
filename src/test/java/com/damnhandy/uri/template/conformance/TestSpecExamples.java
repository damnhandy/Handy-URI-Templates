/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestSpecExamples extends AbstractUriTemplateConformanceTest
{
   
   @Parameters
   public static Collection<Object[]> testData() throws Exception
   {
      File file = new File("./uritemplate-test/spec-examples.json");
      return loadTestData(file);
   }

   public TestSpecExamples(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

}
