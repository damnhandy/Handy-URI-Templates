package com.damnhandy.uri.template;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class UriUtilTest {

    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGIT = "0123456789";
    private static final String UNRESERVED = ALPHA + DIGIT + "-._~";
    private static final String SUBDELIMS = "!$&'()*+,;=";
    private static final String GENDELIMS = ":/?#[]@";


    private void validateFragment(String excpected, String input){
        assertEquals(excpected, UriUtil.encodeFragment(input));

    }
    private void validate(String excpected, String input){
        assertEquals(excpected, UriUtil.encode(input));

    }

    private void validateReserved(String excpected, String input){
        assertEquals(excpected, UriUtil.encodeReserved(input));

    }
    @Test
    public void generalNothingToEncode() throws Exception {
        validate(UNRESERVED, UNRESERVED);

    }

    @Test
    public void fragmentNothingToEncode() throws Exception {
        validateFragment(UNRESERVED+SUBDELIMS+"/?:@", UNRESERVED+SUBDELIMS+"/?:@");
    }

    @Test
    public void reservedNothingToEncode() throws Exception {
        validateReserved(UNRESERVED+SUBDELIMS+GENDELIMS, UNRESERVED+SUBDELIMS+GENDELIMS);
    }

    @Test
    public void fragmentWithCharsToEncodeInFragmentButNotInReserved() throws Exception {
        validateFragment("%23%5B%5D", "#[]");

    }

    @Test
    public void fragmentWithSomeCharsToEncode() throws Exception {
        validateFragment("%C3%A4%C3%B6", "äö");

    }

    @Test
    public void reservedWithSomeCharsToEncode() throws Exception {
        validateReserved("%C3%A4%C3%B6", "äö");

    }


    @Test
    public void generalWithSomeCharsToEncode() throws Exception {

        assertEquals("XX%C3%A4%22", UriUtil.encodeFragment("XXä\""));
    }

    @Test
    public void exoticHighValueCharacterEncoding() throws Exception {
        // this is the '蚠'
        assertEquals("%E8%9A%A0", UriUtil.encodeFragment(Character.valueOf((char) 100000).toString()));
    }

    /**
     * The list of allowed chars is build in a different way then the string in production code!
     */
    @Test
    public void allowedUnreservedCharacterEncoding() throws Exception {
        ArrayList<Character> UNRESERVED = new ArrayList<Character>();
        for (int i = 'a'; i <= 'z'; i++) {
            UNRESERVED.add(new Character((char) i));
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            UNRESERVED.add(new Character((char) i));
        }
        for (int i = '0'; i <= '9'; i++) {
            UNRESERVED.add(new Character((char) i));
        }
        UNRESERVED.add('-');
        UNRESERVED.add('.');
        UNRESERVED.add('_');
        UNRESERVED.add('~');

        for (Character character: UNRESERVED){
            assertEquals(character.toString(), UriUtil.encodeFragment(character.toString()));

        }
    }
}
