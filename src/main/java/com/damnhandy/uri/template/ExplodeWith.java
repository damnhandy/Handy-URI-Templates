/*
 * 
 */
package com.damnhandy.uri.template;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows a type to specify a {@link VarExploder} to be used when expanding
 * a Java object under the explode modifier.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD})
public @interface ExplodeWith {

   /**
    * The class name of the {@link VarExploder}
    * 
    * @return
    */
   Class<?> value();
}
