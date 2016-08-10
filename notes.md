challenges

unreserved character encoding lives at the expression level, but we need that
info to build regex at varspec level

matching of fragment and unreserved characters in list is seemingly impossible
since `,` is allowed in fragments, but is also the separator token. The RFC seems
to note this so most likely the solution is to document that we don't support
values with reserved delimiters

does order need to be considered for key-value paired expressions like ; ? & ifso
named capture groups cannot be used here
idea:
expressions surface two methods, `getMatchPattern()` and `Map getMatches(Matcher)`
`getMatchPattern` returns a regex to match that expression, but rather than using
named capture groups which can be used at the top level, the expression uses 
getMatches to return a map from variable names to values (which can internally use 
ncg, or not).

ex: `{/var,x}/here{?x,y,empty}` might generate pattern like
`(?<TWFuIGlzI>(?<var>\/[unreserved]*)(?<x>\/[unreserved]*))\/here(?<GRpc3Rp>\?((x|y|empty)=([unreserved]*)&?){0,3})`
the expression `{/var,x}` would grab the group `TWFuIGlzI` and use the known group 
names with ncg to get `var` and `x`, while `{?x,y,empty}` would look at each key value group tuple

