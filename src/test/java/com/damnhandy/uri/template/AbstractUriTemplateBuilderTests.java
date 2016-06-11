package com.damnhandy.uri.template;

import com.damnhandy.uri.template.conformance.AbstractUriTemplateConformanceTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import java.util.Map;

/**
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * Created by ryan on 11/14/15.
 */
 @Ignore
public class AbstractUriTemplateBuilderTests extends AbstractUriTemplateConformanceTest
{

    /**
     * @param vars
     * @param template
     * @param expected
     * @param testsuite
     */
    public AbstractUriTemplateBuilderTests(Map<String, Object> vars, String template, Object expected, String testsuite)
    {
        super(vars, template, expected, testsuite);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception
    {
        UriTemplate original = UriTemplate.fromTemplate(template);
        UriTemplateBuilder originalBuilder = UriTemplate.buildFromTemplate(original);
        UriTemplateComponent[] components = originalBuilder.getComponents();

        UriTemplateBuilder builder = UriTemplate.createBuilder();
        for(UriTemplateComponent c : components)
        {
            builder.addComponent(c);
        }
        UriTemplate rebuiltTemplate = builder.build();

        Assert.assertEquals(original.getTemplate(), rebuiltTemplate.getTemplate());
    }
}
