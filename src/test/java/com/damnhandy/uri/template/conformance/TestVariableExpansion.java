/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * A TestVariableExpansion.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestVariableExpansion extends AbstractUriTemplateConformanceTest
{

   /**
    * FIXME Comment this
    * 
    * @return
    * @throws Exception
    */
   @Parameters
   public static Collection<Object[]> testData() throws Exception
   {
      File file = new File("./src/test/resources/variable-expansion.json");
      return loadTestData(file);
   }
   
   /**
    * 
    * Create a new TestVariableExpansion.
    * 
    * @param vars
    * @param expression
    * @param expected
    * @param testsuite
    */
   public TestVariableExpansion(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

}
