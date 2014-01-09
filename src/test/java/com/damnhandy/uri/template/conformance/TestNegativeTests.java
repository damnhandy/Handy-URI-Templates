/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template.conformance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.VariableExpansionException;

/**
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestNegativeTests extends AbstractUriTemplateConformanceTest
{
   @Parameters(name = "{1}")
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
      catch (VariableExpansionException e)
      {
         pass = false;
      }
      catch (MalformedUriTemplateException e)
      {
         pass = false;
      }
      Assert.assertFalse("Expected "+template+" to fail but got "+actual,pass);
   }
}
