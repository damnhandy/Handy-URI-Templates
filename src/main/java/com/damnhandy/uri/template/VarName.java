/*
 * 
 */
package com.damnhandy.uri.template;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When this annotation is placed on a field or getter method, the 
 * annotation value will be used instead of the property name.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface VarName {

   /**
    * Returns the preferred name of the property.
    * 
    * @return
    */
   String value();
}
