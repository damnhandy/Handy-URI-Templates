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

import com.damnhandy.uri.template.Expression;
import com.damnhandy.uri.template.Literal;
import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplateComponent;

import java.util.LinkedList;

/**
 * Utility class used to parse the URI template string into a series of components
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
public final class UriTemplateParser
{

    private static final char EXPR_START = '{';

    private static final char EXPR_END = '}';

    private boolean startedTemplate = false;

    private boolean expressionCaptureOn = false;

    private boolean literalCaptureOn = false;

    private LinkedList<UriTemplateComponent> components = new LinkedList<UriTemplateComponent>();

    private StringBuilder buffer;

    private int startPosition;


    /**
     * Scans the URI template looking for literal string components and expressions.
     *
     * @param templateString the URI template string to scan
     * @since 2.0
     */
    public LinkedList<UriTemplateComponent> scan(String templateString) throws MalformedUriTemplateException
    {
        char[] template = templateString.toCharArray();
        startTemplate();
        int i;
        for (i = 0; i < template.length; i++)
        {
            char c = template[i];

            if (c == EXPR_START)
            {
                if (literalCaptureOn)
                {
                    endLiteral(i);
                }
                startExpression(i);
            }

            if (c != EXPR_START)
            {
                startLiteral(i);
            }


            if (expressionCaptureOn || literalCaptureOn)
            {
                capture(c);
            }

            if (c == EXPR_END)
            {
                endExpression(i);
                startLiteral(i);
            }
        }
        if (literalCaptureOn)
        {
            endLiteral(i);
        }
        endTemplate(i);
        return components;
    }

    /**
     * If capture is active, collect the characters into the buffer
     *
     * @param currentChar
     */
    private void capture(char currentChar)
    {
        if (buffer == null)
        {
            buffer = new StringBuilder();
        }
        buffer.append(currentChar);
    }

    /**
     * Called when the {@link UriTemplateParser} has started on the template.
     */
    private void startTemplate()
    {
        startedTemplate = true;
    }

    /**
     * Called when the end of the template is reached. If an expression has
     * not been closed, an exception will be raised.
     */
    private void endTemplate(int position) throws MalformedUriTemplateException
    {
        startedTemplate = false;
        if (expressionCaptureOn)
        {
            throw new MalformedUriTemplateException("The expression at position " + startPosition + " was never terminated", startPosition);
        }

    }

    /**
     * Marks the start of
     *
     * @param position
     */
    private void startLiteral(int position) throws MalformedUriTemplateException
    {

        if (startedTemplate)
        {
            if (!literalCaptureOn)
            {
                //throw new IllegalStateException("Literal capture already started at character "+template[position]);
                literalCaptureOn = true;
                startPosition = position;
            }
        }
        else
        {
            throw new IllegalStateException("Cannot start a literal without beginning the template");
        }
    }

    private void endLiteral(int position) throws MalformedUriTemplateException
    {
        if (startedTemplate)
        {
            if (!literalCaptureOn)
            {
                throw new IllegalStateException("Can't end a literal without starting it first");
            }
            // in the case that we have back to back expressions ({foo}{?bar}), we can get into a state
            // we started capturing a literal but never actually collected anything yet.
            if (buffer != null)
            {
                components.add(new Literal(buffer.toString(), this.startPosition));
                literalCaptureOn = false;
                buffer = null;
            }

        }
        else
        {
            throw new IllegalStateException("Cannot end a literal without beginning the template");
        }
    }


    /**
     * Called when the start of an expression has been encountered.
     */
    private void startExpression(int position) throws MalformedUriTemplateException
    {

        if (startedTemplate)
        {
            if (expressionCaptureOn)

            {
                throw new MalformedUriTemplateException("A new expression start brace found at " + position
                + " but another unclosed expression was found at " + startPosition, position);
            }
            literalCaptureOn = false;
            expressionCaptureOn = true;
            startPosition = position;
        }
        else
        {
            throw new IllegalStateException("Cannot start an expression without beginning the template");
        }
    }

    /**
     * Called when the end of an expression has been encountered.
     */
    private void endExpression(int position) throws MalformedUriTemplateException
    {

        // an expression close brace is found without a start
        if (startedTemplate)
        {
            if (!expressionCaptureOn)

            {
                throw new MalformedUriTemplateException("Expression close brace was found at position " + position
                + " yet there was no start brace.", position);
            }
            expressionCaptureOn = false;
            components.add(new Expression(buffer.toString(), this.startPosition));
            buffer = null;
        }
        else
        {
            throw new IllegalStateException("Cannot end an expression without beginning the template");
        }
    }
}
