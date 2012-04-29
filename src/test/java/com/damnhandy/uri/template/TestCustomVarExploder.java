/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A TestCustomVarExploder.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestCustomVarExploder
{

   private static final String EXPLODE_TEMPLATE = "/mapper{?address*}";
   
   private static final String JSON = "{\"city\": \"Boston\", " +
   		                               "\"country\": \"USA\", " +
   		                               "\"state\": \"MA\", " +
   		                               "\"street\": \"4 Yawkey Way\", " +
   		                               "\"zipcode\": \"02215-3496\" }";

   @Test
   public void testWrappedExploder()
   {
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("address", new JsonVarExploder(JSON));
      String result = UriTemplate.expand(EXPLODE_TEMPLATE, values);
      Assert.assertEquals("/mapper?city=Boston&country=USA&state=MA&street=4%20Yawkey%20Way&zipcode=02215-3496", result);
      
   }
}
