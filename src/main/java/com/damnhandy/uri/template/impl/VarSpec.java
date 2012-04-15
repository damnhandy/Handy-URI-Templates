/*
 * 
 */
package com.damnhandy.uri.template.impl;

/**
 * A VarSpec.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class VarSpec
{
   /**
    * 
    */
   private String modifier;

   /**
    * 
    */
   private String value;

   /**
    * 
    */
   private Integer position = null;

   /**
    * 
    */
   private String key;

   /**
    * 
    */
   private String defaultValue;

   /**
    * Create a new VarSpec.
    * 
    * @param modifier
    * @param value
    */
   public VarSpec(String value, String modifier)
   {
      this(value, modifier, null);
   }

   /**
    * Create a new VarSpec.
    * 
    * @param modifier
    * @param value
    * @param position
    */
   public VarSpec(String value, String modifier, Integer position)
   {
      this.modifier = modifier;
      this.value = value;
      this.position = position;
      setKey();
   }

   /**
    * Get the modifier.
    * 
    * @return the modifier.
    */
   public String getModifier()
   {
      return modifier;
   }

   /**
    * Get the value.
    * 
    * @return the value.
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Get the position.
    * 
    * @return the position.
    */
   public Integer getPosition()
   {
      return position;
   }

   private void setKey()
   {
      if (modifier != null)
      {
         if (modifier.equals(":"))
         {
            String[] values = getValue().split(":");
            key = values[0];
         }
         else if (modifier.equals("*") || modifier.equals("+"))
         {
            key = getValue().substring(0, getValue().length() - 1);
         }
      }
      else
      {
         key = getValue();
      }
   }

   /**
    * FIXME Comment this
    * 
    * @return
    */
   public String getKey()
   {
      if (key == null)
      {
         return getValue();
      }
      return key;
   }

   /**
    * FIXME Comment this
    * 
    * @return
    */
   public boolean hasDefaultValue()
   {
      return (defaultValue != null);
   }

   /**
    * Get the defaultValue.
    * 
    * @return the defaultValue.
    */
   public String getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * Set the defaultValue.
    * 
    * @param defaultValue The defaultValue to set.
    */
   public void setDefaultValue(String defaultValue)
   {
      this.defaultValue = defaultValue;
   }

   @Override
   public String toString()
   {
      return "VarSpec [modifier=" + modifier + ", value=" + value + ", position=" + position + ", key=" + key + "]";
   }

}
