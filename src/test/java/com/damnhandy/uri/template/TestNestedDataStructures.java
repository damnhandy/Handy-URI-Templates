package com.damnhandy.uri.template;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Test case covering issue #27
 *
 * https://github.com/damnhandy/Handy-URI-Templates/issues/27
 */
public class TestNestedDataStructures
{

    @Test
    public void testNestedDataStructure() throws Exception
    {
       String result =  UriTemplate.fromTemplate("/test{?queryParams*}")
                                    .set("queryParams", new QueryParams(Lists.newArrayList("a","b","c"), Lists.newArrayList("1","2","3")))
                                    .expand();

        Assert.assertEquals("/test?field=a%2Cb%2Cc&user=1%2C2%2C3", result);
    }



    private class QueryParams {
        List<String> field;
        List<String> user;

        public QueryParams(List<String> field, List<String> user)
        {
            this.field = field;
            this.user = user;
        }

        public List<String> getField()
        {
            return field;
        }

        public void setField(List<String> field)
        {
            this.field = field;
        }

        public List<String> getUser()
        {
            return user;
        }

        public void setUser(List<String> user)
        {
            this.user = user;
        }
    }
}
