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
package com.damnhandy.uri.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestExplodeWithPOJO
{

    private static final String EXPLODE_TEMPLATE = "/mapper{?address*}";

    private static final String NON_EXPLODE_TEMPLATE = "/mapper{?address}";

    @Test
    public void testExplodeAddress() throws Exception
    {
        Address address = new Address();
        address.setState("CA");
        address.setCity("Newport Beach");
        address.setActive(true);
        String result = UriTemplate.fromTemplate(EXPLODE_TEMPLATE).set("address", address).expand();

        Assert.assertEquals("/mapper?active=true&city=Newport%20Beach&state=CA", result);
    }

    @Test
    public void testExplodeAddressWithNoExplodeOperator() throws Exception
    {
        Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setActive(true);
        String result = UriTemplate.fromTemplate(NON_EXPLODE_TEMPLATE).set("address", address).expand();
        Assert.assertEquals("/mapper?address=true,Boston,USA,MA,4%20Yawkey%20Way,02215-3496", result);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSimpleAddress() throws Exception
    {
        Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setActive(true);
        String result = UriTemplate.fromTemplate(EXPLODE_TEMPLATE).set("address", address).expand();
        Assert.assertEquals("/mapper?active=true&city=Boston&country=USA&state=MA&street=4%20Yawkey%20Way&zipcode=02215-3496", result);
    }

    @Test
    public void testExplodeWithSubclass() throws Exception
    {
        ExtendedAddress address = new ExtendedAddress("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setIgnored("This should be ignored");
        address.setLabel("A label");
        String result = UriTemplate.fromTemplate(EXPLODE_TEMPLATE).set("address", address).expand();

        Assert.assertEquals(
        "/mapper?active=false&city=Boston&country=USA&label=A%20label&state=MA&street=4%20Yawkey%20Way&zipcode=02215-3496",
        result);
    }

    @Test
    public void testExplodeWithSubclassWithNestedList() throws Exception
    {
        ExtendedAddressWithNestedLists address = new ExtendedAddressWithNestedLists("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setIgnored("This should be ignored");
        address.addStuff("foo");
        address.addStuff("bar");
        address.addStuff("moo");
        String result = UriTemplate.fromTemplate(EXPLODE_TEMPLATE).set("address", address).expand();

        Assert.assertEquals(
        "/mapper?active=false&city=Boston&country=USA&state=MA&street=4%20Yawkey%20Way&stuff=foo%2Cbar%2Cmoo&zipcode=02215-3496",
        result);
    }

    @Test
    public void testWrappedExploder() throws Exception
    {
        Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("address", new DefaultVarExploder(address));
        String result = UriTemplate.expand(EXPLODE_TEMPLATE, values);
        Assert.assertEquals("/mapper?active=false&city=Boston&country=USA&state=MA&street=4%20Yawkey%20Way&zipcode=02215-3496", result);

    }
}
