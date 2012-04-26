/*
 * 
 */
package com.damnhandy.uri.template;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.damnhandy.uri.template.VarExploder;
import com.damnhandy.uri.template.impl.VariableExpansionException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An example exploder that reads JSON data and explodes into a URI template.
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
   public JsonVarExploder(File file) throws FileNotFoundException
   {
      this(new FileInputStream(file));
   }

   public JsonVarExploder(String jsonText)
   {
      this(new ByteArrayInputStream(jsonText.getBytes()));
   }

   /**
    * 
    * Create a new JsonVarExploder.
    * 
    * @param in
    */
   public JsonVarExploder(InputStream in)
   {
      this.source = in;
      initValues();
   }

   private void initValues()
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
         throw new VariableExpansionException(e);
      }
      catch (JsonMappingException e)
      {
         throw new VariableExpansionException(e);
      }
      catch (IOException e)
      {
         throw new VariableExpansionException(e);
      }
   }

   @Override
   public Map<String, Object> getNameValuePairs()
   {
      return values;
   }

}
