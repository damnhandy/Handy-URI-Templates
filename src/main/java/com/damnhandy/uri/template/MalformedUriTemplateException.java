/*
 * Copyright 2013, Ryan J. McDonough
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
 * Raised when the the template processor encounters an issue parsing the URI template string. It indicates
 * the either the template itself is malformed, or an expression within the template is malformed.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
public class MalformedUriTemplateException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 5883174281977078450L;

   private final int location;
   /**
    * Create a new UriTemplateParseException.
    *
    * @param message
    * @param cause
    */
   public MalformedUriTemplateException(String message, final int location, Throwable cause)
   {
      super(message, cause);
      this.location = location;
   }

   /**
    * Create a new UriTemplateParseException.
    *
    * @param message
    */
   public MalformedUriTemplateException(String message, int location)
   {
      super(message);
      this.location = location;
   }

   public int getLocation()
   {
      return this.location;
   }
}
