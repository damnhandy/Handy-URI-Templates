# Handy URI Templates

This project is an implementation of [RFC6570](http://tools.ietf.org/html/rfc6570) written in Java supporting up to level 4 expressions. It features a fluent API and Java objects as template values.

## Maven

To use Handy URI Templates, you need to add the following dependency to your pom.xml:

	<dependency>
		<groupId>com.damnhandy</groupId>
		<artifactId>handy-uri-templates</artifactId>
		<version>1.0.2</version>
	</dependency>

If you feel like using the bleeding edge version, you can use a snapshot release:

	<dependency>
		<groupId>com.damnhandy</groupId>
		<artifactId>handy-uri-templates</artifactId>
		<version>1.1.0-SNAPSHOT</version>
	</dependency>

In order to use a SNAPSHOT, you'll have to add the Sonatype snapshots repository:

	<repository>
        <id>sonatype-nexus-snapshots</id>   
        <name>sonatype-nexus-snapshots</name>
		<url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>

You can also download the artifact
	http://search.maven.org
	
    
## Basic Usage

Using the library is simple:
	
	String uri = 
		UriTemplate.fromExpression("/{foo:1}{/foo,thing*}{?test1, test2}")
				   .set("foo", "one")
				   .set("test1", "query")
				   .set("test2", "blah")
				   .set("thing", "A test")
				   .expand();


This will result in the following URI:

	"/o/one/A%20test?test1=query&test2=blah"
	
You can find more in the [JavaDocs](http://damnhandy.com/handy-uri-templates/apidocs/).

## Using with HTTP Clients

The API can be used with existing HTTP frameworks like the most excellent [Async Http Client](https://github.com/sonatype/async-http-client). Using the [GitHub API](http://developer.github.com/v3/repos/commits/), we can use the a `UriTemplate` to create a URI to look at this repository:

	  RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
             UriTemplate.fromExpression("https://api.github.com/repos{/user,repo,function,id}")
                        .set("user", "damnhandy")
                        .set("repo", "Handy-URI-Templates")
                        .set("function","commits")
                        .expand()).build();

When `Request.getUrl()` is called, it will return:

	"https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits"

Please have a look at the example [test case](https://github.com/damnhandy/Handy-URI-Templates/blob/master/src/test/java/com/damnhandy/uri/template/examples/TestGitHubApis.java) for more details.

Usage with the [Apache HTTP Client](http://hc.apache.org/httpcomponents-client-ga/index.html) is just as similar.

## Supported Value Types

While the `set()` method accepts any Java object, the following Java types are preferred:

* primitive and Object types such as:
  * int & Integer
  * double & Double
  * char & Character
  * float & Float
  * double & Double
  * short & Short
  * long & Long
* Arrays of the above types
* java.util.List<Object>
* java.util.Map<String, Object>	
* java.util.Date. Dates will be formted using the templates default formatter.
* Anything with a `toString()` method

Values that are not strings are rendered into the URI by calling its `toString()` method. Java objects can be treated as composite objects (as name/value pairs) when the variable specifies the explode modifier (see Composite Value below). A `char[]` or `Character[]` array will be treated as String. A multi dimensional character array will be treated as a List of Strings. 

## Unsupported Value Types

The template processor will not accept the following types of value combinations:

* with the exception of character arrays, multi dimensional arrays are not supported.
* Collections of Collections
* Maps that have values of type array, Collection, or Map.

If you need such data structures in a URI, consider implementing your own `VarExploder` to handle it. 

## Composite Values

The URI Template spec supports [composite values](http://tools.ietf.org/html/rfc6570#section-2.4.2) where the variable may be a list of values of an associative array of (name, value) pairs. The template processor always treats lists as java.util.List and name/value pairs as a java.util.Map. Lists and Maps work with any supported type that is not anoth List, Map, or array. 

## POJOs as Composite Values

The template process can treat simple Java objects as composite value. When a POJO is set on a template variable and the variable specifies the an explode modifier "*", a `VarExploder` is invoked. The purpose of the `VarExploder` is to expose the object properties as name/value pairs. 

For most use cases, the `DefaultVarExploder` should be sufficient. The `DefaultVarExploder` is a VarExploder implementation that takes in a Java object and extracts the properties for use in a URI Template. This class is called by default when a POJO is passed into the UriTemplate and the explode modifier is present on the variable. Given the following URI template expression:

	/mapper{?address*}
 
And this Java object for an address:

	Address address = new Address();
	address.setState("CA");
	address.setCity("Newport Beach");
	String result = UriTemplate.fromExpression("/mapper{?address*}")
	                           .set("address", address)
	                           .expand();
	
The expanded URI will be:

	/mapper?city=Newport%20Beach&state=CA
 
The `DefaultVarExploder` breaks down the object properties as follows:

* All properties that contain a non-null return value will be included
* Getters or fields annotated with `@UriTransient` will be excluded 
* By default, the property name is used as the label in the URI. This can be overridden by placing the `@VarName` annotation on the field or getter method and specifying a name.
* Field level annotation take priority of getter annotations
* Property names are sorted in alphabetical order

Please refer to the  JavaDoc for more details on how the `DefaultVarExploder` works.

Should the `DefaultVarExploder` not be suitable for your needs, custom `VarExploder` implementations can be added by rolling your own implementation. A custom VarExploder implementation can be used by wrapping your object in your implementation:

	UriTemplate.fromExpression("/mapper{?address*}")
	           .set("address", new MyCustomVarExploder(address))
	           .expand();
 
Note: All `VarExploder` implementations are ONLY invoked when the explode modifier "*" is declared in the URI Template expression. If the variable declaration does not specify the explode modifier, an exception is raised.



