/*
 * 
 */
package com.damnhandy.uri.template;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * <p>
 * A light-weight utility class for applying encoding to values that are applied to
 * expression values.
 * </p>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public final class UriUtil {
    private static final BitSet GENERAL_ALLOWED_CHARS = new BitSet();

    private static final BitSet FRAGMENT_ALLOWED_CHARS = new BitSet();

    private static final BitSet RESERVED_ALLOWED_CHARS = new BitSet();

    static {
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String digit = "0123456789";

        /**
         * The unreserved chars.
         *
         * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
         */
        String unreserved = alpha + digit + "-._~";

        /**
         * The general delimiters.
         *
         * gen-delims    = ":" / "/" / "?" / "#" / "[" / "]" / "@"
         */
        String gendelims = ":/?#[]@";

        /**
         * The sub delimiters.
         *
         * sub-delims     = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
         */
        String subdelims = "!$&'()*+,;=";

        /**
         * The reserved chars in URI spec.
         *
         * reserved      = gen-delims / sub-delims
         */
        String reserved = subdelims + gendelims;


        /**
         * Allowed chars in Fragments (#).
         * See: http://tools.ietf.org/html/rfc3986#appendix-A
         *
         * The definition in http://tools.ietf.org/html/rfc6570#section-3.2.1 (URI-Template-RFC) is not correct!
         * Not all characters(#[]) in GEN_DELIM are allowed here!
         *
         * fragment    = *( pchar / "/" / "?" )
         * pchar       = unreserved / sub-delims / ":" / "@"
         */
         String fragment = unreserved+subdelims+":@/?";

        add(GENERAL_ALLOWED_CHARS, unreserved);
        add(FRAGMENT_ALLOWED_CHARS, fragment);

        /**
         * This is a special uri encoding for Uri-Templates, allowing Reserved and Unreserved chars.
         */
        add(RESERVED_ALLOWED_CHARS, unreserved);
        add(RESERVED_ALLOWED_CHARS, reserved);
    }

    private static void add(BitSet destination, String toAdd) {

        for (char character : toAdd.toCharArray()) {
            if (character >= 127) {
                throw new IllegalArgumentException("Bitset only works correct with one "
                        + "byte");
            }
            destination.set(character);
        }
    }

    private UriUtil() {

    }

    /**
     * Encodes fragments, allowing only the characters defined in uri fragments(http://tools.ietf
     * .org/html/rfc3986#appendix-A). This are a few less then in the reserved expression.
     *
     * @param sourceValue should not contain a PCT encoded string.
     * @return the encoded value
     */
    public static String encodeFragment(String sourceValue) {

        return encode(sourceValue, FRAGMENT_ALLOWED_CHARS);
    }

    /**
     * Encodes the reserved expression, allowing all reserved and unreserved characters.
     *
     * @param sourceValue should not contain a PCT encoded string.
     * @return the encoded value
     */
    public static String encodeReserved(String sourceValue) {

        return encode(sourceValue, RESERVED_ALLOWED_CHARS);
    }

    /**
     * Encodes general uri expressions, allowing only unreserved characters.
     *
     * @param sourceValue should not contain a PCT encoded string.
     * @return the encoded value
     */
    public static String encode(String sourceValue) {

        return encode(sourceValue, GENERAL_ALLOWED_CHARS);
    }

    /**
     * Uses a white list to encode all characters that are not contained in this list.
     */
    private static String encode(String sourceValue, BitSet allowedCharacters) {

        ByteArrayOutputStream out = new ByteArrayOutputStream(sourceValue.length());
        Charset utf8 = Charset.forName("UTF-8");
        byte[] source = sourceValue.getBytes(utf8);
        for (int i = 0; i < source.length; i++) {
            byte c = source[i];
            // fixed unsigned problem
            if (allowedCharacters.get(c & 0xff)) {
                out.write(c);

            } else {
                out.write('%');
                char hex1 = Character.toUpperCase(Character.forDigit((c >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(c & 0xF, 16));
                out.write(hex1);
                out.write(hex2);
            }
        }
        return new String(out.toByteArray(), utf8);
    }

}
