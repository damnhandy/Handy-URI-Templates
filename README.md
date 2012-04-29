# Handy URI Templates

This project is a compliant implementation of [RFC6570](http://tools.ietf.org/html/rfc6570) written in Java supporting up to level 4 expressions.

## Maven

To use Handy URI Templates, you need to add the following dependency to your pom.xml:

	<dependency>
		<groupId>com.damnhhandy</groupId>
		<artifactId>handy-uri-templates</artifactId>
		<version>1.0.0-B3-SNAPSHOT</version>
	</dependency>

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

## Java types

The basic types that the URI template engine supports are as follows:

* arrays
* java.lang.String
* java.util.List<Object>
* java.util.Map<String, Object>	


Values that are not Strings are rendered into the URI will have its `toString()` method called. Java objects can be treated as composite objects (as name/value pairs). 

## Using with HTTP Clients

The API can be used with existing HTTP frameworks like the most excellent [Async Http Client](https://github.com/sonatype/async-http-client). Using the [GitHub API](http://developer.github.com/v3/repos/commits/), we can use the a `UriTemplate` to create a URI to look at this repository:

	  RequestBuilder builder = new RequestBuilder("GET");
      Request request = builder.setUrl(
             UriTemplate.fromExpression("https://api.github.com/repos{/user,repo,function,id}")
                        .set("user", "damnhandy")
                        .set("repo", "Handy-URI-Templates")
                        .set("function","commits")
                        .expand()).build();

When `Request.getRawUrl()` is called, it will return:

	"https://api.github.com/repos/damnhandy/Handy-URI-Templates/commits"

Please have a look at the exmaple [test case](https://github.com/damnhandy/Handy-URI-Templates/blob/master/src/test/java/com/damnhandy/uri/template/examples/TestGitHubApis.java) for more details.

Usage with the [Apache HTTP Client](http://hc.apache.org/httpcomponents-client-ga/index.html) is just as similar.

## Composite Values

The URI Template spec supports [composite values](http://tools.ietf.org/html/rfc6570#section-2.4.2) where the variable may be a list of values an associative array of (name, value) pairs. Handy URI templates always treats lists as java.util.List and name/value pairs as a java.util.Map.  

## POJOs as Composite Values

A `VarExploder` is invoked when an explode modifier "*" is encountered within a variable name within a URI template expression and the replacement value is a complex type, such a some type of POJO. For most use cases, the `DefaultVarExplode`r will be sufficient. Please refer to the `DefaultVarExploder` JavaDoc for more details on how it works.

Should the `DefaultVarExploder` not be suitable for your needs, custom `VarExploder` implementations can be added by rolling your own implementation. A custom VarExploder implementation can be registered in one of two ways. By wrapping your object in your VarExploder:

	UriTemplate.fromExpression("/mapper{?address*}").set("address", new MyCustomVarExploder(address)).expand();
 
Note: VarExploder implementations are ONLY invoked when the explode modifier "*" is declared in the URI Template expression. If the variable declaration does not specify the explode modifier, an exception is raised.

The DefaultVarExploder is a VarExploder implementation that takes in a Java object and extracts the properties for use in a URI Template. Given the following URI template expression:

	/mapper{?address*}
 
And this Java object for an address:

	Address address = new Address();
	address.setState("CA");
	address.setCity("Newport Beach");
	String result = UriTemplate.fromExpression("/mapper{?address*}").set("address", address).expand();
	
The expanded URI will be:

	/mapper?city=Newport%20Beach&state=CA
 
The DefaultVarExploder breaks down the object properties as follows:

* All properties that contain a non-null return value will be included
* Getters or fields annotated with UriTransient will NOT included in the list
* By default, the property name is used as the label in the URI. This can be overridden by placing the @VarName annotation on the field or getter method and specifying a name.
* Field level annotation take priority of getter annotations
* Property names are sorted in alphabetical order

