/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import java.io.IOException;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * A {@link JsonSerializer} that serializes a {@link UriTemplate} to a JSON string value.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class UriTemplateSerializer extends JsonSerializer<UriTemplate>
{

   @Override
   public void serialize(UriTemplate value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
         JsonProcessingException
   {
      jgen.writeString(value.getTemplate());
   }
}
