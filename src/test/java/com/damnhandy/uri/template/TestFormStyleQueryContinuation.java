/*
 * 
 */
package com.damnhandy.uri.template;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * A TestFormStyleQueryContinuation.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestFormStyleQueryContinuation extends AbstractUriTemplateTest
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
      File file = new File("./src/test/resources/form-style-query-continuation.json");
      return loadTestData(file);
   }

   /**
    * 
    * Create a new TestFormStyleQueryContinuation.
    * 
    * @param vars
    * @param template
    * @param expected
    * @param testsuite
    */
   public TestFormStyleQueryContinuation(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      super(vars, template, expected, testsuite);
   }

}
