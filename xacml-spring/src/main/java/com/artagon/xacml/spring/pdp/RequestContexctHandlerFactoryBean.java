package com.artagon.xacml.spring.pdp;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.artagon.xacml.v30.pdp.RequestContextHandler;
import com.google.common.base.Preconditions;

public class RequestContexctHandlerFactoryBean extends AbstractFactoryBean<RequestContextHandler>
{
	private RequestContextHandler ref;
	
	public void setRef(RequestContextHandler handler){
		Preconditions.checkNotNull(handler);
		this.ref = handler;
	}
	@Override
	protected RequestContextHandler createInstance() throws Exception {
		Preconditions.checkState(ref != null);
		return ref;
	}

	@Override
	public Class<?> getObjectType() {
		return RequestContextHandler.class;
	}
	
}