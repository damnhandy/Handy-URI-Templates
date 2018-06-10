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


import com.damnhandy.uri.template.impl.Modifier;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * An Expression represents the text between '{' and '}', including the enclosing
 * braces, as defined in <a href="ietf.org/html/rfc6570#section-2">Section 2 of RFC6570</a>.
 * </p>
 * <pre>
 * http://www.example.com/foo{?query,number}
 *                            \___________/
 *                                  ^
 *                                  |
 *                                  |
 *                            The expression
 * </pre>
 * <p>
 * This class models this representation and adds helper functions for replacement and reverse matching.
 * </p>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 * @since 2.0
 */
public class Expression extends UriTemplateComponent
{

    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = -5305648325957481840L;

    /**
     * A regex quoted string that matches the expression for replacement in the
     * expansion process. For example:
     * <pre>
     *          \Q{?query,number}\E
     * </pre>
     */
    private String replacementPattern;

    /**
     * That {@link Operator} value that is associated with this Expression
     */
    private Operator op;

    /**
     * The character position where this expression occurs in the URI template
     */
    private final int location;
    /**
     * The the parsed {@link VarSpec} instances found in the expression.
     */
    private List<VarSpec> varSpecs;

    /**
     * The Patter that would be used to reverse match this expression
     */
    private Pattern matchPattern;


    /**
     * Creates a new {@link Builder} to create a simple expression according
     * to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.2">3.2.2</a>.
     * Calling:
     * <pre>
     * String exp = Expression.simple(var("var")).build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {var}
     * </pre>
     *
     * @return
     */
    public static Builder simple(VarSpec... varSpec)
    {
        return Builder.create(Operator.NUL, varSpec);
    }


