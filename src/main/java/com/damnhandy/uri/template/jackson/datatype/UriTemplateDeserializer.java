/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import java.io.IOException;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * A {@link JsonDeserializer} that deserializes a string into a {@link UriTemplate}.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @see JsonDeserializer
 * @since 2.0
 * @version $Revision: 1.1 $
 */
public class UriTemplateDeserializer extends JsonDeserializer<UriTemplate>
{

   @Override
   public UriTemplate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException,
         JsonProcessingException
   {

      String value = parser.getText();
      try
      {
         return UriTemplate.fromTemplate(value);
      }
      catch (MalformedUriTemplateException e)
      {
         throw new JsonParseException(parser,"Error parsing the URI Template", e);
      }
   }

}
