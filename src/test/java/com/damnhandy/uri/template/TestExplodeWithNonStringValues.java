/*
 * 
 */
package com.damnhandy.uri.template;

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
      String result = UriTemplate.fromExpression(expression).set("foo", new Integer(300)).expand();
      Assert.assertEquals("/3/300", result);
   }
}
