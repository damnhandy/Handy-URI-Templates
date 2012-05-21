/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Ignore;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
@Ignore
public class TestSpecExamplesBySection extends AbstractUriTemplateConformanceTest
{
   
   @Parameters
   public static Collection<Object[]> testData() throws Exception
   {
      File file = new File("./uritemplate-test/spec-examples-by-section.json");
      return loadTestData(file);
   }

   public TestSpecExamplesBySection(Map<String, Object> vars, String template, Object expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

}