    /**
     * Creates a new {@link Builder} to create an expression that will use reserved expansion
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.3">3.2.3</a>.
     * Calling:
     * <pre>
     * String exp = Expression.reserved().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {+var}
     * </pre>
     *
     * @return
     */
    public static Builder reserved(VarSpec... varSpec)
    {
        return Builder.create(Operator.RESERVED, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression with a fragment operator
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.4">3.2.4</a>.
     * Calling:
     * <pre>
     * String exp = Expression.fragment().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {#var}
     * </pre>
     *
     * @return
     */
    public static Builder fragment(VarSpec... varSpec)
    {
        return Builder.create(Operator.FRAGMENT, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression using label expansion
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.5">3.2.5</a>.
     * Calling:
     * <pre>
     * String exp = Expression.label(var("var")).build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {.var}
     * </pre>
     *
     * @return
     */
    public static Builder label(VarSpec... varSpec)
    {
        return Builder.create(Operator.NAME_LABEL, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression using path expansion
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.6">3.2.6</a>.
     * Calling:
     * <pre>
     * String exp = Expression.path().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {/var}
     * </pre>
     *
     * @return
     */
    public static Builder path(VarSpec... varSpec)
    {
        return Builder.create(Operator.PATH, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression using path-style parameter
     * (a.k.a. matrix parameter) expansion according to
     * section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.7">3.2.7</a>.
     * Calling:
     * <pre>
     * String exp = Expression.matrix().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {;var}
     * </pre>
     *
     * @return the builder
     */
    public static Builder matrix(VarSpec... varSpec)
    {
        return Builder.create(Operator.MATRIX, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression using form-style query string expansion
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.8">3.2.8</a>.
     * Calling:
     * <pre>
     * String exp = Expression.query().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {?var}
     * </pre>
     *
     * @return
     */
    public static Builder query(VarSpec... varSpec)
    {
        return Builder.create(Operator.QUERY, varSpec);
    }

    /**
     * Creates a new {@link Builder} to create an expression using form-style query continuation expansion
     * according to section <a href="http://tools.ietf.org/html/rfc6570#section-3.2.9">3.2.9</a>.
     * Calling:
     * <pre>
     * String exp = Expression.continuation().var("var").build().toString();
     * </pre>
     * <p>
     * Will return the following expression:
     * </p>
     * <pre>
     * {&amp;var}
     * </pre>
     *
     * @return
     */
    public static Builder continuation(VarSpec... varSpec)
    {
        return Builder.create(Operator.CONTINUATION, varSpec);
    }


    /**
     * Create a new Expression.
     *
     * @param rawExpression
     * @param startPosition
     * @throws MalformedUriTemplateException
     */
    public Expression(final String rawExpression, final int startPosition) throws MalformedUriTemplateException
    {
        super(startPosition);
        this.location = startPosition;
        this.parseRawExpression(rawExpression);
    }

    /**
     * Create a new Expression
     *
     * @param op
     * @param varSpecs
     * @throws MalformedUriTemplateException
     */
    public Expression(final Operator op, final List<VarSpec> varSpecs)
    {
        super(0);
        this.op = op;
        this.varSpecs = varSpecs;
        this.replacementPattern = Pattern.quote(toString());
        this.location = 0;
    }

    /**
     * @param rawExpression
     * @throws MalformedUriTemplateException
     */
    private void parseRawExpression(String rawExpression) throws MalformedUriTemplateException
    {
        final String expressionReplacement = Pattern.quote(rawExpression);
        String token = rawExpression.substring(1, rawExpression.length() - 1);
        // Check for URI operators
        Operator operator = Operator.NUL;
        String firstChar = token.substring(0, 1);
        if (UriTemplate.containsOperator(firstChar))
        {
            try
            {
                operator = Operator.fromOpCode(firstChar);
            }
            catch (IllegalArgumentException e)
            {
                throw new MalformedUriTemplateException("Invalid operator", this.location, e);
            }
            token = token.substring(1, token.length());
        }
        String[] varspecStrings = token.split(",");
        List<VarSpec> varspecs = new ArrayList<VarSpec>();

        for (String varname : varspecStrings)
        {
            int subStrPos = varname.indexOf(Modifier.PREFIX.getValue());
         /*
          * Prefix variable
          */
            if (subStrPos > 0)
            {
                String[] pair = varname.split(Modifier.PREFIX.getValue());
                try
                {
                    Integer pos = Integer.valueOf(varname.substring(subStrPos + 1));
                    varspecs.add(new VarSpec(pair[0], Modifier.PREFIX, pos));
                }
                catch (NumberFormatException e)
                {
                    throw new MalformedUriTemplateException("The prefix value for " + pair[0] + " was not a number", this.location, e);
                }
            }

         /*
          * Variable will be exploded
          */
            else if (varname.lastIndexOf(Modifier.EXPLODE.getValue()) > 0)
            {
                varspecs.add(new VarSpec(varname, Modifier.EXPLODE));
            }
         /*
          * Standard Value
          */
            else
            {
                varspecs.add(new VarSpec(varname, Modifier.NONE));
            }
        }
        this.replacementPattern = expressionReplacement;
        this.op = operator;
        this.varSpecs = varspecs;
    }




    private Pattern buildMatchingPattern()
    {
        StringBuilder b = new StringBuilder();
        for (VarSpec v : getVarSpecs())
        {
            b.append("(?<").append(v.getVariableName()).append(">[^\\/]+)");
        }
        return Pattern.compile(b.toString());
    }

    /**
     * Returns a string that contains a regular expression that matches the
     * URI template expression.
     *
     * @return the match pattern
     */
    @Override
    public Pattern getMatchPattern()
    {
        if (this.matchPattern == null)
        {
            this.matchPattern = buildMatchingPattern();
        }
        return this.matchPattern;
    }

    /**
     * Get the replacementToken.
     *
     * @return the replacementToken.
     */
    public String getReplacementPattern()
    {
        return replacementPattern;
    }

    /**
     * Get the {@link Operator} value for this expression.
     *
     * @return the operator value.
     */
    public Operator getOperator()
    {
        return op;
    }

    /**
     * Get the varSpecs.
     *
     * @return the varSpecs.
     */
    public List<VarSpec> getVarSpecs()
    {
        return varSpecs;
    }

    /**
     * Returns the string representation of the expression.
     *
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("{").append(this.getOperator().getOperator());
        for (int i = 0; i < varSpecs.size(); i++)
        {
            VarSpec v = varSpecs.get(i);
            b.append(v.getValue());
            // Double check that the value doesn't already contain the modifier
            int r = v.getValue().lastIndexOf(v.getModifier().getValue());
            if(v.getModifier() != null && v.getValue().lastIndexOf(v.getModifier().getValue()) == -1)
            {
                b.append(v.getModifier().getValue());
            }

            if (v.getModifier() == Modifier.PREFIX)
            {
                b.append(v.getPosition());
            }
            if (i != (varSpecs.size() - 1))
            {
                b.append(",");
            }
        }
        return b.append("}").toString();
    }

    /**
     * Returns the value of this component
     *
     * @return the string value of this component.
     */
    @Override
    public String getValue()
    {
        return toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((op == null) ? 0 : op.hashCode());
        result = prime * result + ((varSpecs == null) ? 0 : varSpecs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Expression other = (Expression) obj;
        if (op != other.op)
        {
            return false;
        }
        if (varSpecs == null)
        {
            if (other.varSpecs != null)
            {
                return false;
            }
        }
        else if (!varSpecs.equals(other.varSpecs))
        {
            return false;
        }
        return true;
    }


    /**
     * Builder class for creating an {@link Expression}.
     *
     * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
     * @version $Revision: 1.1 $
     */
    public static class Builder
    {
        /**
         *
         */
        private Operator operator;

        /**
         *
         */
        private List<VarSpec> varSpecs;

        /**
         * Create a new ExpressionBuilder.
         *
         * @param operator
         */
        private Builder(Operator operator, VarSpec... varSpec)
        {
            this.operator = operator;
            this.varSpecs = new ArrayList<VarSpec>();
            for (VarSpec v : varSpec)
            {
                varSpecs.add(v);
            }
        }

        /**
         * @param operator
         * @return the builder
         */
        static Builder create(Operator operator, VarSpec... varSpec)
        {
            return new Builder(operator, varSpec);
        }


        public Expression build()
        {
            return new Expression(operator, varSpecs);
        }
    }

}
