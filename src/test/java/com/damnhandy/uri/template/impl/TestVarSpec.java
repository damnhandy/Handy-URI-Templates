package com.damnhandy.uri.template.impl;

import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.UriTemplateBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.damnhandy.uri.template.UriTemplate.buildFromTemplate;
import static com.damnhandy.uri.template.UriTemplateBuilder.var;

/**
 * Created by ryan on 11/8/15.
 */
public class TestVarSpec {

    private static final Map<String, Object> VALUES;

    static {
        VALUES = new LinkedHashMap<String, Object>();
        VALUES.put("experiment", new ArrayList<String>() {
            {
                add("one");
                add("two");
                add("three");
            }
        });
    }

    @Test
    public void testWithExplodeModifier() throws Exception {
        VarSpec varSpec = new VarSpec("experiment", Modifier.EXPLODE, null);
        Assert.assertEquals("experiment", varSpec.getVariableName());
    }

    @Test
    public void testTemplateWithExplodeModifier() throws Exception {
        String templateString = "http://foo.com{/experiment*}";

        UriTemplate template = UriTemplate.fromTemplate(templateString);
        String result = template.expand(VALUES);
        Assert.assertEquals("http://foo.com/one/two/three", result);
    }


    @Test
    public void createExplodedVarSpecFromUriTemplateBuilder() throws Exception {
        //when
        VarSpec varSpec = UriTemplateBuilder.var("experiment", true);

        //then
        Assert.assertEquals("experiment", varSpec.getVariableName());
    }

    @Test
    public void usingAnUnexpandedExplodedVariableInUriTemplateReturnsAURLContainingTheTemplatedVariable() throws Exception {
        //when
        UriTemplate uriTemplate = buildFromTemplate("http://foo.com/").query(var("experiment", true)).build();

        //then
        Assert.assertEquals("http://foo.com/{?experiment*}", uriTemplate.getTemplate());
    }

    @Test
    public void usingAnExpandedExplodedVariableInUriTemplateReturnsAURLContainingTheExpandedVariable() throws Exception {
        //when
        UriTemplate uriTemplate = buildFromTemplate("http://foo.com/").query(var("experiment", true)).build();
        Assert.assertEquals("http://foo.com/{?experiment*}", uriTemplate.getTemplate());
        //then
        Assert.assertEquals("http://foo.com/?experiment=expandedExperiment", uriTemplate.set("experiment", "expandedExperiment").expand());
    }


}
