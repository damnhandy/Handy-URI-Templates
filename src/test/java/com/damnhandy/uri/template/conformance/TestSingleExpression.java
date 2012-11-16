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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;

/**
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */

public class TestSingleExpression
{

   private static final Map<String, Object> VALUES;

   static
   {
      VALUES = new LinkedHashMap<String, Object>();
      VALUES.put("var", "value");
      VALUES.put("hello", "HelloWorld!");
      VALUES.put("empty", "");
      VALUES.put("path", "/foo/bar");
      VALUES.put("x", "1024");
      VALUES.put("y", "768");
      VALUES.put("id", "person");
      VALUES.put("token", "12345");
      VALUES.put("fields", new String[]{"id", "name", "picture"});
      VALUES.put("format", "json");
      VALUES.put("q", "URI Templates");
      VALUES.put("page", "5");
      VALUES.put("lang", "en");
      VALUES.put("null", null);
      VALUES.put("geocode", new String[]{"37.76", "-122.427"});
      VALUES.put("list", new String[]{"red", "green", "blue"});
      Map<String, Object> keys = new LinkedHashMap<String, Object>();
      keys.put("comma", ",");
      keys.put("dot", ".");
      keys.put("semi", ";");
      VALUES.put("keys", keys);
      VALUES.put("empty_list", new String[]{});
   }

   @Test
   public void testExpression() throws Exception
   {
      UriTemplate template = UriTemplate.fromTemplate("{?empty_list}").set(VALUES);

      String expected = "?empty_list=";//"/comma,%2C,dot,.,semi,%3B";
      String result = template.expand();
      System.out.println(result);
      Assert.assertEquals(expected, result);
   }
}
