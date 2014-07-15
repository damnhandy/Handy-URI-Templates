/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;

/**
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
@Ignore
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

      String expected = "";
      String result = template.expand();
      Assert.assertEquals(expected, result);

      String r2 = com.damnhandy.uri.template.UriTemplate.expand("", new java.util.HashMap<String, Object>() {
          @Override
          public Object put(String key, Object value) {
              return super.put(key, value);
          }
      });

      //Pattern p = Pattern.compile("([?&](fields)(?:=([^&]*))?)([?&](first_name)(?:=([^&]*))?)([?&](last.name)(?:=([^&]*))?)([?&](token)(?:=([^&]*))?)");
   }
   
  
}
