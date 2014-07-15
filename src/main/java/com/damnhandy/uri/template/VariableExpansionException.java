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
 * <p>
 * Raised when the the template processor
 * encounters an issue expanding the value into the variable at expansion time.
 * Typical scenarios are:
 * </p>
 * <ul>
 *  <li>The variable specification declares a prefix but the value is an array</li>
 *  <li>A value is a {@link java.util.List} of {@link java.util.List} or an array of array</li>
 * </ul>
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public class VariableExpansionException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -1927979719672747848L;


   /**
    * Create a new VariableExpansionException.
    *
    * @param message
    */
   public VariableExpansionException(final String message)
   {
      super(message);
   }

   /**
    * Create a new VariableExpansionException.
    *
    * @param cause
    */
   public VariableExpansionException(final Throwable cause)
   {
      super(cause);
   }

   /**
    * Create a new VariableExpansionException.
    *
    * @param message
    * @param cause
    */
   public VariableExpansionException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
