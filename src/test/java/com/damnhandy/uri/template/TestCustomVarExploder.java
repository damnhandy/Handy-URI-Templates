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
package com.damnhandy.uri.template;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests a custom {@link VarExploder} that is used to expand a JSON object into
 * a template variable.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestCustomVarExploder
{

   /**
    * Normal test case that correctly uses the explode modifier.
    */
   private static final String EXPLODE_TEMPLATE = "/mapper{?address*}";

   /**
    * Tests a negative test case whereby the variable does not specify
    * the explode variable.
    */
   private static final String BAD_EXPLODE_TEMPLATE = "/mapper{?address}";

   /**
    * The JSON String
    */
   private static final String JSON = "{\"city\": \"Boston\", " +
   		                               "\"country\": \"USA\", " +
   		                               "\"state\": \"MA\", " +
   		                               "\"street\": \"4 Yawkey Way\", " +
   		                               "\"zipcode\": \"02215-3496\" }";
   /**
    * Positve test case.
    *
    */
   @Test
   public void testWrappedExploder() throws Exception
   {
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("address", new JsonVarExploder(JSON));
      String result = UriTemplate.expand(EXPLODE_TEMPLATE, values);
      Assert.assertEquals("/mapper?city=Boston&country=USA&state=MA&street=4%20Yawkey%20Way&zipcode=02215-3496", result);

   }

   /**
    *
    *
    */
   @Test
   public void testWrappedExploderWithInvalidTemplate() throws Exception
   {
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("address", new JsonVarExploder(JSON));
      String result = UriTemplate.expand(BAD_EXPLODE_TEMPLATE, values);
      Assert.assertEquals("/mapper?address=Boston,USA,MA,4%20Yawkey%20Way,02215-3496", result);

   }
}
