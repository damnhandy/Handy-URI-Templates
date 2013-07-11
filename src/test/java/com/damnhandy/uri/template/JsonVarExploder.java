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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An example exploder that reads JSON data and explodes into a URI expression.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class JsonVarExploder implements VarExploder
{

   private InputStream source;

   private Map<String, Object> values;

   /**
    *
    * Create a new JsonVarExploder.
    *
    * @param file
    * @throws FileNotFoundException
    */
   public JsonVarExploder(File file) throws FileNotFoundException, VarExploderException
   {
      this(new FileInputStream(file));
   }

   public JsonVarExploder(String jsonText) throws VarExploderException, UnsupportedEncodingException
   {
      this(new ByteArrayInputStream(jsonText.getBytes("UTF-8")));
   }

   /**
    *
    */
   @Override
   public Collection<Object> getValues() throws VarExploderException
   {
      return values.values();
   }
   /**
    *
    * Create a new JsonVarExploder.
    *
    * @param in
    */
   public JsonVarExploder(InputStream in) throws VarExploderException
   {
      this.source = in;
      initValues();
   }

   private void initValues() throws VarExploderException
   {
      ObjectMapper mapper = new ObjectMapper();
      try
      {
         values = mapper.readValue(source, new TypeReference<Map<String, Object>>()
         {
         });
      }
      catch (JsonParseException e)
      {
         throw new VarExploderException(e);
      }
      catch (JsonMappingException e)
      {
         throw new VarExploderException(e);
      }
      catch (IOException e)
      {
         throw new VarExploderException(e);
      }
   }

   @Override
   public Map<String, Object> getNameValuePairs() throws VarExploderException
   {
      return values;
   }

}
