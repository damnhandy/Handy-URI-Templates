package com.damnhandy.uri.template;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by nfischer on 7/3/2016.
 */
public class TestMatching {

    @Test
    public void test(){
        Pattern temp1 = UriTemplate.buildFromTemplate("{hello}").build().getReverseMatchPattern();
        String val1 = "Hello World!";
        String exp1 = "Hello%20World%21";

        Matcher m = temp1.matcher(exp1);
        m.find();

        assertEquals(exp1, m.group("hello"));
        System.out.println(m.group("hello"));

        Pattern temp2 = UriTemplate.buildFromTemplate("{+hello}").build().getReverseMatchPattern();
        String val2 = "Hello World!";
        String exp2 = "Hello%20World!";
        System.out.println(temp2.pattern());

        Matcher m2 = temp2.matcher(exp2);
        m2.find();
        System.out.println(m2.group("hello"));
        assertEquals(exp2, m2.group("hello"));

        Pattern temp3 = UriTemplate.buildFromTemplate("{#x,hello,y}").build().getReverseMatchPattern();
        String exp3 = "#1024,Hello%20World!,768";

        Matcher m3 = temp3.matcher(exp3);
        m3.find();
        System.out.println(m3.group("x"));
        System.out.println(m3.group("hello"));
        System.out.println(m3.group("y"));
        assertEquals("1024", m3.group("x"));
        assertEquals("Hello%20World!", m3.group("hello"));
        assertEquals("768", m3.group("y"));

        Pattern temp4 = UriTemplate.buildFromTemplate("{?x,y,empty}").build().getReverseMatchPattern();
        String exp4 = "?x=1024&y=768&empty=";

        Matcher m4 = temp4.matcher(exp4);
        m4.find();
        System.out.println(m4.group("x"));
        System.out.println(m4.group("empty") + "<(empty)");
        System.out.println(m4.group("y"));
        assertEquals("1024", m4.group("x"));
        assertEquals("", m4.group("empty"));
        assertEquals("768", m4.group("y"));
    }
}
