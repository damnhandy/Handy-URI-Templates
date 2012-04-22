/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * A TestFragmentExpansion.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestFragmentExpansion extends AbstractUriTemplateConformanceTest
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
      File file = new File("./src/test/resources/fragment-expansion.json");
      return loadTestData(file);
   }

   /**
    * 
    * Create a new TestFragmentExpansion.
    * 
    * @param vars
    * @param template
    * @param expected
    * @param testsuite
    */
   public TestFragmentExpansion(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      // FIXME TestFragmentExpansion constructor
      super(vars, template, expected, testsuite);
   }

}
