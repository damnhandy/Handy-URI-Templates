package com.damnhandy.uri.template;


import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test case covering issue #27
 *
 * https://github.com/damnhandy/Handy-URI-Templates/issues/27
 */
public class TestNestedDataStructures
{

    /**
     * Nested arrays can be supported in composite objects since we can treat them like arrays in
     * @throws Exception
     */
    @Test
    public void testNestedDataStructure() throws Exception
    {
       String result =  UriTemplate.fromTemplate("/test{?queryParams*}")
                                    .set("queryParams", new QueryParams(Arrays.asList("a","b","c"), Arrays.asList("1","2","3")))
                                    .expand();

        Assert.assertEquals("/test?field=a%2Cb%2Cc&user=1%2C2%2C3", result);
    }

    @Test
    public void testNestedDataStructureWithoutExplodeModifier() throws Exception
    {
        String result =  UriTemplate.fromTemplate("/test{?queryParams}")
        .set("queryParams", new QueryParams(Arrays.asList("a","b","c"), Arrays.asList("1","2","3")))
        .expand();

        Assert.assertEquals("/test?queryParams=a%2Cb%2Cc,1%2C2%2C3", result);
    }

    /**
     * N
     *
     * @throws Exception
     */
    @Test(expected = VariableExpansionException.class)
    public void testWithParamsWithMaps() throws Exception
    {
        Map<String, String> values = new HashMap<>();
        values.put("foo", "bar");
        values.put("moo", "foo");



        String result =  UriTemplate.fromTemplate("/test{?queryParams*}")
                                    .set("queryParams", new ParamsWithMaps(values, Arrays.asList("1","2","3")))
                                    .expand();


    }

    /**
     *
     * @throws Exception
     */
    @Test(expected = VariableExpansionException.class)
    public void testCompositeClass() throws Exception
    {
        Address address = new Address();
        address.setState("CA");
        address.setCity("Newport Beach");
        address.setActive(true);
        Person person = new Person("John Worphin", address);

        String result =  UriTemplate.fromTemplate("/test{?person*}")
            .set("person", person)
            .expand();

        System.out.println(result);
    }


    private static class QueryParams {
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

    private static class ParamsWithMaps
    {

        Map<String, String> values;

        List<String> user;

        public ParamsWithMaps(Map<String, String> values, List<String> user)
        {
            this.values = values;
            this.user = user;
        }

        public Map<String, String> getValues()
        {
            return values;
        }

        public void setValues(Map<String, String> values)
        {
            this.values = values;
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

    private static class Person
    {
        String name;

        Address address;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Address getAddress()
        {
            return address;
        }

        public void setAddress(Address address)
        {
            this.address = address;
        }

        public Person(String name, Address address)
        {

            this.name = name;
            this.address = address;
        }
    }
}
