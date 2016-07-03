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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * A light-weight utility class for applying encoding to values that are applied to
 * expression values.
 * </p>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class UriUtil
{
    static final Pattern PCT_ENCODDED_STRING = Pattern.compile("%[0-9A-Fa-f]{2}");

    static final char[] GENERAL_DELIM_CHARS = {':', '/', ',', '?', '#', '[', ']', '@'};

    static final char[] SUB_DELIMS_CHARS = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '<', '>', '{', '}'};

    private static final BitSet RESERVED;

    private static final BitSet ESCAPE_CHARS;

    static
    {

        RESERVED = new BitSet();
        for (int i = 0; i < GENERAL_DELIM_CHARS.length; i++)
        {
            RESERVED.set(GENERAL_DELIM_CHARS[i]);
        }
        RESERVED.set(' ');
        RESERVED.set('%');
        RESERVED.set('|');
        RESERVED.set('\\');
        RESERVED.set('`');
        RESERVED.set('"');
        RESERVED.set('^');

        for (int i = 0; i < SUB_DELIMS_CHARS.length; i++)
        {
            RESERVED.set(SUB_DELIMS_CHARS[i]);
        }

        ESCAPE_CHARS = new BitSet();
        ESCAPE_CHARS.set('<');
        ESCAPE_CHARS.set('>');
        ESCAPE_CHARS.set('%');
        ESCAPE_CHARS.set('\"');
        ESCAPE_CHARS.set('{');
        ESCAPE_CHARS.set('}');
        ESCAPE_CHARS.set('|');
        ESCAPE_CHARS.set('\\');
        ESCAPE_CHARS.set('^');
        ESCAPE_CHARS.set('[');
        ESCAPE_CHARS.set(']');
        ESCAPE_CHARS.set('`');
    }

    private UriUtil()
    {

    }

    /**
     * @param sourceValue
     * @return the encoded string
     */
    public static String encodeFragment(String sourceValue) throws UnsupportedEncodingException
    {
        // Check if the string has %-encoded values already.
        // if it does, rebuild the string and encode the non-encoded bit
        // but don't re-encode the already encoded strings.
        //
        // There's probably a cleaner way to do this.
        Matcher m = PCT_ENCODDED_STRING.matcher(sourceValue);
        List<int[]> positions = new ArrayList<int[]>();
        while (m.find())
        {
            positions.add(new int[]{m.start(), m.end()});
        }
        if(!positions.isEmpty())
        {
            StringBuilder b = new StringBuilder();
            int offset = 0;
            for (int[] pos : positions)
            {
                // encode the non-encoded portion of the string
                b.append(UriUtil.encode(sourceValue.substring(offset, pos[0]),ESCAPE_CHARS));
                // the already encodede string does not get encoded twice
                b.append(sourceValue.substring(pos[0], pos[1]));
                offset = pos[1];
            }
            b.append(encode(sourceValue.substring(offset, sourceValue.length()),ESCAPE_CHARS));
            return b.toString();
        }
        // If there's
        return encode(sourceValue, ESCAPE_CHARS);
    }

    /**
     * @param sourceValue
     * @return the encoded string
     */
    public static String encode(String sourceValue) throws UnsupportedEncodingException
    {
        return encode(sourceValue, RESERVED);
    }

    /**
     * @param sourceValue
     * @param chars
     * @return the encoded string
     * @throws UnsupportedEncodingException
     */
    private static String encode(String sourceValue, BitSet chars) throws UnsupportedEncodingException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] source = sourceValue.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < source.length; i++)
        {
            byte c = source[i];
            // fixed unsigned problem
            if (chars.get(c & 0xff) || c <= 0x20)
            {
                out.write('%');
                char hex1 = Character.toUpperCase(Character.forDigit((c >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(c & 0xF, 16));
                out.write(hex1);
                out.write(hex2);
            }
            else
            {
                out.write(c);
            }
        }
        return new String(out.toByteArray(), "UTF-8");
    }

}
