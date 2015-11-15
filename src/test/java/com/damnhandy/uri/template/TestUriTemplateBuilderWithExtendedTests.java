package com.damnhandy.uri.template;

import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * Created by ryan on 11/14/15.
 */
public class TestUriTemplateBuilderWithExtendedTests extends AbstractUriTemplateBuilderTests
{

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> testData() throws Exception
    {
        File file = new File("./uritemplate-test/extended-tests.json");
        return loadTestData(file);
    }

    /**
     * @param vars
     * @param template
     * @param expected
     * @param testsuite
     */
    public TestUriTemplateBuilderWithExtendedTests(Map<String, Object> vars, String template, Object expected, String testsuite)
    {
        super(vars, template, expected, testsuite);
    }
}
