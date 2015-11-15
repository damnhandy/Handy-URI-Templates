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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestExpandPartial {

   private static final String TEMPLATE = "https://foo.com:8080/things{/thingId,thingSubId}{?param1,param2}{&param3,param4}{#anchor1,anchor2}";

   @Test
   public void shouldLeftExpressionsWithoutValues()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> of();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals(TEMPLATE, result);
   }

   @Test
   public void shouldReplaceAllValues()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> builder()
         .put("thingId", 123)
         .put("thingSubId", 456)
         .put("param1", "value1")
         .put("param2", "value2")
         .put("param3", "value3")
         .put("param4", "value4")
         .put("anchor1", "value5")
         .put("anchor2", "value6")
         .build();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals("https://foo.com:8080/things/123/456?param1=value1&param2=value2&param3=value3&param4=value4#value5,value6", result);
   }

   @Test
   public void shouldReplaceExpressionWithValues()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> builder()
         .put("thingId", 123)
         .put("thingSubId", 456)
         .put("anchor1", "value5")
         .put("anchor2", "value6")
         .build();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals("https://foo.com:8080/things/123/456{?param1,param2}{&param3,param4}#value5,value6", result);
   }

   @Test
   public void shouldReplacePartialExpressionVariables()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> builder()
         .put("thingId", 123)
         .put("param4", "value4")
         .build();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals("https://foo.com:8080/things/123{/thingSubId}{?param1,param2}{&param3}&param4=value4{#anchor1,anchor2}", result);
   }

   @Test
   public void shouldChangeOperatorToSeperatorIfFirstVariableForExpressionIsProvided()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> builder()
         .put("thingId", 123)
         .put("param1", "value1")
         .put("param3", "value3")
         .put("anchor1", "value5")
         .build();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals("https://foo.com:8080/things/123{/thingSubId}?param1=value1{&param2}&param3=value3{&param4}#value5{,anchor2}", result);
   }

   @Test
   public void shouldUseSeperatorIfOnlySecondVariableForExpressionIsProvided()
   {
      Map<String, Object> values = ImmutableMap.<String, Object> builder()
         .put("thingSubId", 456)
         .put("param2", "value2")
         .put("param4", "value4")
         .put("anchor2", "value6")
         .build();
      String result = UriTemplate.expandPartial(TEMPLATE, values);
      Assert.assertEquals("https://foo.com:8080/things{/thingId}/456?param2=value2{&param1}{&param3}&param4=value4{#anchor1},value6", result);
   }

   @Test
   public void shouldReplaceReservedExpansions() {
     String template = "/base/{+path1,path2}{a*,b*}";
     Map<String, Object> values = ImmutableMap.<String, Object> of("path1", "abc/def", "path2", "/ghi/jkl");
     String partiallyExpanded = UriTemplate.expandPartial(template, values);
     Assert.assertEquals("/base/abc/def/ghi/jkl{a*,b*}", partiallyExpanded);
   }

   @Test
   public void shouldExpandNullVariablesBeforeValuedVariablesForFormStyleQuery() 
   {
      String template = "{?var1,var2}";
      Map<String, Object> values = ImmutableMap.<String, Object> of("var2", "value-of-var2");
      String partiallyExpanded = UriTemplate.expandPartial(template, values);
      Assert.assertEquals("?var2=value-of-var2{&var1}", partiallyExpanded);

   }

}
