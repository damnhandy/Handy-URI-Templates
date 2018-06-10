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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * <p>
 * The {@link DefaultVarExploder} is a {@link VarExploder} implementation that takes in a Java object and
 * extracts the properties for use in a URI Template. Given the following URI expression:
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
 * String result = UriTemplate.fromTemplate("/mapper{?address*}").set("address", address).expand();
 * </pre>
 * <p>
 * The expanded URI will be:
 * </p>
 * <pre>
 * /mapper?city=Newport%20Beach&amp;state=CA
 * </pre>
 * <p>
 * <p>
 * The {@link DefaultVarExploder} breaks down the object properties as follows:
 * <ul>
 * <li>All properties that contain a non-null return value will be included</li>
 * <li>Getters or fields annotated with {@link UriTransient} will <b>NOT</b> included in the list</li>
 * <li>By default, the property name is used as the label in the URI. This can be overridden by
 * placing the {@link VarName} annotation on the field or getter method and specifying a name.</li>
 * <li>Field level annotation take priority of getter annotations</li>
 * </ul>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @see VarName
 * @see UriTransient
 * @see VarExploder
 * @since 1.0
 */
public class DefaultVarExploder implements VarExploder
{

    /**
     *
     */
    private static final String GET_PREFIX = "get";

    /**
     *
     */
    private static final String IS_PREIX = "is";


    /**
     * The original object.
     */
    private Object source;

    /**
     * The objects properties that have been extracted to a {@link Map}
     */
    private Map<String, Object> pairs = new TreeMap<String, Object>();

    /**
     * @param source the Object to explode
     */
    public DefaultVarExploder(Object source) throws VarExploderException
    {
        this.setSource(source);
    }

    /**
     * @return the name value pairs of the input
     */
    @Override
    public Map<String, Object> getNameValuePairs()
    {
        return pairs;
    }


    /**
     *
     * @param source
     * @throws VarExploderException
     */
    void setSource(Object source) throws VarExploderException
    {
        this.source = source;
        this.initValues();
    }

    /**
     * Initializes the values from the object properties and constructs a
     * map from those values.
     *
     * @throws VarExploderException
     */
    private void initValues() throws VarExploderException
    {

        Class<?> c = source.getClass();
        if (c.isAnnotation() || c.isArray() || c.isEnum() || c.isPrimitive())
        {
            throw new IllegalArgumentException("The value must an object");
        }

        if(source instanceof Map )
        {
            this.pairs = (Map<String,Object>) source;
            return;
        }
        Method[] methods = c.getMethods();
        for (Method method : methods)
        {
            inspectGetters(method);
        }
        scanFields(c);
    }

    /**
     * A lite version of the introspection logic performed by the BeanInfo introspector.
     * @param method
     */
    private void inspectGetters(Method method)
    {
        String methodName = method.getName();
        int prefixLength = 0;
        if (methodName.startsWith(GET_PREFIX)) {
            prefixLength = GET_PREFIX.length();
        }

        if (methodName.startsWith(IS_PREIX)) {
            prefixLength = IS_PREIX.length();
        }
        if(prefixLength == 0)
        {
            return;
        }

        String name = decapitalize(methodName.substring(prefixLength));
        if(!isValidProperty(name))
        {
            return;
        }

        // Check that the return type is not null or void
        Class propertyType = method.getReturnType();

        if (propertyType == null || propertyType == void.class)
        {
            return;
        }

        // isXXX return boolean
        if (prefixLength == 2)
        {
            if (!(propertyType == boolean.class))
            {
                return;
            }
        }

        // validate parameter types
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 1 ||
           (paramTypes.length == 1 && paramTypes[0] != int.class))
        {
            return;
        }

        if (!method.isAnnotationPresent(UriTransient.class) && !"class".equals(name))
        {
            Object value = getValue(method);

            if (method.isAnnotationPresent(VarName.class))
            {
                name = method.getAnnotation(VarName.class).value();
            }
            if (value != null)
            {
                pairs.put(name, value);
            }
        }
    }

    private static boolean isValidProperty(String propertyName) {
        return (propertyName != null) && (propertyName.length() != 0);
    }

    static String decapitalize(String name)
    {

        if (name == null)
            return null;
        // The rule for decapitalize is that:
        // If the first letter of the string is Upper Case, make it lower case
        // UNLESS the second letter of the string is also Upper Case, in which case no
        // changes are made.
        if (name.length() == 0 || (name.length() > 1 && Character.isUpperCase(name.charAt(1)))) {
            return name;
        }

        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Scans the fields on the class or super classes to look for
     * field-level annotations.
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

                if (pairs.containsKey(fieldName))
                {
                    if (field.isAnnotationPresent(UriTransient.class))
                    {
                        pairs.remove(fieldName);
                    }
                    else if (field.isAnnotationPresent(VarName.class))
                    {
                        String name = field.getAnnotation(VarName.class).value();
                        pairs.put(name, pairs.get(fieldName));
                        pairs.remove(fieldName);
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

    /**
     * Return the value of the property.
     *
     * @param method
     * @return
     * @throws VarExploderException
     */
    private Object getValue(Method method) throws VarExploderException
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
            throw new VarExploderException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new VarExploderException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new VarExploderException(e);
        }
    }

    @Override
    public Collection<Object> getValues() throws VarExploderException
    {
        return pairs.values();
    }

}
