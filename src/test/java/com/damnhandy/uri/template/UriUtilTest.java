package com.damnhandy.uri.template;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

public class UriUtilTest
{

   private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

   private static final String DIGIT = "0123456789";

   private static final String UNRESERVED = ALPHA + DIGIT + "-._~";

   private static final String SUBDELIMS = "!$&'()*+,;=";

   private static final String GENDELIMS = ":/?#[]@";

   private void validateFragment(String excpected, String input) throws UnsupportedEncodingException
   {
      assertEquals(excpected, UriUtil.encodeFragment(input));

   }

   private void validate(String excpected, String input) throws UnsupportedEncodingException
   {
      assertEquals(excpected, UriUtil.encode(input));

   }

   @Test
   public void generalNothingToEncode() throws Exception
   {
      validate(UNRESERVED, UNRESERVED);

   }

   @Test
   public void fragmentNothingToEncode() throws Exception
   {
      validateFragment(UNRESERVED + SUBDELIMS + "/?:@", UNRESERVED + SUBDELIMS + "/?:@");
   }

   @Test
   @Ignore
   public void reservedNothingToEncode() throws Exception
   {
      validate(UNRESERVED + SUBDELIMS + GENDELIMS, UNRESERVED + SUBDELIMS + GENDELIMS);
   }

   @Test
   public void fragmentWithCharsToEncodeInFragmentButNotInReserved() throws Exception
   {
      validate("%23%5B%5D", "#[]");
   }

   @Test
   public void fragmentWithSomeCharsToEncode() throws Exception
   {
      validateFragment("%C3%A4%C3%B6", "äö");
   }

   @Test
   public void reservedWithSomeCharsToEncode() throws Exception
   {
      validate("%C3%A4%C3%B6", "äö");

   }

   @Test
   public void generalWithSomeCharsToEncode() throws Exception
   {

      assertEquals("XX%C3%A4%22", UriUtil.encodeFragment("XXä\""));
   }

   @Test
   public void exoticHighValueCharacterEncoding() throws Exception
   {
      // this is the '蚠'
      assertEquals("%E8%9A%A0", UriUtil.encodeFragment(Character.valueOf((char) 100000).toString()));
   }

   /**
    * The list of allowed chars is build in a different way then the string in production code!
    */
   @Test
   public void allowedUnreservedCharacterEncoding() throws Exception
   {
      ArrayList<Character> UNRESERVED = new ArrayList<Character>();
      for (int i = 'a'; i <= 'z'; i++)
      {
         UNRESERVED.add(Character.valueOf((char) i));
      }
      for (int i = 'A'; i <= 'Z'; i++)
      {
         UNRESERVED.add(Character.valueOf((char) i));
      }
      for (int i = '0'; i <= '9'; i++)
      {
         UNRESERVED.add(Character.valueOf((char) i));
      }
      UNRESERVED.add('-');
      UNRESERVED.add('.');
      UNRESERVED.add('_');
      UNRESERVED.add('~');

      for (Character character : UNRESERVED)
      {
         assertEquals(character.toString(), UriUtil.encodeFragment(character.toString()));

      }
   }
}
