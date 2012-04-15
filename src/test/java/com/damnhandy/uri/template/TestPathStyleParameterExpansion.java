/*
 * 
 */
package com.damnhandy.uri.template;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * A TestPathStyleParameterExpansion.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestPathStyleParameterExpansion extends AbstractUriTemplateTest
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
      File file = new File("./src/test/resources/path-style-parameter-expansion.json");
      return loadTestData(file);
   }

   /**
    * 
    * Create a new TestPathStyleParameterExpansion.
    * 
    * @param vars
    * @param template
    * @param expected
    * @param testsuite
    */
   public TestPathStyleParameterExpansion(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

}
