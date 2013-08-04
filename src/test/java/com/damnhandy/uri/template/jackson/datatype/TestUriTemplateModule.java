/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A TestUriTemplateModule.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestUriTemplateModule
{

   private static ObjectMapper mapper;

   @BeforeClass
   public static void setUp()
   {
      mapper = new ObjectMapper();
      mapper.registerModule(new UriTemplateModule());
   }

   @Test
   public void testDeserialize() throws Exception
   {
      Dummy dummy = mapper.readValue(new File("./src/test/resources/json/test1.json"), Dummy.class);
      Assert.assertEquals("http://example.com/orders/{orderId}{?view}", dummy.getTemplate().getTemplate());
   }

   @Test
   public void testDeserializeWithError()
   {

      try
      {
         mapper.readValue(new File("./src/test/resources/json/errorTest1.json"), Dummy.class);
      }

      catch (JsonMappingException e)
      {
         Assert.assertTrue(e.getCause().getClass().isAssignableFrom(JsonParseException.class));
         JsonParseException jpe = (JsonParseException) e.getCause();
         Assert.assertEquals(3, jpe.getLocation().getLineNr());
         Assert.assertEquals(3, jpe.getLocation().getLineNr());
         MalformedUriTemplateException mte = (MalformedUriTemplateException) jpe.getCause();
         Assert.assertEquals(40, mte.getLocation());
      }
      catch (IOException e)
      {
         Assert.fail();
      }

   }

   @Test
   public void testSerialize() throws Exception
   {
      Dummy dummy = new Dummy("test", "http://example.com/orders/{orderId}{?view}");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      mapper.writer().writeValue(out, dummy);

      Dummy dummy2 = mapper.readValue(out.toByteArray(), Dummy.class);

      Assert.assertEquals("test", dummy2.getName());
      Assert.assertEquals("http://example.com/orders/{orderId}{?view}", dummy2.getTemplate().getTemplate());
   }

   public static class Dummy
   {
      private String name;

      private UriTemplate template;

      public Dummy()
      {

      }

      /**
       * Create a new Dummy.
       * 
       * @param name
       * @param template
       */
      public Dummy(String name, String template) throws MalformedUriTemplateException
      {
         super();
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
