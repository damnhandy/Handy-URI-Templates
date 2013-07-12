/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Map;

import com.damnhandy.uri.template.impl.Modifier;
import com.damnhandy.uri.template.impl.UriTemplateParser;
import com.damnhandy.uri.template.impl.VarSpec;

/**
 * 
 * A UriTemplateBuilder.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class UriTemplateBuilder
{

   /**
    * The URI expression
    */
   private LinkedList<UriTemplateComponent> components = new LinkedList<UriTemplateComponent>();

   /**
    *
    */
   private DateFormat defaultDateFormat = null;

   /**
    *
    */
   private Map<String, Object> values = null;

   /**
    *
    * Create a new UriTemplateBuilder.
    *
    * @param templateString
    */
   UriTemplateBuilder(String templateString) throws MalformedUriTemplateException
   {
      this.components = new UriTemplateParser().scan(templateString);
   }
   
   
   

   /**
    *
    * Create a new UriTemplateBuilder.
    *
    * @param template
    */
   UriTemplateBuilder(UriTemplate template) throws MalformedUriTemplateException
   {
      this(template.getTemplate());
      this.values = template.getValues();
      this.defaultDateFormat = template.defaultDateFormat;
   }



   /**
   *
   * @param dateFormatString
   * @return
   * @since 2.0
   */
   public UriTemplateBuilder withDefaultDateFormat(String dateFormatString)
   {
      return this.withDefaultDateFormat(new SimpleDateFormat(dateFormatString));
   }

   /**
    *
    * @param dateFormat
    * @return
    * @since 2.0
    */
   public UriTemplateBuilder withDefaultDateFormat(DateFormat dateFormat)
   {
      defaultDateFormat = dateFormat;
      return this;
   }

   
   private void addComponent(UriTemplateComponent component)
   {
      this.components.add(component);
   }
   /**
    * <p>
    * Appends the expression from a base URI template expression, such as:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com");
    * </pre>
    *
    * <p>
    * A child expression can be appended by:
    * </p>
    * <pre>
    * UriTemplate template = UriTemplate.fromExpression("http://api.github.com")
    *                                   .expression("/repos/{user}/{repo}/commits");
    *
    * </pre>
    * <p>The resulting expression would result in:</p>
    * <pre>
    * http://api.github.com/repos/{user}/{repo}/commits
    * </pre>
    * <p>
    * Multiple expressions can be appended to the template as follows:
    * </p>
    * <pre>
    *  UriTemplate template = UriTemplateBuilder.fromTemplate("http://myhost")
    *                                           .append("{/version}")
    *                                           .append("{/myId}")
    *                                           .append("/things/{thingId}")
    *                                           .build()
    *                                           .set("myId","damnhandy")
    *                                           .set("version","v1")
    *                                           .set("thingId","12345");
    * </pre>
    * <p>This will result in the following template and URI:</p>
    * <pre>
    * Template: http://myhost{/version}{/myId}/things/{thingId}
    * URI:      http://myhost/v1/damnhandy/things/12345
    * </pre>
    *
    * @param template
    * @return
    * @since 2.0
    *
    */
   public UriTemplateBuilder literal(String string)
   {
      if (string == null)
      {
         return this;
      }
      addComponent(new Literal(string, 0));
      return this;
   }

   private static VarSpec[] toVarSpec(String...varSpec)
   {
      VarSpec[] vars = new VarSpec[varSpec.length];
      for(int i = 0; i < varSpec.length; i++)
      {
         vars[i] = var(varSpec[i]);
      }
      return vars;
   }

   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder simple(String...var)
   {
      simple(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder simple(VarSpec...var)
   {
      addComponent(Expression.simple(var).build());
      return this;
   }
   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder reserved(String...var)
   {
      reserved(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder reserved(VarSpec...var)
   {
      addComponent(Expression.reserved(var).build());
      return this;
   }
   
   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder fragment(String...var)
   {
      fragment(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder fragment(VarSpec...var)
   {
      addComponent(Expression.fragment(var).build());
      return this;
   }

   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder label(String...var)
   {
      label(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder label(VarSpec...var)
   {
      addComponent(Expression.label(var).build());
      return this;
   }
   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder matrix(String...var)
   {
      matrix(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder matrix(VarSpec...var)
   {
      addComponent(Expression.matrix(var).build());
      return this;
   }
   
   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder path(String...var)
   {
      path(toVarSpec(var));
      return this;
   }
   
   /**
    * FIXME Comment this
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder path(VarSpec...var)
   {
      addComponent(Expression.path(var).build());
      return this;
   }
   
   
   public UriTemplateBuilder query(String...var)
   {
      query(toVarSpec(var));
      return this;
   }

   public UriTemplateBuilder query(VarSpec...var)
   {
      addComponent(Expression.query(var).build());
      return this;
   }
   
   

   /**
    * FIXME Comment this
    *
    * @return
    * @since 2.0
    */
   public UriTemplate build() throws MalformedUriTemplateException
   {
      UriTemplate template = new UriTemplate(this.components);
      if(this.values != null)
      {
         template.set(values);
      }
      
      if (defaultDateFormat != null)
      {
         template.defaultDateFormat = defaultDateFormat;
      }
      return template;
   }
   
   /**
    * Adds a variable name to the expression.
    *
    * <pre>
    * var("foo");
    * </pre>
    *
    * Will yield the following expression:
    * <pre>
    * {foo}
    * </pre>
    *
    * @param varName
    * @return
    */
   public static VarSpec var(String varName)
   {
      return var(varName, Modifier.NONE, null);
   }

   /**
    * Adds a variable name to the expression with an explode modifier.
    *
    * <pre>
    * var("foo",true);
    * </pre>
    *
    * Will yield the following expression:
    * <pre>
    * {foo*}
    * </pre>
    *
    * @param varName
    * @param explode
    * @return
    */
   public static VarSpec var(String varName, boolean explode)
   {
      if (explode)
      {
         return var(varName, Modifier.EXPLODE, null);
      }
      return var(varName, Modifier.NONE, null);
   }

   /**
    * Adds a variable name to the expression with a prefix modifier.
    *
    * <pre>
    * var("foo",2);
    * </pre>
    *
    * Will yield the following expression:
    * <pre>
    * {foo:2}
    * </pre>
    * @param varName
    * @param prefix
    * @return
    */
   public static VarSpec var(String varName, int prefix)
   {
      return var(varName, Modifier.PREFIX, prefix);
   }

   /**
    *
    *
    * @param varName
    * @param modifier
    * @param position
    * @return
    */
   private static VarSpec var(String varName, Modifier modifier, Integer position)
   {
      return new VarSpec(varName, modifier, position);
   }

}