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

import static com.damnhandy.uri.template.Expression.*;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A TestUriTemplateBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateBuilder
{

   @Test
   public void testCreateBasicTemplate() throws Exception
   {
      UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
             .appendLiteral("/foo")
             .append(path().var("thing1").var("explodedThing", true).build())
             .append(fragment().var("prefix", 2).build()).build();

      Assert.assertEquals("http://example.com/foo{/thing1,explodedThing*}{#prefix:2}", template.getTemplate());
   }
}
