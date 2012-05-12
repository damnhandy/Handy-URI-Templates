/*
 * 
 */
package com.damnhandy.uri.template.conformance;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

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
      VALUES = new HashMap<String, Object>();
      VALUES.put("var", "value");
      VALUES.put("hello", "HelloWorld!");
      VALUES.put("empty", "");
      VALUES.put("path", "/foo/bar");
      VALUES.put("x", "1024");
      VALUES.put("y", "768");
      VALUES.put("list", new String[] {"red", "green", "blue"});
      Map<String, Object> keys = new HashMap<String, Object>();
      keys.put("comma", ",");
      keys.put("dot", ".");
      keys.put("semi", ";"); 
      VALUES.put("keys", keys);
   }
   
   @Test
   public void testExpression() throws Exception {
      UriTemplate template = UriTemplate.fromExpression("{keys}").set(VALUES);
      
      URI expected = new URI("comma,%2C,dot,.,semi,%3B");
      URI result = new URI(template.expand());
 
      Assert.assertEquals(expected, result);
   }
}
