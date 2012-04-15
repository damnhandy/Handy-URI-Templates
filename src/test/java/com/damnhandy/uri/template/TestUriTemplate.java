/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

/**
 * A TestUriTemplate.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplate
{

   private static final Map<String, Object> vars;

   static
   {
      vars = new HashMap<String, Object>();
      vars.put("var", "value");
      vars.put("hello", "Hello World!");
      vars.put("empty", "");

      List<String> list = new ArrayList<String>();
      list.add("val1");
      list.add("val2");
      list.add("val3");
      vars.put("list", list);
      Map<String, String> keys = new HashMap<String, String>();
      keys.put("key1", "val1");
      keys.put("key2", "val2");
      vars.put("keys", keys);
      vars.put("path", "/foo/bar");
      vars.put("x", "1024");
      vars.put("y", "768");
   }

   /**
    * 
    */
   @Test
   public void testBasicTemplate()
   {
      UriTemplate t = new RFC6570UriTemplate("/{foo:1}{/foo,thing*}{?test1, test2}");
      Map<String, Object> vars = new HashMap<String, Object>();
      vars.put("foo", "one");
      vars.put("test1", "query");
      vars.put("test2", "blah");
      vars.put("thing", "A test");
      String result = t.expand(vars);
      Assert.assertEquals("/o/one/A%20test?test1=query&test2=blah", result);
   }

   @Test
   public void testWithTrailingPath() throws Exception
   {
      String template = "{path}/here";
      String expected = "%2Ffoo%2Fbar/here";
      UriTemplate t = UriTemplate.create(template);
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testSimpleVar() throws Exception
   {
      String template = "{+var}";
      String expected = "value";
      UriTemplate t = UriTemplate.create(template);

      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testHelloWorldWithPlus() throws Exception
   {
      String template = "{+hello}";
      String expected = "Hello%20World!";
      UriTemplate t = UriTemplate.create(template);

      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testWithAssigingDefaultValueNoReplacement() throws Exception
   {
      String template = "{var=default}";
      String expected = "value";
      UriTemplate t = UriTemplate.create(template);
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testWithUsingDefaultValue() throws Exception
   {
      String template = "{var=default}";
      String expected = "default";
      UriTemplate t = UriTemplate.create(template);
      Map<String, Object> vars = new HashMap<String, Object>();
      vars.put("undef", "default");
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testWithArrayValue() throws Exception
   {
      String template = "{list}";
      String expected = "val1,val2,val3";
      UriTemplate t = UriTemplate.create(template);
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testWithArrayValueWithMatrixOp() throws Exception
   {
      String template = "{;list}";
      String expected = ";val1,val2,val3";
      UriTemplate t = UriTemplate.create(template);
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }

   @Test
   public void testWithArrayValueAndExpansion() throws Exception
   {
      String template = "{list+}";
      String expected = "list.val1,list.val2,list.val3";
      UriTemplate t = UriTemplate.create(template);
      String result = t.expand(vars);
      Assert.assertEquals(expected, result);
   }
}
