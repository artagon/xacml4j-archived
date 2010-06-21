package com.artagon.xacml.v3.pdp.profiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.artagon.xacml.v3.Request;
import com.artagon.xacml.v3.Result;
import com.artagon.xacml.v3.pdp.PolicyDecisionCallback;
import com.artagon.xacml.v3.pdp.RequestProfileHandler;
import com.google.common.collect.Iterables;

/**
 * A chain of {@link RequestProfileHandler}
 * implementations implemented as {@link RequestProfileHandler}
 * 
 * @author Giedrius Trumopickas
 */
public class RequestProfileHandlerChain implements RequestProfileHandler
{
	private List<RequestProfileHandler> handlers;
	
	public RequestProfileHandlerChain(
			Iterable<RequestProfileHandler> handlers)
	{
		this.handlers = new LinkedList<RequestProfileHandler>();
		Iterables.addAll(this.handlers, handlers);
		RequestProfileHandler prev = null;
		for(RequestProfileHandler h : handlers){
			if(prev == null){
				prev = h;
				continue;
			}
			prev.setNext(h);
			prev = h;
		}
	}
	
	public RequestProfileHandlerChain(
			RequestProfileHandler ...handlers){
		this(Arrays.asList(handlers));
	}
	
	@Override
	public final Collection<Result> handle(Request request, PolicyDecisionCallback pdp) 
	{
		if(handlers.isEmpty()){
			return Collections.singleton(pdp.requestDecision(request));
		}
		return handlers.get(0).handle(request, pdp);
	}

	@Override
	public final void setNext(RequestProfileHandler handler) 
	{
		if(handlers.isEmpty()){
			throw new IllegalArgumentException("Can't set next handler, " +
					"this handler does not have any handlers");
		}
		handlers.get(handlers.size() - 1).setNext(handler);
	}
}