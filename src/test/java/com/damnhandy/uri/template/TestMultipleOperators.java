/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A TestMultipleOperators.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestMultipleOperators
{

   private static final Map<String, Object> VALUES;

   static
   {
      VALUES = new LinkedHashMap<String, Object>();
      VALUES.put("group_id", "12345");
      VALUES.put("first_name", "John");
      VALUES.put("item_id", "yu780");
      VALUES.put("page", "5");
      VALUES.put("lang", "en");
      VALUES.put("format", "json");
      VALUES.put("q", "URI Templates");
   }
   
   @Test
   public void testMultiplePathOperators() 
   {
      UriTemplate t = UriTemplate.fromTemplate("/base{/group_id,id}/pages{/page,lang}{?format,q}");
      String uri = t.expand(VALUES);
      Assert.assertEquals("/base/12345/pages/5/en?format=json&q=URI%20Templates", uri);
     
      
   }
}
