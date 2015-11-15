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
package com.damnhandy.uri.template.conformance;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 */
@RunWith(Parameterized.class)
public abstract class AbstractUriTemplateConformanceTest
{

    /**
     * <p>
     * Loads the test data from the JSON file and generated the parameter list.
     * </p>
     *
     * @param file
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected static Collection<Object[]> loadTestData(File file) throws Exception
    {
        InputStream in = new FileInputStream(file);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> testsuites = mapper.readValue(in, new TypeReference<Map<String, Object>>()
            {
            });

            List<Object[]> params = new ArrayList<Object[]>();
            for (Map.Entry<String, Object> entry : testsuites.entrySet())
            {
                String name = entry.getKey();
                Map<String, Object> testsuite = (Map<String, Object>) entry.getValue();
                Map<String, Object> variables = (Map<String, Object>) testsuite.get("variables");
                List<List<Object>> testcases = (List<List<Object>>) testsuite.get("testcases");

                for (List<Object> test : testcases)
                {
                    Object[] p = new Object[4];
                    p[0] = variables;
                    p[1] = test.get(0); // expression
                    p[2] = test.get(1); // expected result
                    p[3] = name;        // test suite label
                    params.add(p);
                }
            }
            return params;
        }
        finally
        {
            in.close();
        }


    }

    /**
     * The name of the testsuite
     */
    protected String testsuite;

    /**
     * The expression patter string
     */
    protected String template;

    /**
     * The expected result
     */
    protected Object expected;

    /**
     * The collection of variables to be used on each test
     */
    protected Map<String, Object> variables;

    /**
     * @param vars
     * @param template
     * @param expected
     * @param testsuite
     */
    public AbstractUriTemplateConformanceTest(Map<String, Object> vars, String template, Object expected, String testsuite)
    {
        this.template = template;
        this.expected = expected;
        this.variables = vars;
        this.testsuite = testsuite;
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception
    {
        UriTemplate t = UriTemplate.fromTemplate(template);
        String actual;
        try
        {
            actual = t.expand(variables);
            if (expected instanceof String)
            {
                Assert.assertEquals(testsuite + "->  Template: " + template, expected, actual);
            }
            else if (expected instanceof Collection)
            {
                List<String> combinations = (List<String>) expected;
                boolean match = false;
                for (String combo : combinations)
                {
                    if (combo.equalsIgnoreCase(actual))
                    {
                        match = true;
                        break;
                    }
                }
                Assert.assertTrue(testsuite + "->  Template: " + template + " returned " + actual + " and did not match any combination", match);
            }
        }
        catch (Exception e)
        {
            Assert.fail(testsuite + "->  Template: " + template + " returned " + e.getMessage() + " and did not match any combination");
        }

    }
}
