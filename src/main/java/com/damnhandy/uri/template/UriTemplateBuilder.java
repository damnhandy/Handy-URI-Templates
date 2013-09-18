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
import com.damnhandy.uri.template.impl.Operator;
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
    * Appends a {@link Literal} value to the {@link UriTemplate}. The following 
    * code:
    * 
    * <pre>
    * UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
    *                                   .literal("/foo")
    *                                   .build();
    * </pre>
    * 
    * Will generate the following template:
    * 
    * <pre>
    * http://example.com/foo
    * </pre>
    * 
    * Note that this particular example has no expressions, so it's not a valid URI template.
    * @param string
    * @return
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
     * Appends a template expression using the no operator. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .simple("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{foo}
     * </pre>
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
    * Appends a template expression using the reserved operator (+). The following 
    * code:
    * <pre>
    * UriTemplate template = 
    *        UriTemplate.buildFromTemplate("http://example.com/")
    *                   .reserved("foo")
    *                   .build();
    * </pre>
    * Will generate the following URI Template string:
    * <pre>
    * http://example.com/{+foo}
    * </pre>
    * @param var
    * @return
    */
   public UriTemplateBuilder reserved(String...var)
   {
      reserved(toVarSpec(var));
      return this;
   }

   /**
    * Appends a template expression using the reserved operator (+). The 
    * following code:
    * <pre>
    * UriTemplate template = 
    *        UriTemplate.buildFromTemplate("http://example.com/")
    *                   .reserved("foo")
    *                   .build();
    * </pre>
    * Will generate the following URI Template string:
    * <pre>
    * http://example.com/{+foo}
    * </pre>
    * @param var
    * @return
    */
   public UriTemplateBuilder reserved(VarSpec...var)
   {
      addComponent(Expression.reserved(var).build());
      return this;
   }
   
   /**
    * Appends a template expression using the fragment operator (#). The 
    * following code:
    * <pre>
    * UriTemplate template = 
    *        UriTemplate.buildFromTemplate("http://example.com/")
    *                   .fragement("foo")
    *                   .build();
    * </pre>
    * Will generate the following URI Template string:
    * <pre>
    * http://example.com/{#foo}
    * </pre>
    * 
    * @param var
    * @throws UriTemplateBuilderException if you attempt to add more than one fragment expression, a UriTemplateBuilderException will be raised
    * @return
    */
   public UriTemplateBuilder fragment(String...var) throws UriTemplateBuilderException
   {
      fragment(toVarSpec(var));
      return this;
   }

   /**
    * Appends a template expression using the fragment operator (#). The 
    * following code:
    * <pre>
    * UriTemplate template = 
    *        UriTemplate.buildFromTemplate("http://example.com/")
    *                   .fragement(var("foo", 1))
    *                   .build();
    * </pre>
    * Will generate the following URI Template string:
    * <pre>
    * http://example.com/{#foo:1}
    * </pre>
    * 
    * @param var
    * @throws UriTemplateBuilderException if you attempt to add more than one fragment expression, a UriTemplateBuilderException will be raised
    * @return
    */
   public UriTemplateBuilder fragment(VarSpec...var) throws UriTemplateBuilderException
   {
      if(hasExpressionWithOperator(Operator.FRAGMENT))
      {
         throw new UriTemplateBuilderException("The template already has a fragment expression and this would not result in a valid URI");
      }
      addComponent(Expression.fragment(var).build());
      return this;
   }

   
   private boolean hasExpressionWithOperator(Operator op)
   {
      for(UriTemplateComponent c : components)
      {
         if(Expression.class.isInstance(c))
         {
            Expression e = (Expression) c;
            if(e.getOperator() == op)
            {
               return true;
            }
         }
      }
      return false;
   }
    /**
     * Appends a template expression using the label (.) operator. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .label("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{.foo}
     * </pre>
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
     * Appends a template expression using the matrix (;) operator. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .matrix("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{foo}
     * </pre>
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
     * Appends a template expression using the path (/) operator. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com")
     *                   .path("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com{/foo}
     * </pre>
     * @param var
     * @return
     */
   public UriTemplateBuilder path(String...var)
   {
      path(toVarSpec(var));
      return this;
   }
   
   /**
    *
    * 
    * @param var
    * @return
    */
   public UriTemplateBuilder path(VarSpec...var)
   {
      addComponent(Expression.path(var).build());
      return this;
   }

    /**
     * Appends a template expression using the query (?) operator. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .query("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{?foo}
     * </pre>
     * @param var
     * @return
     */
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