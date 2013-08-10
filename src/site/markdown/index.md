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