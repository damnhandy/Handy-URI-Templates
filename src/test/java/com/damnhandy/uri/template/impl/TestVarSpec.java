package com.damnhandy.uri.template.impl;

import com.damnhandy.uri.template.UriTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ryan on 11/8/15.
 */
public class TestVarSpec
{

    private static final Map<String, Object> VALUES;

    static
    {
        VALUES = new LinkedHashMap<String, Object>();
        VALUES.put("experiment", new ArrayList<String>()
        {
            {
                add("one");
                add("two");
                add("three");
            }
        });
    }

    @Test
    public void testWithExplodeModifier() throws Exception
    {
        VarSpec varSpec = new VarSpec("experiment*", Modifier.EXPLODE, null);
        Assert.assertEquals("experiment",varSpec.getVariableName());
    }

    @Test
    public void testTemplateWithExplodeModifier() throws  Exception
    {
        String templateString = "http://foo.com{/experiment*}";

        UriTemplate template = UriTemplate.fromTemplate(templateString);
        String result = template.expand(VALUES);
        Assert.assertEquals("http://foo.com/one/two/three",result);
    }
}
