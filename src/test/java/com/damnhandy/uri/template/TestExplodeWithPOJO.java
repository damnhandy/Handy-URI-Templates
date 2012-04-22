/*
 * 
 */
package com.damnhandy.uri.template;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A TestExplodeWithPOJO.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExplodeWithPOJO
{

   private static final String EXPLODE_TEMPLATE = "/mapper{?address*}";

   private static final String NON_EXPLODE_TEMPLATE = "/mapper{?address}";

   @Test
   public void testExplodeAddress() throws Exception
   {
      Address address = new Address();
      address.setState("CA");
      address.setCity("Newport Beach");
      String result = UriTemplate.create(EXPLODE_TEMPLATE).set("address", address).expand();
      
      Assert.assertEquals("/mapper?state=CA&city=Newport%20Beach", result);
   }
   
   @Test
   public void testExplodeAddressWithNoExplodeOperator() throws Exception
   {
      Address address = new Address();
      address.setState("CA");
      address.setCity("Newport Beach");
      String result = UriTemplate.create(NON_EXPLODE_TEMPLATE).set("address", address).expand();
      System.out.println(result);
      Assert.assertNotSame("/mapper?state=CA&city=Newport%20Beach", result);
   }

   /**
    * FIXME Comment this
    * 
    * @throws Exception
    */
   @Test
   public void testSimpleAddress() throws Exception
   {
      Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
      String result = UriTemplate.create(EXPLODE_TEMPLATE).set("address", address).expand();
      System.out.println(result);
      Assert.assertEquals("/mapper?zipcode=02215-3496&street=4%20Yawkey%20Way&state=MA&country=USA&city=Boston", result);
   }
}
