/*
 * 
 */
package com.damnhandy.uri.template;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */

public class TestDifferentDataTypes
{
   private static final int[] INT_COUNT = {1, 2, 3};

   private static final Integer[] INTEGER_COUNT = {new Integer(1), new Integer(2), new Integer(3)};

   private static final long[] LONG_COUNT = {1l, 2l, 3l};

   private static final float[] FLOAT_COUNT = {1.01f, 2.02f, 3.03f};

   private static final double[] DOUBLE_COUNT = {1.02d, 2.04d, 3.05d};

   private static final String TEMPLATE_1 = "{count}";
   
   private static final String TEMPLATE_2 = "{count*}";

   @Test
   public void testTypes() throws Exception
   {

      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_1).set("count", INT_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_1).set("count", INTEGER_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_1).set("count", LONG_COUNT).expand());
      assertEquals("1.01,2.02,3.03", UriTemplate.create(TEMPLATE_1).set("count", FLOAT_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_1).set("count", INT_COUNT).expand());
      assertEquals("1.02,2.04,3.05", UriTemplate.create(TEMPLATE_1).set("count", DOUBLE_COUNT).expand());
      
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_2).set("count", INT_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_2).set("count", INTEGER_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_2).set("count", LONG_COUNT).expand());
      assertEquals("1.01,2.02,3.03", UriTemplate.create(TEMPLATE_2).set("count", FLOAT_COUNT).expand());
      assertEquals("1,2,3", UriTemplate.create(TEMPLATE_2).set("count", INT_COUNT).expand());
      assertEquals("1.02,2.04,3.05", UriTemplate.create(TEMPLATE_2).set("count", DOUBLE_COUNT).expand());
   }
}
