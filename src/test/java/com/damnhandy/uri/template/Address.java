/*
 * 
 */
package com.damnhandy.uri.template;

/**
 * 
 * A Address POJO.
 */

public class Address
{

   private String street;

   private String city;

   @VarName("zipcode")
   private String postalCode;

   private String state;

   private String country;

   @UriTransient
   private String ignored;

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
    * 
    * Create a new Address.
    *
    */
   public Address()
   {
   }

   public Address(String street, String city, String state, String postalCode, String country)
   {
      this.street = street;
      this.city = city;
      this.state = state;
      this.postalCode = postalCode;
      this.country = country;

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

   @Override
   public String toString()
   {
      return "Address [street=" + street + ", city=" + city + ", postalCode=" + postalCode
            + ", state=" + state + ", country=" + country + ", ignored=" + ignored + "]";
   }
}