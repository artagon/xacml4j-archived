package com.artagon.xacml.v30.marshall;

import java.io.IOException;

import com.artagon.xacml.v30.ResponseContext;
import com.artagon.xacml.v30.XacmlSyntaxException;

public interface ResponseUnmarshaller 
{
	ResponseContext unmarshal(Object source) 
		throws XacmlSyntaxException, IOException;
}