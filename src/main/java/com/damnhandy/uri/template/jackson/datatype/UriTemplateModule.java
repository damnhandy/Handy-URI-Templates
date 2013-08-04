/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Uri Template module for Jackson. To use it, simpley register it 
 * with an {@link ObjectMapper} like so:
 * 
 * <pre>
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.registerModule(new UriTemplateModule());
 * </pre>
 * 
 * Any mapped JSON property that is a {@link UriTemplate} will be
 * serialized or deserialized properly. 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class UriTemplateModule extends SimpleModule
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 20L;

   /**
    * 
    * Create a new UriTemplateModule.
    *
    */
   public UriTemplateModule() {
      super("Handy-URI-Templates", Version.unknownVersion());
      addDeserializer(UriTemplate.class, new UriTemplateDeserializer());
      addSerializer(UriTemplate.class, new UriTemplateSerializer());
   }

}
