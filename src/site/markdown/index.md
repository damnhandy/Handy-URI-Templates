## What is it?

Handy URI Templates is a uritemplate processor implementing [RFC6570](http://tools.ietf.org/html/rfc6570) written in
Java. If you are looking for a non-Java implementation, please check the
[RFC6570 implementations page](http://code.google.com/p/uri-templates/wiki/Implementations). The current
implementation is based on final realease of the uri template spec. The template processor supports the
following features:

* Fluent Java API for manipulating uritemplates
* Supports up to [level 4 template expressions](http://tools.ietf.org/html/rfc6570#section-1.2) including prefix and explode modifiers
* Java objects as template values
* Support for rendering date values
* Template expression validation
* Custom object expanders

As of version `1.1.1`, Handy URI Templates is passes all tests defined by the
[uritemplate-test](https://github.com/uri-templates/uritemplate-test) suite.

## What's it used for?

URI Templates are useful if your building a client or server application that is a using a media type that leverages
URI Templates. Some examples are:

* [JSON Hyper Schema](http://json-schema.org/)
* [JSON Home Documents](http://tools.ietf.org/html/draft-nottingham-json-home-03)
* [HAL - Hypertext Application Language](http://stateless.co/hal_specification.html)
* [OpenSearch](http:/opensearch.org)

And others. URI Templates can even used by themselves as a convienent means of parameterizing URLs.

## Basic Usage

Using the library is simple:
	
```java
String uri =  UriTemplate.fromTemplate("/{foo:1}{/foo,thing*}{?query,test2}")
                         .set("foo", "houses")
                         .set("query", "Ask something")
                         .set("test2", "someting else")
                         .set("thing", "A test")
                         .expand();
```

This will result in the following URI:

	"/h/houses/A%20test?query=Ask%20something&test2=someting%20else"
	
You can see more examples in some of the [unit tests](https://github.com/damnhandy/Handy-URI-Templates/blob/master/src/test/java/com/damnhandy/uri/template/TestBasicUsage.java) or
you can read more in the [JavaDocs](http://damnhandy.github.com/Handy-URI-Templates/apidocs/index.html).

## Project Set Up

To use the library in your project, simply add the following to your projects pom:

```xml
<dependency>
  <groupId>com.damnhandy</groupId>
  <artifactId>handy-uri-templates</artifactId>
  <version>2.0.0</version>
</dependency>
```

You can also download the artifact directly at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Chandy-uri-templates)