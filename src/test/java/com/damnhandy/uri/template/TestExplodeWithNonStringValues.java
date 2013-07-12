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



import org.junit.Assert;
import org.junit.Test;

/**
 * A TestExplodeWithNonStringValues.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExplodeWithNonStringValues
{
   private static final String expression = "/{foo:1}/{foo}";

   @Test
   public void testExpandInteger() throws Exception
   {
      String result = UriTemplate.fromTemplate(expression).set("foo", new Integer(300)).expand();
      Assert.assertEquals("/3/300", result);
   }
}
