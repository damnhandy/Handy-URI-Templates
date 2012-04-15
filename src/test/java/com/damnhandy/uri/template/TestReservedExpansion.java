/*
 * 
 */
package com.damnhandy.uri.template;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

/**
 * A TestReservedExpansion.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestReservedExpansion extends AbstractUriTemplateTest
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
      File file = new File("./src/test/resources/reserved-expansion.json");
      return loadTestData(file);
   }

   /**
    * 
    * Create a new TestReservedExpansion.
    * 
    * @param vars
    * @param template
    * @param expected
    * @param testsuite
    */
   public TestReservedExpansion(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      // FIXME TestReservedExpansion constructor
      super(vars, template, expected, testsuite);
   }

}
