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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Utility used to match a URI to a collection of {@link UriTemplate} instances. 
 * 
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
public class UriTemplateResolver
{

   private List<UriTemplate> templates = new ArrayList<>();

   /**
    * 
    * 
    * @param templateStrings
    * @throws MalformedUriTemplateException
    */
   public void addUriTemplates(final String...templateStrings) throws MalformedUriTemplateException
   {
      for(String template : templateStrings)
      {
         addUriTemplate(template);
      }
   }
   /**
    * Creates a {@link UriTemplate} from the input string and adds it
    * to the collection of templates to match against.
    * 
    * @param templateString
    * @return
    * @throws MalformedUriTemplateException
    */
   public boolean addUriTemplate(final String templateString) throws MalformedUriTemplateException
   {
      return this.addUriTemplate(UriTemplate.fromTemplate(templateString));
   }
   /**
    * Adds a {@link UriTemplate} instance to an internal list of
    * templates to match against.
    * 
    * @param template
    * @return
    */
   public boolean addUriTemplate(final UriTemplate template)
   {
      return templates.add(template);
   }
   
   
   /**
    * Matches the input URI against the collection of {@link UriTemplate}'s
    * 
    * @param uri
    * @return
    */
   public Map<String, Object> match(String uri)
   {
      for(UriTemplate template : templates)
      {
         Pattern p = template.getReverseMatchPattern();
         System.out.println(p.pattern());
         Matcher m = p.matcher(uri);
         if (m.matches())
         {
            Map<String, Object> params = new HashMap<String, Object>(m.groupCount());
            for (String param : template.getVariables())
            {
               params.put(param, m.group(param));
            }
            System.out.println(params);
            return params;
         }
      }
      return null;
   }
}
