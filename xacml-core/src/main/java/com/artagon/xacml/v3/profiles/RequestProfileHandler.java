package com.artagon.xacml.v3.profiles;

import java.util.Collection;

import com.artagon.xacml.v3.Request;
import com.artagon.xacml.v3.Result;
import com.artagon.xacml.v3.pdp.PolicyDecisionCallback;

public interface RequestProfileHandler 
{
	/**
	 * Handles given request
	 * 
	 * @param request a decision request
	 * @param pdp a policy decision point
	 * @return collection of {@link Result} instances
	 */
	Collection<Result> handle(Request request, PolicyDecisionCallback pdp);
	
	/**
	 * Sets next handler in a chain
	 * 
	 * @param handler a request handler
	 */
	void setNext(RequestProfileHandler handler);
}