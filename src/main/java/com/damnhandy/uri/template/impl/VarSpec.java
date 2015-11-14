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
package com.damnhandy.uri.template.impl;

import java.io.Serializable;


/**
 * Represents a variable in a URI template expression.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class VarSpec implements Serializable
{
    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = 5850478145190940514L;

    public static enum VarFormat
    {
        SINGLE, ARRAY, PAIRS;
    }

    private static final String BASE_PATTERN = "([\\w.~\\-\\_]|%[A-Fa-f0-9]{2})";
    /**
     *
     */
    private Modifier modifier = Modifier.NONE;

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
    private String variableName;

    /**
     *
     */
    private String regexMatchString;


    /**
     * Create a new VarSpec.
     *
     * @param modifier
     * @param value
     */
    public VarSpec(String value, Modifier modifier)
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
    public VarSpec(String value, Modifier modifier, Integer position)
    {
        this.modifier = modifier;
        this.value = value;
        this.position = position;
        initVariableName();
        initRegExMatchString();
    }

    /**
     * Get the modifier.
     *
     * @return the modifier.
     */
    public Modifier getModifier()
    {
        return modifier;
    }

    private void initRegExMatchString()
    {
        StringBuilder b = new StringBuilder(BASE_PATTERN);
        if (modifier == Modifier.PREFIX)
        {
            b.append("{").append(getPosition()).append("}");
        }
        else
        {
            b.append("+");
        }
        regexMatchString = b.toString();
    }

    /**
     * Returns a regex pattern that matches the variable.
     *
     * @return
     */
    public String getRegExMatchString()
    {
        if (regexMatchString == null)
        {
            initRegExMatchString();
        }
        return regexMatchString;
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

    private void initVariableName()
    {
        variableName = getValue();
        if (modifier != Modifier.NONE)
        {
            if (modifier == Modifier.PREFIX)
            {
                String[] values = getValue().split(Modifier.PREFIX.getValue());
                variableName = values[0];
            }
            // Strip the '*' from the variable name if it's presnet on the variable
            // name. Note that in the case of the UriTemplateBuilder, the VarSpec
            // is not responsible for rendering the '*' on the generated template
            // output as that is done in the UriTemplateBuilder
            if (modifier == Modifier.EXPLODE && getValue().lastIndexOf('*') != -1)
            {
                variableName = getValue().substring(0, getValue().length() - 1);
            }
        }
    }

    /**
     * FIXME Comment this
     *
     * @return
     */
    public String getVariableName()
    {
        if (variableName == null)
        {
            return getValue();
        }
        return variableName;
    }

    @Override
    public String toString()
    {
        return "VarSpec [modifier=" + modifier + ", value=" + value + ", position=" + position + ", variableName="
        + variableName + "]";
    }

    public static class Builder
    {


    }

}
