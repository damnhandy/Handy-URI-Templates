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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.damnhandy.uri.template.impl.RFC6570UriTemplate;

/**
 * A TemplateBuilder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.2
 */
public final class UriTemplateBuilder
{

   /**
    * The URI expression
    */
   private final StringBuilder templateBuffer;

   /**
    *
    */
   private DateFormat defaultDateFormat = null;

   /**
    *
    */
   private Map<String, Object> values = null;

   /**
    *
    * Create a new UriTemplateBuilder.
    *
    * @param templateString
    */
   protected UriTemplateBuilder(String templateString)
   {
      this.templateBuffer = new StringBuilder(templateString);
   }

   /**
    *
    * Create a new UriTemplateBuilder.
    *
    * @param template
    */
   protected UriTemplateBuilder(UriTemplate template)
   {
      this(template.getTemplate());
      this.values = template.getValues();
      this.defaultDateFormat = template.defaultDateFormat;
   }

   /**
    * Creates a new {@link UriTemplateBuilder} from the template string.
    *
    * @param templateString
    * @return
    * @since 1.2
    */
   public static UriTemplateBuilder fromTemplate(String template)
   {
      return new UriTemplateBuilder(template);
   }

   /**
    * <p>
    * Creates a new {@link UriTemplateBuilder} from a root {@link UriTemplate}. This
    * method will create a new {@link UriTemplate} from the base and copy the variables
    * from the base template to the new {@link UriTemplate}.
    * </p>
    * <p>
    * This method is useful when the base template is less volatile than the child
    * expression and you want to merge the two.
    * </p>
    * @param base
    * @return
    * @since 1.2
    */
   public static UriTemplateBuilder fromTemplate(UriTemplate template)
   {
      return new UriTemplateBuilder(template);
   }

   /**
   *
   * @param dateFormatString
   * @return
   * @since 1.2
   */
   public UriTemplateBuilder withDefaultDateFormat(String dateFormatString)
   {
      return this.withDefaultDateFormat(new SimpleDateFormat(dateFormatString));
   }

   /**
    *
    * @param dateFormat
    * @return
    * @since 1.2
    */
   public UriTemplateBuilder withDefaultDateFormat(DateFormat dateFormat)
   {
      defaultDateFormat = dateFormat;
      return this;
   }

   /**
    * <p>
    * Appends the expression from a base URI template expression, such as:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com");
    * </pre>
    *
    * <p>
    * A child expression can be appended by:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com")
    *                                   .expression("/repos/{user}/{repo}/commits");
    *
    * </pre>
    * <p>The resulting expression would result in:</p>
    * <pre>
    * http://api.github.com/repos/{user}/{repo}/commits
    * </pre>
    * <p>
    * Multiple expressions can be appended to the template as follows:
    * </p>
    * <pre>
    *  UriTemplate template = UriTemplateBuilder.fromTemplate("http://myhost")
    *                                           .append("{/version}")
    *                                           .append("{/myId}")
    *                                           .append("/things/{thingId}")
    *                                           .build()
    *                                           .set("myId","damnhandy")
    *                                           .set("version","v1")
    *                                           .set("thingId","12345");
    * </pre>
    * <p>This will result in the following template and URI:</p>
    * <pre>
    * Template: http://myhost{/version}{/myId}/things/{thingId}
    * URI:      http://myhost/v1/damnhandy/things/12345
    * </pre>
    * <p>
    * Since a URI template is not a URI, there no way to accurately determine
    * how the variable expression should be delimited. Therefore, it is up to
    * the expression author to include the necessary delimiters in each sub
    * expression.
    * </p>
    * @param template
    * @return
    * @since 1.2
    *
    */
   public UriTemplateBuilder append(String template)
   {
      if (template == null)
      {
         return this;
      }
      this.templateBuffer.append(template.trim());
      return this;
   }

   /**
    * FIXME Comment this
    *
    * @param expression
    * @return
    * @since 1.2
    */
   public UriTemplateBuilder append(Expression expression)
   {
      return append(expression.toString());
   }

   /**
    * FIXME Comment this
    *
    * @return
    * @since 1.2
    */
   public UriTemplate build() throws MalformedUriTemplateException
   {
      UriTemplate template = new RFC6570UriTemplate(templateBuffer.toString());
      template.set(values);
      if (defaultDateFormat != null)
      {
         template.defaultDateFormat = defaultDateFormat;
      }
      return template;
   }

}
