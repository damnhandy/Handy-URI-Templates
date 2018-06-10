/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template;

import com.damnhandy.uri.template.impl.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * <p>
 * This is the primary class for creating and manipulating URI templates. This project implements
 * <a href="http://tools.ietf.org/html/rfc6570">RFC6570 URI Templates</a> and produces output
 * that is compliant with the spec. The template processor supports <a href="http://tools.ietf.org/html/rfc6570#section-2.0">levels
 * 1 through 4</a>. In addition to supporting {@link Map}
 * and {@link List} values as composite types, the library also supports the use of Java objects
 * as well. Please see the {@link VarExploder} and {@link DefaultVarExploder} for more info.
 * </p>
 * <h3>Basic Usage:</h3>
 * <p>
 * There are many ways to use this library. The simplest way is to create a template from a
 * URI template  string:
 * </p>
 * <pre>
 * UriTemplate template = UriTemplate.fromTemplate("http://example.com/search{?q,lang}");
 * </pre>
 * <p>
 * Replacement values are added by calling the {@link #set(String, Object)} method on the template:
 * </p>
 * <pre>
 * template.set("q","cat")
 *         .set("lang","en");
 * String uri = template.expand();
 * </pre>
 * <p>The {@link #expand()} method will replace the variable names with the supplied values
 * and return the following URI:</p>
 * <pre>
 * http://example.com/search?q=cat&apm;lang=en
 * </pre>
 *
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public class UriTemplate implements Serializable
{


    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = -5245084430838445979L;

    public enum Encoding
    {
        U, UR;
    }

    public static final String DEFAULT_SEPARATOR = ",";

    /**
     *
     */
    transient DateTimeFormatter defaultDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * @deprecated Replaced by {@link #defaultDateTimeFormatter defaultDateTimeFormatter}
     */
    @Deprecated
    protected DateFormat defaultDateFormat = null;

    /**
     *
     */
    private static final char[] OPERATORS =
    {'+', '#', '.', '/', ';', '?', '&', '!', '='};

    /**
     *
     */
    private static final BitSet OPERATOR_BITSET = new BitSet();

    static
    {
        for (int i = 0; i < OPERATORS.length; i++)
        {
            OPERATOR_BITSET.set(OPERATORS[i]);
        }
    }

    /**
     * The URI template String
     */
    private String template;

    /**
     * A regex string that matches the a URI to the template pattern
     */
    private Pattern reverseMatchPattern;

    /**
     * The collection of values that will be applied to the URI expression in the
     * expansion process.
     */
    private Map<String, Object> values = new LinkedHashMap<String, Object>();

    /**
     *
     */
    private LinkedList<UriTemplateComponent> components;

    /**
     *
     */
    private List<Expression> expressions;

    /**
     *
     */
    private Set<String> variables;

    /**
     * Create a new UriTemplate.
     *
     * @param template
     * @throws MalformedUriTemplateException
     */
    private UriTemplate(final String template) throws MalformedUriTemplateException
    {
        this.template = template;
        this.parseTemplateString();
    }

    /**
     * Create a new UriTemplate.
     *
     * @param components
     */
    protected UriTemplate(LinkedList<UriTemplateComponent> components)
    {
        this.components = components;
        initExpressions();
        buildTemplateStringFromComponents();
    }

    /**
     * Creates a new {@link UriTemplateBuilder} instance.
     * @return the new UriTemplateBuilder
     * @since 2.1.2
     */
    public static UriTemplateBuilder createBuilder()
    {
        return new UriTemplateBuilder();
    }

    /**
     * Creates a new {@link UriTemplateBuilder} from the template string.
     *
     * @param template
     * @return
     * @throws MalformedUriTemplateException
     * @since 2.0
     */
    public static UriTemplateBuilder buildFromTemplate(String template) throws MalformedUriTemplateException
    {
        return new UriTemplateBuilder(template);
    }

    /**
     * <p>
     * Creates a new {@link UriTemplateBuilder} from a root {@link UriTemplate}. This
     * method will create a new {@link UriTemplate} from the base and copy the variables
     * from the base template to the new {@link UriTemplate}.
     * </p>
     * <p>
     * This method is useful when the base template is less volatile than the child
     * expression and you want to merge the two.
     * </p>
     *
     * @param baseTemplate
     * @return
     * @since 2.0
     */
    public static UriTemplateBuilder buildFromTemplate(UriTemplate baseTemplate) throws MalformedUriTemplateException
    {
        return new UriTemplateBuilder(baseTemplate);
    }

    /**
     * <p>
     * Creates a new {@link UriTemplate} from the template string.
     * </p>
     *
     * @param templateString
     * @return
     * @since 2.0
     */
    public static final UriTemplate fromTemplate(final String templateString) throws MalformedUriTemplateException
    {
        return new UriTemplate(templateString);
    }

    /**
     * <p>
     *   This method is now deprecated. Use {@link UriTemplate#buildFromTemplate(UriTemplate)} instead.
     * </p>
     *
     * @deprecated use {@link #buildFromTemplate(UriTemplate)}
     * @param baseTemplate
     * @return
     * @since 1.0
     */
    @Deprecated
    public static UriTemplateBuilder fromTemplate(UriTemplate baseTemplate) throws MalformedUriTemplateException
    {
        return new UriTemplateBuilder(baseTemplate);
    }

    /**
     * <p>
     * Returns the collection of {@link UriTemplateComponent} instances
     * found in this template.
     * </p>
     * @return the collection of {@link UriTemplateComponent}s
     */
    public Collection<UriTemplateComponent> getComponents() {
        return Collections.unmodifiableCollection(components);
    }

    /**
     * Returns the number of expressions found in this template
     *
     * @return
     */
    public int expressionCount()
    {
        return expressions.size();
    }

    /**
     * Returns an array of {@link Expression} instances found in this
     * template.
     *
     * @return
     */
    public Expression[] getExpressions()
    {
        return expressions.toArray(new Expression[expressions.size()]);
    }

    /**
     * Returns the list of unique variable names, from all {@link Expression}'s, in this template.
     *
     * @return
     */
    public String[] getVariables()
    {
        if (variables == null)
        {
            variables = new LinkedHashSet<String>();
            for (Expression e : getExpressions())
            {
                for (VarSpec v : e.getVarSpecs())
                {
                    variables.add(v.getVariableName());
                }
            }
        }
        return variables.toArray(new String[variables.size()]);
    }

    /**
     * Parse the URI template string into the template model.
     */
    protected void parseTemplateString() throws MalformedUriTemplateException
    {
        final String templateString = getTemplate();
        final UriTemplateParser scanner = new UriTemplateParser();
        this.components = scanner.scan(templateString);
        initExpressions();
    }

    /**
     * Initializes the collection of expressions in the template.
     */
    private void initExpressions()
    {
        expressions = new LinkedList<>();
        for (UriTemplateComponent c : components)
        {
            if (c instanceof Expression)
            {
                expressions.add((Expression) c);
            }
        }
    }


    private void buildTemplateStringFromComponents()
    {
        StringBuilder b = new StringBuilder();
        for (UriTemplateComponent c : components)
        {
            b.append(c.getValue());
        }
        this.template = b.toString();
    }

    private void buildReverseMatchRegexFromComponents()
    {
        StringBuilder b = new StringBuilder();
        for (UriTemplateComponent c : components)
        {
            b.append("(").append(c.getMatchPattern()).append(")");
        }
        this.reverseMatchPattern = Pattern.compile(b.toString());
    }

    /**
     * Returns the
     *
     * @return
     */
    protected Pattern getReverseMatchPattern()
    {
        if (this.reverseMatchPattern == null)
        {
            buildReverseMatchRegexFromComponents();
        }
        return this.reverseMatchPattern;
    }

    /**
     * Expands the given template string using the variable replacements
     * in the supplied {@link Map}.
     *
     * @param templateString
     * @param values
     * @return the expanded URI as a String
     * @throws MalformedUriTemplateException
     * @throws VariableExpansionException
     * @since 1.0
     */
    public static String expand(final String templateString, Map<String, Object> values)
    throws MalformedUriTemplateException, VariableExpansionException
    {
        UriTemplate t = new UriTemplate(templateString);
        t.set(values);
        return t.expand();
    }

    /**
     * Expands the given template string using the variable replacements
     * in the supplied {@link Map}. Expressions without replacements get
     * preserved and still exist in the expanded URI string.
     *
     * @param templateString URI template
     * @param values         Replacements
     * @return The expanded URI as a String
     * @throws MalformedUriTemplateException
     * @throws VariableExpansionException
     */
    public static String expandPartial(final String templateString, Map<String, Object> values)
    throws MalformedUriTemplateException, VariableExpansionException
    {
        UriTemplate t = new UriTemplate(templateString);
        t.set(values);
        return t.expandPartial();
    }

    /**
     * Expand the URI template using the supplied values
     *
     * @param vars The values that will be used in the expansion
     * @return the expanded URI as a String
     * @throw VariableExpansionException
     * @since 1.0
     */
    public String expand(Map<String, Object> vars) throws VariableExpansionException
    {
        this.values = vars;
        return expand();
    }


    /**
     * Applies variable substitution the URI Template and returns the expanded
     * URI.
     *
     * @return the expanded URI as a String
     * @throw VariableExpansionException
     * @since 1.0
     */
    public String expand() throws VariableExpansionException
    {
        String template = getTemplate();
        for (Expression expression : expressions)
        {
            final String replacement = expressionReplacementString(expression, false);
            template = template.replaceAll(expression.getReplacementPattern(), replacement);
        }
        return template;
    }

    /**
     * @return
     * @throws VariableExpansionException
     */
    public String expandPartial() throws VariableExpansionException
    {
        String template = getTemplate();
        for (Expression expression : expressions)
        {
            final String replacement = expressionReplacementString(expression, true);
            template = template.replaceAll(expression.getReplacementPattern(), replacement);
        }
        return template;
    }

    /**
     * Returns the original URI template expression.
     *
     * @return the template string
     * @since 1.1.4
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * Returns the collection of name/value pairs contained in the instance.
     *
     * @return the name value pairs
     * @since 1.0
     */
    public Map<String, Object> getValues()
    {
        return this.values;
    }

    /**
     * @param dateFormatString
     * @return the date format used to render dates
     * @since 1.0
     */
    public UriTemplate withDefaultDateFormat(String dateFormatString)
    {
        return this.withDefaultDateFormat(DateTimeFormat.forPattern(dateFormatString));
    }

    private UriTemplate withDefaultDateFormat(DateTimeFormatter dateTimeFormatter)
    {
        defaultDateTimeFormatter = dateTimeFormatter;
        return this;
    }

    /**
     * @param dateFormat
     * @return the date format used to render dates
     * @since 1.0
     * @deprecated replaced by {@link #withDefaultDateFormat(String) withDefaultDateFormat}
     */
    @Deprecated
    public UriTemplate withDefaultDateFormat(DateFormat dateFormat)
    {
        if (!(dateFormat instanceof SimpleDateFormat))
        {
            throw new IllegalArgumentException(
            "The only supported subclass of java.text.DateFormat is java.text.SimpleDateFormat");
        }
        defaultDateTimeFormatter = DateTimeFormat.forPattern(((SimpleDateFormat) dateFormat).toPattern());
        return this;
    }

    /**
     * Sets a value on the URI template expression variable.
     *
     * @param variableName
     * @param value
     * @return
     * @since 1.0
     */
    public UriTemplate set(String variableName, Object value)
    {
        values.put(variableName, value);
        return this;
    }

    /**
     * Returns true if the {@link UriTemplate} contains the variableName.
     *
     * @param variableName
     * @return
     */
    public boolean hasVariable(String variableName)
    {
        return values.containsKey(variableName);
    }

    /**
     * FIXME Comment this
     *
     * @param variableName
     * @return
     */
    public Object get(String variableName)
    {
        return values.get(variableName);
    }

    /**
     * Sets a Date value into the list of variable substitutions using the
     * default {@link DateFormat}.
     *
     * @param variableName
     * @param value
     * @return
     * @since 1.0
     */
    public UriTemplate set(String variableName, Date value)
    {
        values.put(variableName, value);
        return this;
    }

    /**
     * Adds the name/value pairs in the supplied {@link Map} to the collection
     * of values within this URI template instance.
     *
     * @param values
     * @return
     * @since 1.0
     */
    public UriTemplate set(Map<String, Object> values)
    {
        if (values != null && !values.isEmpty())
        {
            this.values.putAll(values);
        }
        return this;
    }

    /**
     * @param op
     * @return
     */
    public static boolean containsOperator(String op)
    {
        return OPERATOR_BITSET.get(op.toCharArray()[0]);
    }


    /**
     * @param expression
     * @param partial
     * @return
     * @throws VariableExpansionException
     */
    private String expressionReplacementString(Expression expression, boolean partial) throws VariableExpansionException
    {
        final Operator operator = expression.getOperator();
        final List<String> replacements = expandVariables(expression, partial);
        String result = partial ? joinParts(expression, replacements) : joinParts(operator.getSeparator(), replacements);
        if (result != null)
        {
            if (!partial && operator != Operator.RESERVED)
            {
                result = operator.getPrefix() + result;
            }
        }
        else
        {
            result = "";
        }
        return result;
    }

    /**
     * @param expression
     * @param partial
     * @return
     * @throws VariableExpansionException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<String> expandVariables(Expression expression, boolean partial) throws VariableExpansionException
    {
        final List<String> replacements = new ArrayList<String>();
        final Operator operator = expression.getOperator();
        for (VarSpec varSpec : expression.getVarSpecs())
        {
            if (values.containsKey(varSpec.getVariableName()))
            {
                Object value = values.get(varSpec.getVariableName());
                // The expanded value
                String expanded = null;

                if (value != null)
                {
                    if (value.getClass().isArray())
                    {
                        if (value instanceof char[][])
                        {
                            final char[][] chars = (char[][]) value;
                            final List<String> strings = new ArrayList<String>();
                            for (char[] c : chars)
                            {
                                strings.add(String.valueOf(c));
                            }
                            value = strings;
                        }
                        else if (value instanceof char[])
                        {
                            value = String.valueOf((char[]) value);
                        }
                        else
                        {
                            value = arrayToList(value);
                        }

                    }
                }
                // Check to see if the value is explodable, meaning that we need to pass it to VarExploder to
                // decompose the object to simple key/value pairs. We don't handle prefix modifiers on composite values.
                final boolean explodable = isExplodable(value);
                if (explodable && varSpec.getModifier() == Modifier.PREFIX)
                {
                    throw new VariableExpansionException(
                    "Prefix modifiers are not applicable to variables that have composite values.");
                }
                // If it's explodable, lookup the appropriate exploder
                if (explodable)
                {
                    final VarExploder exploder;
                    if (value instanceof VarExploder)
                    {
                        exploder = (VarExploder) value;
                    }
                    else
                    {
                        exploder = VarExploderFactory.getExploder(value, varSpec);
                    }
                    if (varSpec.getModifier() == Modifier.EXPLODE)
                    {
                        expanded = expandMap(operator, varSpec, exploder.getNameValuePairs());
                    }
                    else if (varSpec.getModifier() != Modifier.EXPLODE)
                    {
                        expanded = expandCollection(operator, varSpec, exploder.getValues());
                    }
                }

                /*
                 * Format the date if we have a java.util.Date
                 */
                if (value instanceof Date)
                {
                    value = defaultDateTimeFormatter.print(new DateTime((Date) value));
                }
                /*
                 * The variable value contains a list of values
                 */
                if (value instanceof Collection)
                {
                    expanded = this.expandCollection(operator, varSpec, (Collection) value);
                }
                /*
                 * The variable value contains a list of key-value pairs
                 */
                else if (value instanceof Map)
                {
                    expanded = expandMap(operator, varSpec, (Map) value);
                }
                /*
                 * The variable value is null or has o value.
                 */
                else if (value == null)
                {
                    expanded = null;
                }
                /*
                 * the value hasn't been expanded yet and we should call toString() on it.
                 */
                else if (expanded == null)
                {
                    expanded = this.expandStringValue(operator, varSpec, value.toString(), VarSpec.VarFormat.SINGLE);
                }
                if (expanded != null)
                {
                    replacements.add(expanded);
                }

            }
            else if (partial)
            {
                replacements.add(null);
            }
        }
        return replacements;
    }

    /**
     * @param value
     * @return
     */
    private boolean isExplodable(Object value)
    {
        if (value == null)
        {
            return false;
        }
        if (value instanceof Collection || value instanceof Map || value.getClass().isArray())
        {
            return true;
        }
        if (!isSimpleType(value))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true of the object is:
     * <p>
     * <ul>
     * <li>a primitive type</li>
     * <li>an enum</li>
     * <li>an instance of {@link CharSequence}</li>
     * <li>an instance of {@link Number} <li>
     * <li>an instance of {@link Date} <li>
     * <li>an instance of {@link Boolean}</li>
     * <li>an instance of {@link UUID}</li>
     * <li>an instance of {@link Class}</li>
     * </ul>
     *
     * @param value
     * @return
     */
    private boolean isSimpleType(Object value)
    {

        if (value.getClass().isPrimitive()
        || value.getClass().isEnum()
        || value instanceof Class
        || value instanceof Number
        || value instanceof CharSequence
        || value instanceof Date
        || value instanceof Boolean
        || value instanceof UUID
        )
        {
            return true;
        }
        return false;
    }

    /**
     * @param operator
     * @param varSpec
     * @param variable
     * @return
     */
    private String expandCollection(Operator operator, VarSpec varSpec, Collection<?> variable)
    throws VariableExpansionException
    {

        if (variable == null || variable.isEmpty())
        {
            return null;
        }

        final List<String> stringValues = new ArrayList<String>();
        final Iterator<?> i = variable.iterator();
        String separator = operator.getSeparator();
        if (varSpec.getModifier() != Modifier.EXPLODE)
        {
            separator = operator.getListSeparator();
        }
        while (i.hasNext())
        {
            final Object obj = i.next();
            checkValue(obj);
            String value;
            if(checkValue(obj))
            {
                value = joinParts(",", obj);
            }
            else
            {
                if(isSimpleType(obj))
                {
                    value = obj.toString();
                }
                else
                {
                    throw new VariableExpansionException("Collections or other complex types are not supported in collections.");
                }
            }

            stringValues.add(expandStringValue(operator, varSpec, value, VarSpec.VarFormat.ARRAY));
        }

        if (varSpec.getModifier() != Modifier.EXPLODE && operator.useVarNameWhenExploded())
        {
            final String parts = joinParts(separator, stringValues);
            if (operator == Operator.QUERY && parts == null)
            {
                return varSpec.getVariableName() + "=";
            }
            return varSpec.getVariableName() + "=" + parts;
        }
        return joinParts(separator, stringValues);
    }

    /**
     * Check to ensure that the values being passed down do not contain nested data structures.
     *
     * @param obj
     */
    private boolean checkValue(Object obj) throws VariableExpansionException
    {
        if (obj instanceof Map)
        {
            throw new VariableExpansionException("Nested data structures are not supported.");
        }

        if (obj instanceof Collection || obj.getClass().isArray())
        {
            return true;
        }
        return false;
    }

    /**
     * @param operator
     * @param varSpec
     * @param variable
     * @return
     */
    private String expandMap(Operator operator, VarSpec varSpec, Map<String, Object> variable)
    throws VariableExpansionException
    {
        if (variable == null || variable.isEmpty())
        {
            return null;
        }

        List<String> stringValues = new ArrayList<String>();
        String pairJoiner = "=";
        if (varSpec.getModifier() != Modifier.EXPLODE)
        {
            pairJoiner = ",";
        }
        String joiner = operator.getSeparator();
        if (varSpec.getModifier() != Modifier.EXPLODE)
        {
            joiner = operator.getListSeparator();
        }
        for (Entry<String, Object> entry : variable.entrySet())
        {

            String key = entry.getKey();
            String value;
            if(checkValue(entry.getValue()))
            {
                value = joinParts(",", entry.getValue());
            }
            else
            {
                if(isSimpleType(entry.getValue()))
                {
                    value = entry.getValue().toString();
                }
                else
                {
                    throw new VariableExpansionException("Collections or other complex types are not supported in collections.");
                }
            }

            String pair = expandStringValue(operator, varSpec, key, VarSpec.VarFormat.PAIRS) + pairJoiner
            + expandStringValue(operator, varSpec, value, VarSpec.VarFormat.PAIRS);

            stringValues.add(pair);
        }

        if (varSpec.getModifier() != Modifier.EXPLODE
        && (operator == Operator.MATRIX || operator == Operator.QUERY || operator == Operator.CONTINUATION))
        {
            String joinedValues = joinParts(joiner, stringValues);
            if (operator == Operator.QUERY && joinedValues == null)
            {
                return varSpec.getVariableName() + "=";
            }
            return varSpec.getVariableName() + "=" + joinedValues;
        }

        return joinParts(joiner, stringValues);
    }

    /**
     * This method performs the expansion on the string value being applied to the output URI. The rules for exapnasion
     * depends heavily on the {@link Operator} in use. The {@link Operator} will dictate the URI encoding rules that
     * will be applied to the string.
     *
     *
     * @param operator
     * @param varSpec
     * @param variable
     * @param format
     * @return
     */
    private String expandStringValue(Operator operator, VarSpec varSpec, String variable, VarSpec.VarFormat format)
    throws VariableExpansionException
    {
        String expanded;

        if (varSpec.getModifier() == Modifier.PREFIX)
        {
            int position = varSpec.getPosition();
            if (position < variable.length())
            {
                variable = variable.substring(0, position);
            }
        }

        try
        {
            // If we have a {+} or {#} operator, there are items we do not need to encode.
            if (operator.getEncoding() == Encoding.UR)
            {
                expanded = UriUtil.encodeFragment(variable);
            }
            else
            {
                expanded = UriUtil.encode(variable);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new VariableExpansionException("Could not expand variable due to a problem URI encoding the value.", e);
        }

        if (operator.isNamed())
        {
            if (expanded.isEmpty() && !"&".equals(operator.getSeparator()) )
            {
                expanded = varSpec.getValue();
            }
            else if (format == VarSpec.VarFormat.SINGLE)
            {
                expanded = varSpec.getVariableName() + "=" + expanded;
            }

            else
            {
                if (varSpec.getModifier() == Modifier.EXPLODE &&
                    operator.useVarNameWhenExploded() &&
                    format != VarSpec.VarFormat.PAIRS)
                    {
                        expanded = varSpec.getVariableName() + "=" + expanded;
                    }

            }
        }
        return expanded;
    }


    private String joinParts(final String joiner, Object parts)
    {
        if(parts instanceof Collection)
        {
            Collection<String> values = (Collection)parts;
            List<String> v = new ArrayList<String>();
            for(String s : values)
            {
                v.add(s);
            }
            return joinParts(joiner,v);
        }
        else if (parts.getClass().isArray())
        {
            List<String> v = Arrays.asList((String[]) parts);
            return joinParts(joiner,v);
        }
        return null;

    }
    /**
     * @param joiner
     * @param parts
     * @return
     */
    private String joinParts(final String joiner, List<String> parts)
    {
        if (parts.isEmpty())
        {
            return null;
        }

        if (parts.size() == 1)
        {
            return parts.get(0);
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++)
        {
            final String part = parts.get(i);
            if (!part.isEmpty())
            {
                builder.append(part);
                if (parts.size() > 0 && i != (parts.size() - 1))
                {
                    builder.append(joiner);
                }
            }

        }
        return builder.toString();
    }

   /**
    * Joins parts by preserving expressions without values.
    * @param expression Expression for the given parts
    * @param parts Parts to join
    * @return Joined parts
    */
   private String joinParts(final Expression expression, List<String> parts)
   {

      int[] index = getIndexForPartsWithNullsFirstIfQueryOrRegularSequnceIfNot(expression, parts);

      List<String> replacedParts = new ArrayList<String>(parts.size());
      for(int i = 0; i < parts.size(); i++) {
         StringBuilder builder = new StringBuilder();
         if(parts.get(index[i]) == null)
         {
            builder.append('{');
            while(i < parts.size() && parts.get(index[i]) == null)
            {
               if(builder.length() == 1)
               {
                  builder.append(replacedParts.size() == 0 ? expression.getOperator().getPrefix() : expression.getOperator().getSeparator());
               }
               else
               {
                  builder.append(DEFAULT_SEPARATOR);
               }
               builder.append(expression.getVarSpecs().get(index[i]).getValue());
               i++;
            }
            i--;
            builder.append('}');
         } else {
           if(expression.getOperator() != Operator.RESERVED) {
            builder.append(replacedParts.size() == 0 ? expression.getOperator().getPrefix() : expression.getOperator().getSeparator());
           }
           builder.append(parts.get(index[i]));
         }
         replacedParts.add(builder.toString());
      }
      return joinParts("", replacedParts);
   }

   /**
    * Takes an array of objects and converts them to a {@link List}.
    *
    * @param array
    * @return
    */
   private List<Object> arrayToList(Object array) throws VariableExpansionException
   {
      List<Object> list = new ArrayList<Object>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++)
      {
         final Object element = Array.get(array, i);
         if (element.getClass().isArray())
         {
            throw new VariableExpansionException("Multi-dimenesional arrays are not supported.");
         }
         list.add(element);
      }
      return list;
   }

   /**
    * Takes the expression and the parts and generate a index with null value parts pulled to the start and
    * the not null value parts pushed to the end. Ex:
    * ["var3",null,"var1",null] will generate the following index:
    * [1,3,0,2]
    * @param expression
    * @param parts
    * @return
    */
   private int[] getIndexForPartsWithNullsFirstIfQueryOrRegularSequnceIfNot(final Expression expression, List<String> parts)
   {
      int[] index = new int[parts.size()];

      int inverse, forward = 0, backward = parts.size() - 1;
      for (int i = 0; i < parts.size(); i++) {
         if (expression.getOperator() == Operator.QUERY)
         {
            inverse = parts.size() - i - 1;
            if (parts.get(i) != null)
            {
               index[forward++] = i;
            }
            if (parts.get(inverse) == null)
            {
               index[backward--] = inverse;
            }
         }
         else
         {
            index[i] = i;
         }
      }
      return index;
   }




   /**
    *
    *
    * @return
    */
//   public String getRegexString()
//   {
//      return null;
//   }
}
