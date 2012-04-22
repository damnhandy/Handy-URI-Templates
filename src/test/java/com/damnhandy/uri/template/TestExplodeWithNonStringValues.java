/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A TestExplodeWithNonStringValues.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExplodeWithNonStringValues
{
   private static final String expression = "/{foo:1}/{foo}";

   @Test
   public void testExpandInteger() throws Exception
   {
      String result = UriTemplate.create(expression).set("foo", new Integer(300)).expand();
      System.out.println(result);
      Assert.assertNotNull(result);
   }

   @Test
   public void testExpandDate() throws Exception
   {
      String result = UriTemplate.create(expression).set("foo", new Date()).expand();
      System.out.println(result);
      Assert.assertNotNull(result);
   }
}
