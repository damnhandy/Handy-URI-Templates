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

/**
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class UriEncodingException extends VariableExpansionException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 51633959333775593L;

   /**
    * Create a new UriEncodingException.
    *
    */
   public UriEncodingException()
   {
      super();
   }

   /**
    * Create a new UriEncodingException.
    *
    * @param message
    * @param cause
    */
   public UriEncodingException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Create a new UriEncodingException.
    *
    * @param message
    */
   public UriEncodingException(String message)
   {
      super(message);
   }

   /**
    * Create a new UriEncodingException.
    *
    * @param cause
    */
   public UriEncodingException(Throwable cause)
   {
      super(cause);
   }

}
