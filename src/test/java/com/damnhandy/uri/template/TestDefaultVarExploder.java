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

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Simple test to make sure that the properties of an object are correctly
 * translated to {@link Map} and that the annotations are properly controlling
 * the label names or ensuring that the field is not included in the value list.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestDefaultVarExploder
{

    /**
     * @throws Exception
     */
    @Test
    public void testBasic() throws Exception
    {
        Address address = new Address("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setActive(true);
        address.setIgnored("This should be ignored");
        VarExploder exploder = new DefaultVarExploder(address);
        Map<String, Object> values = exploder.getNameValuePairs();

        assertTrue(values.containsKey("street"));
        assertTrue(values.containsKey("city"));
        assertTrue(values.containsKey("zipcode"));
        assertFalse(values.containsKey("postalCode"));
        assertTrue(values.containsKey("state"));
        assertTrue(values.containsKey("country"));
        assertFalse(values.containsKey("ignored"));
        assertTrue(values.containsKey("active"));
        assertEquals("4 Yawkey Way", values.get("street"));
        assertEquals("Boston", values.get("city"));
        assertEquals("MA", values.get("state"));
        assertEquals("02215-3496", values.get("zipcode"));
        assertEquals("USA", values.get("country"));


    }

    /**
     * Test case for https://github.com/damnhandy/Handy-URI-Templates/issues/49
     * @throws Exception
     */
    @Test
    public void testWithEmptyMap() throws Exception
    {
        String result = UriTemplate.fromTemplate("http://example.com/{?keys*}")
                                   .set("keys", Collections.emptyMap())
                                   .expand();
        assertEquals("http://example.com/", result);
    }


    @Test
    public void testWithSubclass() throws Exception
    {
        ExtendedAddress address = new ExtendedAddress("4 Yawkey Way", "Boston", "MA", "02215-3496", "USA");
        address.setIgnored("This should be ignored");
        address.setLabel("A label");
        VarExploder exploder = new DefaultVarExploder(address);
        Map<String, Object> values = exploder.getNameValuePairs();

        assertTrue(values.containsKey("street"));
        assertTrue(values.containsKey("city"));
        assertTrue(values.containsKey("zipcode"));
        assertFalse(values.containsKey("postalCode"));
        assertTrue(values.containsKey("state"));
        assertTrue(values.containsKey("country"));
        assertTrue(values.containsKey("label"));
        assertFalse(values.containsKey("ignored"));

        assertEquals("4 Yawkey Way", values.get("street"));
        assertEquals("Boston", values.get("city"));
        assertEquals("MA", values.get("state"));
        assertEquals("02215-3496", values.get("zipcode"));
        assertEquals("USA", values.get("country"));
        assertEquals("A label", values.get("label"));

    }
}
