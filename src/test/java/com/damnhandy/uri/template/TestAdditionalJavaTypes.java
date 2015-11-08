package com.damnhandy.uri.template;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ryan on 11/6/15.
 */
public class TestAdditionalJavaTypes
{

    public static enum Things
    {
        BOX, CARTON;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUUID() throws Exception
    {

        String uri = UriTemplate.fromTemplate("{/id}")
              .set("id", UUID.fromString("b462a6d9-789d-4a21-ab0d-b099835d76f5"))
              .expand();
        Assert.assertEquals("/b462a6d9-789d-4a21-ab0d-b099835d76f5", uri);
    }


    @Test
    public void testEnum() throws Exception
    {
        System.out.println("Enum value " + Things.BOX.toString());

        String uri = UriTemplate.fromTemplate("/stuff{?enum}")
              .set("enum", Things.BOX)
              .expand();
        System.out.println(uri);
    }
}


