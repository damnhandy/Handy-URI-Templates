/*
 *
 *
 */
package com.damnhandy.uri.template;

import com.damnhandy.uri.template.impl.Modifier;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.UriTemplateParser;
import com.damnhandy.uri.template.impl.VarSpec;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>
 * A utility class used for programatically generating a {@link UriTemplate}.
 * The class can be used as follows:
 * </p>
 * <pre>
 * UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
 *                                   .literal("/foo")
 *                                   .path(var("thing1"), var("explodedThing", true))
 *                                   .fragment(var("prefix", 2))
 *                                   .build();
 * </pre>
 * <p>
 * This code will return a {@link UriTemplate} with the following value:
 * </p>
 * <pre>
 * http://example.com/foo{/thing1,explodedThing*}{#prefix:2}
 * </pre>
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
    private DateTimeFormatter defaultDateTimeFormatter = null;

    /**
     *
     */
    private Map<String, Object> values = null;


    UriTemplateBuilder() {
        this.components = new LinkedList<UriTemplateComponent>();
    }
    /**
     * Create a new UriTemplateBuilder.
     *
     * @param templateString the initial URI template string
     */
    UriTemplateBuilder(String templateString) throws MalformedUriTemplateException
    {
        this.components = new UriTemplateParser().scan(templateString);
    }


    /**
     * Create a new UriTemplateBuilder.
     *
     * @param template the initial UriTemplate
     */
    UriTemplateBuilder(UriTemplate template) throws MalformedUriTemplateException
    {
        this(template.getTemplate());
        this.values = template.getValues();
        this.defaultDateTimeFormatter = template.defaultDateTimeFormatter;
    }


    /**
     * @param dateFormatString the date format to use internally
     * @return the UriTemplateBuilder
     * @since 2.0
     */
    public UriTemplateBuilder withDefaultDateFormat(String dateFormatString)
    {
        return this.withDefaultDateFormat(DateTimeFormat.forPattern(dateFormatString));
    }

    private UriTemplateBuilder withDefaultDateFormat(DateTimeFormatter dateTimeFormatter)
    {
        defaultDateTimeFormatter = dateTimeFormatter;
        return this;
    }

    /**
     * @param dateFormat the date format to use internally
     * @return the UriTemplateBuilder
     * @since 2.0
     * @deprecated replaced by {@link #withDefaultDateFormat(String) withDefaultDateFormat}
     */
    @Deprecated
    public UriTemplateBuilder withDefaultDateFormat(DateFormat dateFormat)
    {
        if (!(dateFormat instanceof SimpleDateFormat))
        {
            throw new IllegalArgumentException(
            "The only supported subclass of java.text.DateFormat is java.text.SimpleDateFormat");
        }
        defaultDateTimeFormatter = DateTimeFormat.forPattern(((SimpleDateFormat) dateFormat).toPattern());
        return this;
    }


    void addComponent(UriTemplateComponent component)
    {
        this.components.add(component);
    }

    void addComponents(Collection<UriTemplateComponent> compoments)
    {
        this.components.addAll(compoments);
    }

    /**
     * Appends a {@link Literal} value to the {@link UriTemplate}. The following
     * code:
     * <p>
     * <pre>
     * UriTemplate template = UriTemplate.buildFromTemplate("http://example.com")
     *                                   .literal("/foo")
     *                                   .build();
     * </pre>
     * <p>
     * Will generate the following template:
     * <p>
     * <pre>
     * http://example.com/foo
     * </pre>
     * <p>
     * Note that this particular example has no expressions, so it's not a valid URI template.
     *
     * @param string the literal string
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

    /**
     * @param varSpec the varspec name
     * @return the array of varspecs
     */
    private static VarSpec[] toVarSpec(String... varSpec)
    {
        VarSpec[] vars = new VarSpec[varSpec.length];
        for (int i = 0; i < varSpec.length; i++)
        {
            vars[i] = var(varSpec[i]);
        }
        return vars;
    }

    /**
     * Appends a template expression using no operator. The following
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
     *
     * @param var the name of the variable
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder simple(String... var)
    {
        simple(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using no operator but with an optional
     * modifier. The following code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .simple(var("foo",true))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{foo*}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder simple(VarSpec... var)
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
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder reserved(String... var)
    {
        reserved(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the reserved operator (+) along
     * with an optional modifier. The following code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .reserved(var("foo",1))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{+foo:1}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder reserved(VarSpec... var)
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
     * @param var the varspec name
     * @return the UriTemplateBuilder
     * @throws UriTemplateBuilderException if you attempt to add more than one fragment expression, a UriTemplateBuilderException will be raised
     */
    public UriTemplateBuilder fragment(String... var) throws UriTemplateBuilderException
    {
        fragment(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the fragment operator (#) with a
     * modifier. The following code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
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
     * @param var the varspec
     * @return the UriTemplateBuilder
     * @throws UriTemplateBuilderException if you attempt to add more than one fragment expression, a UriTemplateBuilderException will be raised
     */
    public UriTemplateBuilder fragment(VarSpec... var) throws UriTemplateBuilderException
    {
        if (hasExpressionWithOperator(Operator.FRAGMENT))
        {
            throw new UriTemplateBuilderException("The template already has a fragment expression and this would not result in a valid URI");
        }
        addComponent(Expression.fragment(var).build());
        return this;
    }

    /**
     * Scans the components for an expression with the specified operator.
     *
     * @param op
     * @return
     */
    private boolean hasExpressionWithOperator(Operator op)
    {
        for (UriTemplateComponent c : components)
        {
            if (Expression.class.isInstance(c))
            {
                Expression e = (Expression) c;
                if (e.getOperator() == op)
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
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder label(String... var)
    {
        label(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the label (.) operator and modifier.
     * The following code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .label(var("foo", true))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{.foo*}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder label(VarSpec... var)
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
     * http://example.com/{;foo}
     * </pre>
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder matrix(String... var)
    {
        matrix(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the matrix (;) operator and modifier.
     * The following code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .matrix(var("foo", true))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{;foo*}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder matrix(VarSpec... var)
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
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder path(String... var)
    {
        path(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the path (/) operator and modifier.
     * The following code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com")
     *                   .path(var("foo", 1))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com{/foo:1}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder path(VarSpec... var)
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
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder query(String... var)
    {
        query(toVarSpec(var));
        return this;
    }

    /**
     * Appends a template expression using the query (?) operator and
     * and optional modifier. The following
     * code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .query(var("foo",1))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{?foo:1}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder query(VarSpec... var)
    {
        addComponent(Expression.query(var).build());
        return this;
    }

    /**
     * Appends a template expression using the form-style query continuation. The following
     * code:
     * <pre>
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .continuation("foo")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{&amp;foo}
     * </pre>
     *
     * @param var the varspec name
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder continuation(String... var)
    {
        return continuation(toVarSpec(var));
    }

    /**
     * Appends a template expression using the form-style query continuation and
     * and optional modifier. The following
     * code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .query(var("foo",1))
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/{&amp;foo:1}
     * </pre>
     *
     * @param var the varspec
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder continuation(VarSpec... var)
    {
        addComponent(Expression.continuation(var).build());
        return this;
    }

    /**
     * Parses the template and appends the parsed components
     * to the builder. The following
     * code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .template("foo/{id}{?filter}")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/foo/{id}{?filter}
     * </pre>
     *
     * @param template the uri template
     * @return the UriTemplateBuilder
     */
    public UriTemplateBuilder template(UriTemplate... template) {
        for(UriTemplate t : template) {
            addComponents(t.getComponents());
        }

        return this;
    }

    /**
     * Parses the template and appends the parsed components
     * to the builder. The following
     * code:
     * <pre>
     * import static com.damnhandy.uri.template.UriTemplateBuilder.var;
     *
     * ...
     *
     * UriTemplate template =
     *        UriTemplate.buildFromTemplate("http://example.com/")
     *                   .template("foo/{id}{?filter}")
     *                   .build();
     * </pre>
     * Will generate the following URI Template string:
     * <pre>
     * http://example.com/foo/{id}{?filter}
     * </pre>
     *
     * @param template the uri template string
     * @return the uri template builder
     */
    public UriTemplateBuilder template(String... template) {
        UriTemplateParser parser = new UriTemplateParser();
        for(String t : template) {
            addComponents(parser.scan(t));
        }

        return this;
    }

    /**
     * Returns an array of the components in the Builder.
     * @return array of the components in the Builder
     */
    public UriTemplateComponent[] getComponents()
    {
        return this.components.toArray(new UriTemplateComponent[components.size()]);
    }


    /**
     * <p>
     * Generates a {@link UriTemplate} instance from the builder.
     * </p>
     *
     * @return the UriTemplate
     * @since 2.0
     */
    public UriTemplate build() throws MalformedUriTemplateException
    {
        UriTemplate template = new UriTemplate(this.components);
        if (this.values != null)
        {
            template.set(values);
        }

        if (defaultDateTimeFormatter != null)
        {
            template.defaultDateTimeFormatter = defaultDateTimeFormatter;
        }
        return template;
    }

    /**
     * Adds a variable name to the expression.
     * <p>
     * <pre>
     * var("foo");
     * </pre>
     * <p>
     * Will yield the following expression:
     * <pre>
     * {foo}
     * </pre>
     *
     * @param varName the varspec name
     * @return the {@link com.damnhandy.uri.template.impl.VarSpec} for the specified name
     */
    public static VarSpec var(String varName)
    {
        return var(varName, Modifier.NONE, null);
    }

    /**
     * Adds a variable name to the expression with an explode modifier.
     * <p>
     * <pre>
     * var("foo",true);
     * </pre>
     * <p>
     * Will yield the following expression:
     * <pre>
     * {foo*}
     * </pre>
     *
     * @param varName the varspec name
     * @param explode boolean to indicate that this VarSpec should use the explode modifer
     * @return the {@link com.damnhandy.uri.template.impl.VarSpec} for the specified name
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
     * <p>
     * <pre>
     * var("foo",2);
     * </pre>
     * <p>
     * Will yield the following expression:
     * <pre>
     * {foo:2}
     * </pre>
     *
     * @param varName the varspec name
     * @param prefix the prefix position
     * @return the {@link com.damnhandy.uri.template.impl.VarSpec} for the specified name
     */
    public static VarSpec var(String varName, int prefix)
    {
        return var(varName, Modifier.PREFIX, prefix);
    }

    /**
     * @param varName the varspec name
     * @param modifier the modifier
     * @param position the prefix position
     * @return the {@link com.damnhandy.uri.template.impl.VarSpec} for the specified name
     */
    private static VarSpec var(String varName, Modifier modifier, Integer position)
    {
        return new VarSpec(varName, modifier, position);
    }

}
