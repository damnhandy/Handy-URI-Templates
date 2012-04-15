# Handy URI Templates

This project is an implementation of [RFC6570](http://tools.ietf.org/html/rfc6570) written in Java. At present, this is very much a work in project and pretty bare bones. It does not pass all of my tests at the moment and is a pretty hairy state of alpha.

## Using the Library

Using the library is simple:

	UriTemplate t = UriTemplate.create("/{foo:1}{/foo,thing*}{?test1, test2}");
	Map<String, Object> vars = new HashMap<String, Object>();
	vars.put("foo", "one");
	vars.put("test1", "query");
	vars.put("test2", "blah");
	vars.put("thing", "A test");
	String result = t.expand(vars);

This would result in the following URI:

	"/o/one/A%20test?test1=query&test2=blah"
	
## Future Plans

In addition to making the implementaion fully compliant with RFC6570, I'm considering the following:

* Include more options other than java.util.Map to supply paramater values. For exmaple, it would be helpful to be able to explode a POJO into a URI parameter.
* A URI Template Builder that can be used to produce URI Template strings for use in clients.