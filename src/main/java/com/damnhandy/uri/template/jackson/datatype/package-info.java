
/**
 * <p>
 * These classes allow the {@link com.damnhandy.uri.template.UriTemplate } class to
 * better integrate with the Jackson JSON framework. This will enable JSON properties
 * to map directly to a {@link com.damnhandy.uri.template.UriTemplate }. This can be 
 * done by one of two ways:
 * </p>
 * <p>Via Object Mapper:</p>
 * <pre>
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.registerModule(new UriTemplateModule());
 * </pre>
 * 
 * <p>Or via annotations on the desired property:</p>
 * 
 * <pre>
 * {@literal @}JsonDeserialize(using = UriTemplateDeserializer.class)
 * {@literal @}JsonSerialize(using = UriTemplateSerializer.class)
 * private UriTemplate template;
 * ...
 * </pre>
 * 
 * <p>
 * Both options will yield the same results, but the module registration is far more convenient.
 * </p>
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
package com.damnhandy.uri.template.jackson.datatype;


