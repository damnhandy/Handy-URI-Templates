/*
 * 
 */
package com.damnhandy.uri.template;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.damnhandy.uri.template.impl.DefaultVarExploder;

/**
 * Simple test to make sure that the properties of an object are correctly 
 * translated to {@link Map} and that the annotations are properly controlling
 * the label names or ensuring that the field is not included in the value list.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestDefaultVarExploder
{

   /**
    * 
    * 
    * @throws Exception
    */
   @Test
   public void testBasic() throws Exception
   {
      Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
      address.setIgnored("This should be ignored");
      VarExploder exploder = new DefaultVarExploder(address);
      Map<String, Object> values = exploder.getNameValuePairs();
      
      assertTrue(values.containsKey("street"));
      assertTrue(values.containsKey("city"));
      assertTrue(values.containsKey("zipcode"));
      assertFalse(values.containsKey("postalCode"));
      assertTrue(values.containsKey("state"));
      assertTrue(values.containsKey("country"));
      assertFalse(values.containsKey("ignored"));

      assertEquals("4 Yawkey Way", values.get("street"));
      assertEquals("Boston", values.get("city"));
      assertEquals("MA", values.get("state"));
      assertEquals("02215-3496", values.get("zipcode"));
      assertEquals("USA", values.get("country"));

   }
   
   
   @Test
   public void testWithSubclass() throws Exception
   {
      ExtendedAddress address = new ExtendedAddress("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
      address.setIgnored("This should be ignored");
      address.setLabel("A label");
      VarExploder exploder = new DefaultVarExploder(address);
      Map<String, Object> values = exploder.getNameValuePairs();
      
      assertTrue(values.containsKey("street"));
      assertTrue(values.containsKey("city"));
      assertTrue(values.containsKey("zipcode"));
      assertFalse(values.containsKey("postalCode"));
      assertTrue(values.containsKey("state"));
      assertTrue(values.containsKey("country"));
      assertTrue(values.containsKey("label"));
      assertFalse(values.containsKey("ignored"));

      assertEquals("4 Yawkey Way", values.get("street"));
      assertEquals("Boston", values.get("city"));
      assertEquals("MA", values.get("state"));
      assertEquals("02215-3496", values.get("zipcode"));
      assertEquals("USA", values.get("country"));
      assertEquals("A label", values.get("label"));

   }
}
