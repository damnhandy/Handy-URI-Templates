/*
 * 
 */
package com.damnhandy.uri.template.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import com.damnhandy.uri.template.UriTransient;
import com.damnhandy.uri.template.VarExploder;
import com.damnhandy.uri.template.VarName;

/**
 * <p>
 * The {@link DefaultVarExploder} is a {@link VarExploder} implementation that takes in a Java object and
 * extracts the properties for use in a URI Template. Given the following URI template template: 
 * </p>
 * <pre>
 * /mapper{?address*}
 * </pre>
 * <p>
 * And this Java object for an address:
 * </p>
 * <pre>
 * Address address = new Address();
 * address.setState("CA");
 * address.setCity("Newport Beach");
 * String result = UriTemplate.create("/mapper{?address*}").set("address", address).expand();
 * </pre>
 * <p>
 * The expanded URI will be:
 * </p>
 * <pre>
 * /mapper?state=CA&city=Newport%20Beach
 * </pre>
 * 
 * <p>
 * The {@link DefaultVarExploder} breaks down the object properties as follows:
 * <ul>
 *  <li>All properties that contain a non-null return value will be included</li>
 *  <li>Getters or fields annotated with {@link UriTransient} will <b>NOT</b> included in the list</li>
 *  <li>By default, the property name is used as the label in the URI. This can be overridden by 
 *      placing the {@link @VarName} annotation on the field or getter method and specifying a name.</li>
 *  <li>Field level annotation take priority of getter annotations</li>
 * </ul>
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class DefaultVarExploder implements VarExploder
{
   /**
    * The original object.
    */
   private Object source;

   /**
    * The objects properties that have been extracted to a {@link Map}
    */
   private Map<String, Object> values = new TreeMap<String, Object>();

   /**
    * 
    * 
    * 
    * @param source the Object to explode
    */
   public DefaultVarExploder(Object source)
   {
      this.source = source;
      this.initValues();
   }

   /**
    * FIXME Comment this
    * 
    * @return
    */
   @Override
   public Map<String, Object> getNameValuePairs()
   {
      return values;
   }

   /**
    * FIXME Comment this
    *
    */
   private void initValues()
   {

      Class<?> c = source.getClass();
      if (c.isAnnotation() || c.isArray() || c.isEnum() || c.isPrimitive())
      {
         throw new IllegalArgumentException("The value must an object");
      }
      BeanInfo beanInfo;
      try
      {
         beanInfo = Introspector.getBeanInfo(c);
      }
      catch (IntrospectionException e)
      {
         throw new VariableExpansionException(e);
      }
      for (PropertyDescriptor p : beanInfo.getPropertyDescriptors())
      {
         Method readMethod = p.getReadMethod();
         if (!readMethod.isAnnotationPresent(UriTransient.class) && !p.getName().equals("class"))
         {
            Object value = getValue(readMethod);
            String name = p.getName();
            if (readMethod.isAnnotationPresent(VarName.class))
            {
               name = readMethod.getAnnotation(VarName.class).value();
            }
            if (value != null)
            {
               values.put(name, value);
            }
         }

      }
      scanFields(c);
   }

   /**
    * Scans the fields on the class or super classes to look for 
    * annotations on the fields.
    * 
    * @param c
    */
   private void scanFields(Class<?> c)
   {
      if (!c.isInterface())
      {
         Field[] fields = c.getDeclaredFields();
         for (Field field : fields)
         {
            String fieldName = field.getName();

            if (values.containsKey(fieldName))
            {
               if (field.isAnnotationPresent(UriTransient.class))
               {
                  values.remove(fieldName);
               }
               else if (field.isAnnotationPresent(VarName.class))
               {
                  String name = field.getAnnotation(VarName.class).value();
                  values.put(name, values.get(fieldName));
                  values.remove(fieldName);
               }
            }
         }
      }
      /*
       * We still need to scan the fields of the super class if its
       * not Object to check for annotations. There might be a better
       * way to do this.
       */
      if (!c.getSuperclass().equals(Object.class))
      {
         scanFields(c.getSuperclass());
      }
   }

   private Object getValue(Method method)
   {
      try
      {
         if (method == null)
         {
            return null;
         }
         return method.invoke(source);
      }
      catch (IllegalArgumentException e)
      {
         throw new VariableExpansionException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new VariableExpansionException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new VariableExpansionException(e);
      }
   }

}
