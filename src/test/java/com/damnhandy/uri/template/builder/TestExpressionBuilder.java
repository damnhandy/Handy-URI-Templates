/*
 *
 *
 */
package com.damnhandy.uri.template.builder;

import junit.framework.Assert;

import org.junit.Test;

import com.damnhandy.uri.template.Expression;

/**
 * A TestExpressionBuilder.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExpressionBuilder
{

   @Test
   public void testSimple() throws Exception
   {
      Expression e = Expression.simple().var("var").build();
      Assert.assertEquals("{var}", e.toString());
   }

   @Test
   public void testReserved() throws Exception
   {
      Expression e = Expression.reserved().var("var").build();
      Assert.assertEquals("{+var}", e.toString());
   }

   @Test
   public void testFragment() throws Exception
   {
      Expression e = Expression.fragment().var("var").build();
      Assert.assertEquals("{#var}", e.toString());
   }

   @Test
   public void testLabel() throws Exception
   {
      Expression e = Expression.label().var("var").build();
      Assert.assertEquals("{.var}", e.toString());
   }

   @Test
   public void testPath() throws Exception
   {
      Expression e = Expression.path().var("var").build();
      Assert.assertEquals("{/var}", e.toString());
   }

   @Test
   public void testMatrix() throws Exception
   {
      Expression e = Expression.matrix().var("var").build();
      Assert.assertEquals("{;var}", e.toString());
   }

   @Test
   public void testQuery() throws Exception
   {
      Expression e = Expression.query().var("var").build();
      Assert.assertEquals("{?var}", e.toString());
   }

   @Test
   public void testContinuation() throws Exception
   {
      Expression e = Expression.continuation().var("var").build();
      Assert.assertEquals("{&var}", e.toString());
   }

   @Test
   public void testSimple2() throws Exception
   {
      Expression e = Expression.simple().var("foo", 1).var("foo").var("thing", true).build();
      Assert.assertEquals("{foo:1,foo,thing*}", e.toString());
   }
}
