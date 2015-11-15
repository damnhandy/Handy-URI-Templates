# Handy URI Templates

[![Join the chat at https://gitter.im/damnhandy/Handy-URI-Templates](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/damnhandy/Handy-URI-Templates?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://secure.travis-ci.org/damnhandy/Handy-URI-Templates.png?branch=master)](http://travis-ci.org/damnhandy/Handy-URI-Templates)

[![Dependency Status](https://www.versioneye.com/user/projects/5499a5516b1b8171590000b5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5499a5516b1b8171590000b5)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.damnhandy/handy-uri-templates/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.damnhandy/handy-uri-templates)

Handy URI Templates is a uritemplate processor implementing [RFC6570](http://tools.ietf.org/html/rfc6570) written in Java. If you are looking for a non-Java implementation, please check the [RFC6570 implementations page](http://code.google.com/p/uri-templates/wiki/Implementations). The current implementation is based on the final realease of the uri template spec. The template processor supports the following features:

* Fluent Java API for manipulating uritemplates
* Supports up to [level 4 template expressions](http://tools.ietf.org/html/rfc6570#section-1.2) including prefix and explode modifiers
* Strong validation and error reporting
* Java objects as template values
* Support for rendering date values
* Template expression validation
* Custom object expanders
* Support for [Jackson](http://jackson.codehaus.org/) serializers and deserializers.
* Support for partial template expansion

As of version `1.1.1`, Handy URI Templates passes all tests defined by the [uritemplate-test](https://github.com/uri-templates/uritemplate-test) suite.

The complete [JavaDocs are here](http://damnhandy.github.io/Handy-URI-Templates/apidocs/).


## Maven

To use the latest version of Handy URI Templates, you need to add the following dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.damnhandy</groupId>
  <artifactId>handy-uri-templates</artifactId>
  <version>2.1.1</version>
</dependency>
```

The next version of Handy URI Templates includes some big changes to the API. If you want to be daring, you can use the `SNAPSHOT` release:

```xml
<dependency>
  <groupId>com.damnhandy</groupId>
  <artifactId>handy-uri-templates</artifactId>
  <version>2.1.2-SNAPSHOT</version>
</dependency>
```



In order to use a SNAPSHOT release, you'll have to add the Sonatype snapshots repository:

```xml
<repository>
  <id>sonatype-nexus-snapshots</id>
  <name>sonatype-nexus-snapshots</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```

You can also download the artifact directly at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Chandy-uri-templates)


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

You can find more in the [JavaDocs](http://damnhandy.github.com/Handy-URI-Templates/apidocs/index.html).

## URI Template Builder API

Starting in version 2.x, the `UriTemplateBuilder` was added to make it easier to programatically construct URI templates. It's used like this:

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

This API is still a work in progress an feedback is appreciated.

## Using with HTTP Clients

The API can be used with existing HTTP frameworks like the most excellent [Async Http Client](https://github.com/sonatype/async-http-client). Using the [GitHub API](http://developer.github.com/v3/repos/commits/), we can use the a `UriTemplate` to create a URI to look at this repository:

```java
RequestBuilder builder = new RequestBuilder("GET");
Request request = builder.setUrl(
    UriTemplate.fromTemplate("https://api.github.com/repos{/user,repo,function,id}")
               .set("user", "damnhandy")
               .set("repo", "Handy-URI-Templates")
               .set("function","commits")
               .expand()).build();
```

When `Request.getUrl()` is called, it will return:

	"https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits"

Please have a look at the example [test case](https://github.com/damnhandy/Handy-URI-Templates/blob/master/src/test/java/com/damnhandy/uri/template/examples/TestGitHubApis.java) for more details.

Usage with the [Apache HTTP Client](http://hc.apache.org/httpcomponents-client-ga/index.html) is just as similar.

## Supported Value Types

While the `set()` method of the [UriTemplate](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/UriTemplate.html) accepts any Java object, the following Java types are preferred:

* Primitive and Object types such as:
	* int & Integer
	* double & Double
	* char & Character
	* float & Float
	* double & Double
	* short & Short
	* long & Long
	* UUID
	* Enums 
*  Arrays of the above types
* `java.util.List<Object>`
* `java.util.Map<String, Object>`
* `java.util.Date` Dates will be formatted using the template's default formatter.
* Anything with a `toString()` method



Values that are not strings are rendered into the URI by calling its `toString()` method. Java objects can be treated as composite objects (as name/value pairs) when the variable specifies the explode modifier (see Composite Value below). A `char[]` or `Character[]` array will be treated as String. A multi dimensional character array will be treated as a List of Strings.


## Unsupported Value Types

The template processor will not accept the following types of value combinations:

* with the exception of character arrays, multi dimensional arrays are not supported.
* Collections of Collections
* Maps that have values of type array, Collection, or Map.

If you need such data structures in a URI, consider implementing your own `VarExploder` to handle it.

## Composite Values

The URI Template spec supports [composite values](http://tools.ietf.org/html/rfc6570#section-2.4.2) where the variable may be a list of values of an associative array of (name, value) pairs. The template processor always treats lists as java.util.List and name/value pairs as a java.util.Map. Lists and Maps work with any supported type that is not another List, Map, or array.

## POJOs as Composite Values

The template processor can treat simple Java objects as composite value. When a POJO is set on a template variable and the variable specifies the an explode modifier "*", a [VarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/VarExploder.html) is invoked. The purpose of the `VarExploder` is to expose the object properties as name/value pairs.

For most use cases, the [DefaultVarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/DefaultVarExploder.html) should be sufficient. The `DefaultVarExploder` is a VarExploder implementation that takes in a Java object and extracts the properties for use in a URI Template. This class is called by default when a POJO is passed into the UriTemplate and the explode modifier is present on the variable. Given the following URI template expression:

	/mapper{?address*}

And this Java object for an address:

```java
Address address = new Address();
address.setState("CA");
address.setCity("Newport Beach");
String result = UriTemplate.fromTemplate("/mapper{?address*}")
                           .set("address", address)
                           .expand();
```

The expanded URI will be:

	/mapper?city=Newport%20Beach&state=CA

The [DefaultVarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/DefaultVarExploder.html) breaks down the object properties as follows:

* All properties that contain a non-null return value will be included
* Getters or fields annotated with `@UriTransient` will be excluded
* By default, the property name is used as the label in the URI. This can be overridden by placing the `@VarName` annotation on the field or getter method and specifying a name.
* Field level annotations take priority over getter annotations
* Property names are sorted in the order that they are found in the target class.

Please refer to the JavaDoc for more details on how the `DefaultVarExploder` works.

Should the [DefaultVarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/DefaultVarExploder.html) not be suitable for your needs, custom [VarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/VarExploder.html) implementations can be added by rolling your own implementation. A custom VarExploder implementation can be used by wrapping your object in your implementation:

```java
UriTemplate.fromTemplate("/mapper{?address*}")
           .set("address", new MyCustomVarExploder(address))
           .expand();
```

Note: All [VarExploder](http://damnhandy.github.com/Handy-URI-Templates/apidocs/com/damnhandy/uri/template/VarExploder.html) implementations are ONLY invoked when the explode modifier "*" is declared in the URI Template expression. If the variable declaration does not specify the explode modifier, an exception is raised.

License
-------

   Copyright 2011-2013 Ryan J. McDonough

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
