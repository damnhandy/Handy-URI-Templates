/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template.builder;

import com.damnhandy.uri.template.Expression;
import org.junit.Assert;
import org.junit.Test;

import static com.damnhandy.uri.template.UriTemplateBuilder.var;

/**
 * A TestExpressionBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExpressionBuilder
{

    @Test
    public void testExpressionFromString() throws Exception
    {
        Expression e = new Expression("{test:1}", 0);
        Assert.assertEquals("\\Q{test:1}\\E", e.getReplacementPattern());
    }

    @Test
    public void testSimple() throws Exception
    {
        Expression e = Expression.simple(var("var")).build();
        Assert.assertEquals("{var}", e.toString());
    }

    @Test
    public void testSimpleWithExplode() throws Exception
    {
        Expression e = Expression.simple(var("var*")).build();
        Assert.assertEquals("{var*}", e.toString());
    }

    @Test
    public void testReserved() throws Exception
    {
        Expression e = Expression.reserved(var("var")).build();
        Assert.assertEquals("{+var}", e.toString());
    }

    @Test
    public void testFragment() throws Exception
    {
        Expression e = Expression.fragment(var("var")).build();
        Assert.assertEquals("{#var}", e.toString());
    }

    @Test
    public void testLabel() throws Exception
    {
        Expression e = Expression.label(var("var")).build();
        Assert.assertEquals("{.var}", e.toString());
    }

    @Test
    public void testPath() throws Exception
    {
        Expression e = Expression.path(var("var")).build();
        Assert.assertEquals("{/var}", e.toString());
    }

    @Test
    public void testMatrix() throws Exception
    {
        Expression e = Expression.matrix(var("var")).build();
        Assert.assertEquals("{;var}", e.toString());
    }

    @Test
    public void testQuery() throws Exception
    {
        Expression e = Expression.query(var("var")).build();
        Assert.assertEquals("{?var}", e.toString());
    }

    @Test
    public void testContinuation() throws Exception
    {
        Expression e = Expression.continuation(var("var")).build();
        Assert.assertEquals("{&var}", e.toString());
    }

    @Test
    public void testMultipleExpressions() throws Exception
    {
        Expression e = Expression.simple(var("foo", 1), var("bar"), var("thing", true)).build();
        Assert.assertEquals("{foo:1,bar,thing*}", e.toString());
    }

    @Test
    public void testMultipleExpressionsAndLiteralValues() throws Exception
    {
        Expression e = Expression.simple(var("foo", 1), var("bar"), var("thing", true)).build();
        Assert.assertEquals("{foo:1,bar,thing*}", e.toString());
    }

    @Test
    public void testUnderscorVariableName() throws Exception
    {
        Expression e = Expression.query(var("test_var")).build();
        Assert.assertEquals("{?test_var}", e.toString());
    }
}
