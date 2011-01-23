package com.artagon.xacml.v30.policy.function;

import javax.security.auth.x500.X500Principal;

import com.artagon.xacml.v30.spi.function.XacmlFuncParam;
import com.artagon.xacml.v30.spi.function.XacmlFuncReturnType;
import com.artagon.xacml.v30.spi.function.XacmlFuncSpec;
import com.artagon.xacml.v30.spi.function.XacmlFunctionProvider;
import com.artagon.xacml.v30.types.BooleanType;
import com.artagon.xacml.v30.types.BooleanValue;
import com.artagon.xacml.v30.types.RFC822NameValue;
import com.artagon.xacml.v30.types.StringValue;
import com.artagon.xacml.v30.types.X500NameValue;

@XacmlFunctionProvider(description="XACML special match functions")
public class SpecialMatchFunctions 
{
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static 
			BooleanValue rfc822NameMatch(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue pattern, 
			@XacmlFuncParam(typeId="urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name")RFC822NameValue rfc822Name)
	{
		 return BooleanType.BOOLEAN.create(rfc822Name.getValue().matches(pattern.getValue()));
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:x500Name-match")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static 
			BooleanValue x500NameMatch(
			@XacmlFuncParam(typeId="urn:oasis:names:tc:xacml:1.0:data-type:x500Name")X500NameValue a, 
			@XacmlFuncParam(typeId="urn:oasis:names:tc:xacml:1.0:data-type:x500Name")X500NameValue b)
	{
		 String n0 = a.getValue().getName(X500Principal.CANONICAL);
		 String n1 = b.getValue().getName(X500Principal.CANONICAL);
		 return BooleanType.BOOLEAN.create(n1.endsWith(n0));
	}
}