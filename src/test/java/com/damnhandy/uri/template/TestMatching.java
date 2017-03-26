package com.damnhandy.uri.template;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by nfischer on 7/3/2016.
 */
public class TestMatching {

    final String hello = "Hello World!";
    final String x = "1024";
    final String y = "768";
    final String empty = "";

    @Test
    public void simpleExpansion(){
        UriTemplate temp = UriTemplate.buildFromTemplate("{hello}").build();
        temp.setFrom("Hello%20World%21");
        //assertEquals(hello, temp.get("hello")); todo decode
        assertEquals("Hello%20World%21", temp.get("hello"));
    }

    @Test
    public void reservedExpansion(){
        UriTemplate temp = UriTemplate.buildFromTemplate("{+hello}").build();
        temp.setFrom("Hello%20World!");
        //assertEquals(hello, temp.get("hello")); todo decode
        assertEquals("Hello%20World!", temp.get("hello"));
    }

    @Test
    public void fragmentExpansion(){
        UriTemplate temp = UriTemplate.buildFromTemplate("{#x,hello,y}").build();
        temp.setFrom("#1024,Hello%20World!,768");
        assertEquals(x, temp.get("x"));
        assertEquals(y, temp.get("y"));
        //assertEquals(hello, temp.get("hello")); todo decode
    }

    @Test
    public void queryExpansion(){
        UriTemplate temp = UriTemplate.buildFromTemplate("{?x,y,empty}").build();
        temp.setFrom("?x=1024&y=768&empty=");
        assertEquals(x, temp.get("x"));
        assertEquals(y, temp.get("y"));
        assertEquals(empty, temp.get("empty"));
    }


    public void test(){
        Pattern temp1 = UriTemplate.buildFromTemplate("{hello}").build().getReverseMatchPattern();
        String val1 = "Hello World!";
        String exp1 = "Hello%20World%21";

        System.out.println(temp1);
        Matcher m = temp1.matcher(exp1);
        m.find();

        assertEquals(exp1, m.group(0));
        System.out.println(m.group(0));

        Pattern temp2 = UriTemplate.buildFromTemplate("{+hello}").build().getReverseMatchPattern();
        String val2 = "Hello World!";
        String exp2 = "Hello%20World!";
        System.out.println(temp2.pattern());

        Matcher m2 = temp2.matcher(exp2);
        m2.find();
        System.out.println(m2.group(0));
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
        System.out.println(temp4);

        Matcher m4 = temp4.matcher(exp4);
        m4.find();
        System.out.println(m4.group("x"));
        System.out.println(m4.group("empty") + "<(empty)");
        System.out.println(m4.group("y"));
        assertEquals("1024", m4.group("x"));
        assertEquals("", m4.group("empty"));
        assertEquals("768", m4.group("y"));


        System.out.println(matchParameters('?', '&', "?x=1024&y=768&empty=", asList("y", "x", "empty")));

        print(matchSegments('#', ',', "#/foo/bar,1024", asList("path", "x")));
        print(matchSegments('\0', ',', "1024,Hello%20World%21,768", asList("x", "hello", "y")));

        Map<String, String[]> multi = matchParameters('?', '&', "?x=1024&y=768&y=othery&empty=", asList("y", "x", "empty"));
        multi.entrySet().forEach(e -> {
            System.out.print(e.getKey() + "=");
            System.out.print(asList(e.getValue()));
            System.out.print(' ');
        });




    }

    @Test
    public void test2(){
        UriTemplate template = UriTemplate.buildFromTemplate("mysite.com/some{/path}/stuff{?query}&parts=too{&extensions}").build();
        UriTemplate expanded = UriTemplate
        .buildFromTemplate("mysite.com/some{/path}/stuff{?query}&parts=too{&extensions}")
        .build()
        .set("path", "pathVal")
        .set("query", "queryVal")
        .set("extensions", "extensionsVal");
        System.out.println(template.getReverseMatchPattern());
        System.out.println(expanded.getValues());

        template.setFrom("mysite.com/some/pathVal/stuff?query=queryVal&parts=too&extensions=extensionsVal");
        System.out.println(template.getValues());

        assertEquals(expanded.getValues(), template.getValues());

        UriTemplate template2 = UriTemplate.fromTemplate("https://example.com/collection{/id}{?orderBy}");
        template2.setFrom("\"https://example.com/collection/9?orderBy=age");

        assertEquals("9", template2.get("id"));
        assertEquals("age", template2.get("orderBy"));
        System.out.println(template2.get("id")); // 9
        System.out.println(template2.get("orderBy")); // age
        System.out.println(template);

    }

    void print(Map<String, String[]> multi){
        System.out.print('{');
        multi.entrySet().forEach(e -> {
            System.out.print(e.getKey() + "=");
            System.out.print(asList(e.getValue()));
            System.out.print(" ");
        });
        System.out.println('}');
    }

    Map<String, String[]> matchParameters(char prefix, char separator, String part, List<String> varNames){
        String regex = "\\" + prefix;
        Map<String, List<String>> results = new HashMap<>();

        String varNameRex = "(?<key>";
        for(String varName:varNames){
            varNameRex += "\\Q" + varName + "\\E" + "|";
        }
        varNameRex = varNameRex.substring(0, varNameRex.length() -1);
        varNameRex += ")";

        /*
        for(int n = varNames.size(); n --> 0;){
            regex += varNameRex + '=' + "(?<value>[^"+separator+"]*)" + separator;
        }
        */

        regex = varNameRex + '=' + "(?<value>[^"+separator+"]*)";

        System.out.println(regex);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(part);

        while(matcher.find()){
            String key = matcher.group(1);
            String value = matcher.group(2);

            List<String> values = results.getOrDefault(key, new ArrayList<>());
            values.add(value);

            results.put(key, values);
        }

        Map<String, String[]> ret = new HashMap<>();
        for(Map.Entry<String, List<String>> entry:results.entrySet())
            ret.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));

        return ret;
    }

    Map<String, String[]> matchSegments(char prefix, char separator, String part, List<String> varNames){
        String regex = prefix == '\0' ? "" : "\\" + prefix;
        Map<String, String[]> results = new HashMap<>();

        for(String varName:varNames){
            regex += "(?<"+varName+">[^"+separator+"]*)" + separator;
        }

        regex = regex.substring(0, regex.length()-1);

        System.out.println(regex);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(part);

        if (!matcher.matches())
            throw new RuntimeException("doesn't match");

        for(String varName:varNames){
            results.put(varName,  new String[]{matcher.group(varName)});
        }

        return results;
    }
}
