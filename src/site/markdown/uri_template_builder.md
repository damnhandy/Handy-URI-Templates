## URI Template Builder


The `UriTemplateBuilder` API was added to make it easier to programatically construct URI templates. It's used like this:

```java
UriTemplate template =
      UriTemplate.buildFromTemplate("http://example.com")
                 .literal("/foo")
                 .path(var("thing1"),var("explodedThing", true))
                 .fragment(var("prefix", 2))
                 .build();
```

This will yield the following URL template string:

	"http://example.com/foo{/thing1,explodedThing*}{#prefix:2}"


You can also use the `UriTemplateBuilder` to build new templates from other templates like so:

```java
        UriTemplate rootTemplate = UriTemplate.fromTemplate("http://example.com/foo{/thing1}");

        UriTemplate template = UriTemplate.buildFromTemplate(rootTemplate)
                .path(var("explodedThing", true))
                .fragment(var("prefix", 2))
                .build();

        Assert.assertEquals("http://example.com/foo{/thing1}{/explodedThing*}{#prefix:2}", template.getTemplate());
```

This will get you:

       "http://example.com/foo{/thing1}{/explodedThing*}{#prefix:2}"

The API is fairly flexible and pretty useful for constructing URI templates. Have a gander at the
[Java Docs](http://damnhandy.github.io/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/UriTemplateBuilder.html) for more
details.



