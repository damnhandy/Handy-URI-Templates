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

import java.util.ArrayList;
import java.util.List;

import com.damnhandy.uri.template.MalformedUriTemplateException;

/**
 *
 * Utility class used to scan the URI Template string for expressions.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
class ExpressionScanner
{

   private static final char EXPR_START = '{';

   private static final char EXPR_END = '}';

   private boolean startedTemplate = false;

   private boolean expressionCaptureOn = false;

   private List<String> expressions = new ArrayList<String>();

   private StringBuilder expression;

   private char[] template;

   private int expressionStartPos;

   /**
    *
    *
    */
   public List<String> scan(String templateString) throws MalformedUriTemplateException
   {
      this.template = templateString.toCharArray();
      startTemplate();
      for (int i = 0; i < template.length; i++)
      {
         char current = template[i];

         if (current == EXPR_START)
         {
            startExpression(i);
         }

         if (expressionCaptureOn)
         {
            expression(current);
         }

         if (current == EXPR_END)
         {
            endExpression(i);
         }
      }
      endTemplate();
      return expressions;
   }

   /**
    * If expression capture is active, collect the characters into t
    *
    * @param currentChar
    */
   private void expression(char currentChar)
   {
      expression.append(currentChar);
   }

   /**
    * Called when the {@link ExpressonScanner} has started on the template.
    *
    */
   private void startTemplate()
   {
      startedTemplate = true;
   }

   /**
    * Called when the end of the template is reached. If an expression has
    * not been closed, an exception will be raised.
    *
    */
   private void endTemplate() throws MalformedUriTemplateException
   {
      startedTemplate = false;
      if (expressionCaptureOn == true)
      {
         throw new MalformedUriTemplateException("Template scanning complete, but the start of an expression at "
               + expressionStartPos + " was never terminated");
      }
   }

   /**
    * Called when the start of an expression has been encountered.
    *
    */
   private void startExpression(int position) throws MalformedUriTemplateException
   {

      if (startedTemplate)
      {
         if (expressionCaptureOn)

         {
            throw new MalformedUriTemplateException("A new expression start brace found at " + position
                  + " but another unclosed expression was found at " + expressionStartPos);
         }
         expressionStartPos = position;
         expressionCaptureOn = true;
         expression = new StringBuilder();
      }
      else
      {
         throw new IllegalStateException("Cannot start an expression without beginning the template");
      }
   }

   /**
    * Called when the end of an expression has been encountered.
    *
    */
   private void endExpression(int position) throws MalformedUriTemplateException
   {

      // an expression close brace is found without a start
      if (startedTemplate)
      {
         if (expressionCaptureOn == false)

         {
            throw new MalformedUriTemplateException("Expression close brace was found at position " + position
                  + " yet there was no start brace.");
         }
         expressionCaptureOn = false;
         expressions.add(expression.toString());
         expression = null;
      }
      else
      {
         throw new IllegalStateException("Cannot end an expression without beginning the template");
      }
   }
}
