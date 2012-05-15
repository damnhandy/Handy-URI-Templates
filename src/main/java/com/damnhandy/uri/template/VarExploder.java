/*
 * 
 */
package com.damnhandy.uri.template;

import java.util.Collection;
import java.util.Map;

import com.damnhandy.uri.template.impl.VariableExpansionException;


/**
 * 
 * <p>
 * A {@link VarExploder} is invoked when an explode modifier "*" is encountered within
 * a variable name within a URI expression expression and the replacement value is a complex 
 * type, such a some type of POJO or other data type. For most use cases, the {@link DefaultVarExploder} will be 
 * sufficient. Please refer to the {@link DefaultVarExploder} JavaDoc
 * for more details on how it works.
 * </p>
 * 
 * <p>
 * Should the {@link DefaultVarExploder} not be suitable for your needs, custom {@link VarExploder}
 * implementations can be added by rolling your own implementation. A custom {@link VarExploder} 
 * implementation can be registered in one of two ways. By wrapping your object in your {@link VarExploder}:
 * </p>
 * <pre>
 * UriTemplate.fromExpression("/mapper{?address*}").set("address", new MyCustomVarExploder(address)).expand();
 * </pre>
 * <p>
 * <strong>Note:</strong> {@link VarExploder} implementations are <strong>ONLY</strong> invoked when the 
 * explode modifier "*" is declared in the URI Template expression. If the variable declaration does not 
 * specify the explode modifier, a {@link VariableExpansionException} will be raised.
 * </p>
 * 
 * <p>
 * Please see the unit test on example usage of a custom {@link VarExploder}.
 * </p>
 * 
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public interface VarExploder
{


   /**
    * Returns the properties of the source object a {@link Map} of 
    * name/value pairs.
    * 
    * @return the object properties as name/value pairs.
    */
   Map<String, Object> getNameValuePairs();
   
   /**
    * Returns the properties of the source object a {@link Collection} of 
    * object values.
    * 
    * @return
    */
   Collection<Object> getValues();

}