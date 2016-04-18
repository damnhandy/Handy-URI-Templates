package com.damnhandy.uri.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 4/18/16.
 */
public class ExtendedAddressWithNestedLists extends Address
{
    private List<String> stuff = new ArrayList<String>();


    public ExtendedAddressWithNestedLists()
    {
        super();
    }

    /**
     * Create a new ExtendedAddress.
     *
     * @param street
     * @param city
     * @param state
     * @param postalCode
     * @param country
     */
    public ExtendedAddressWithNestedLists(String street, String city, String state, String postalCode, String country)
    {
        super(street, city, state, postalCode, country);
    }


    public List<String> getStuff()
    {
        return stuff;
    }

    public void setStuff(List<String> stuff)
    {
        this.stuff = stuff;
    }

    public void addStuff(String thing)
    {
        this.getStuff().add(thing);
    }
}
