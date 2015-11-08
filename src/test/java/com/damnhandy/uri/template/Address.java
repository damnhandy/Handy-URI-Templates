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

/**
 * A Address POJO.
 */
public class Address
{

    private String street;

    private String city;

    private String state;

    @VarName("zipcode")
    private String postalCode;

    private String country;

    @UriTransient
    private String ignored;

    private boolean active;


    public Address(String street, String city, String state, String postalCode, String country)
    {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;

    }

    /**
     * Create a new Address.
     */
    public Address()
    {
    }

    /**
     * Get the street.
     *
     * @return the street.
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * Set the street.
     *
     * @param street The street to set.
     */
    public void setStreet(String street)
    {
        this.street = street;
    }

    /**
     * Get the city.
     *
     * @return the city.
     */
    public String getCity()
    {
        return city;
    }

    /**
     * Set the city.
     *
     * @param city The city to set.
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Get the postalCode.
     *
     * @return the postalCode.
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * Set the postalCode.
     *
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * Get the country.
     *
     * @return the country.
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * Set the country.
     *
     * @param country The country to set.
     */
    public void setCountry(String country)
    {
        this.country = country;
    }


    /**
     * Get the ignored.
     *
     * @return the ignored.
     */
    public String getIgnored()
    {
        return ignored;
    }

    /**
     * Set the ignored.
     *
     * @param ignored The ignored to set.
     */
    public void setIgnored(String ignored)
    {
        this.ignored = ignored;
    }

    /**
     * Returns true if the address is active
     * @return
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Sets the active status
     * @param active
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
