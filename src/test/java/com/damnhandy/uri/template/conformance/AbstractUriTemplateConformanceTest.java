/**
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.damnhandy.uri.template.UriTemplate;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 *
 */
@RunWith(Parameterized.class)
public abstract class AbstractUriTemplateConformanceTest
{

   /**
    * <p>
    * Loads the test data from the JSON file and generated the parameter list.
    * </p>
    * 
    * @param file
    * @return
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   protected static Collection<Object[]> loadTestData(File file) throws Exception
   {
      InputStream in = new FileInputStream(file);
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> testsuites = mapper.readValue(in, new TypeReference<Map<String, Object>>()
      {
      });

      List<Object[]> params = new ArrayList<Object[]>();
      for (Map.Entry<String, Object> entry : testsuites.entrySet())
      {
         String name = entry.getKey();
         Map<String, Object> testsuite = (Map<String, Object>) entry.getValue();
         Map<String, Object> variables = (Map<String, Object>) testsuite.get("variables");
         List<List<String>> testcases = (List<List<String>>) testsuite.get("testcases");

         for (List<String> test : testcases)
         {
            Object[] p = new Object[4];
            p[0] = variables;
            p[1] = test.get(0);
            p[2] = test.get(1);
            p[3] = name;
            params.add(p);
         }
      }
      return params;
   }

   /**
    * The name of the testsuite
    */
   private String testsuite;

   /**
    * The template patter string
    */
   private String template;

   /**
    * The expected result
    */
   private String expected;

   /**
    * The collection of variables to be used on each test
    */
   private Map<String, Object> variables;

   /**
    * @param template
    * @param expected
    */
   public AbstractUriTemplateConformanceTest(Map<String, Object> vars, String template, String expected, String testsuite)
   {
      this.template = template;
      this.expected = expected;
      this.variables = vars;
      this.testsuite = testsuite;
   }

   /**
    * 
    * @throws Exception
    */
   @Test
   public void test() throws Exception
   {
      UriTemplate t = UriTemplate.create(template);
      String actual = t.expand(variables);
      //String msg = testsuite + "->  Template: " + template + " Expected: " + expected + " Actual: " + actual;
      Assert.assertEquals(testsuite + "->  Template: " + template, expected, actual);
   }
}
