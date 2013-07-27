/*
 *
 *
 */
package com.damnhandy.uri.template.jackson.datatype;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A UriTemplateModule.
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
