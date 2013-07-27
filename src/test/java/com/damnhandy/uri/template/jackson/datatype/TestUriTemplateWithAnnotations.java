/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A TestUriTemplateWithAnnotations.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateWithAnnotations
{

   @Test
   public void testSerialize() throws Exception 
   {
      ObjectMapper mapper = new ObjectMapper();
      Dummy dummy = new Dummy("test", "http://example.com/orders/{orderId}{?view}");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      mapper.writer().writeValue(out, dummy);
      
      Dummy dummy2 = mapper.readValue(out.toByteArray(), Dummy.class);
      
      Assert.assertEquals("test", dummy2.getName());
      Assert.assertEquals("http://example.com/orders/{orderId}{?view}", dummy2.getTemplate().getTemplate());
   }
   
   
   public static class Dummy
   {
      
      @JsonProperty("name")
      private String name;
      
      @JsonDeserialize(using = UriTemplateDeserializer.class)
      @JsonSerialize(using = UriTemplateSerializer.class)
      private UriTemplate template;
      
      public Dummy() {
         
      }
      /**
       * Create a new Dummy.
       * 
       * @param name
       * @param template
       */
      public Dummy(String name, String template) throws MalformedUriTemplateException
      {
         this.name = name;
         this.template = UriTemplate.fromTemplate(template);
      }
      /**
       * Get the name.
       * 
       * @return the name.
       */
      public String getName()
      {
         return name;
      }
      /**
       * Set the name.
       * 
       * @param name The name to set.
       */
      public void setName(String name)
      {
         this.name = name;
      }
      /**
       * Get the template.
       * 
       * @return the template.
       */
      public UriTemplate getTemplate()
      {
         return template;
      }
      /**
       * Set the template.
       * 
       * @param template The template to set.
       */
      public void setTemplate(UriTemplate template)
      {
         this.template = template;
      }
   }
}
