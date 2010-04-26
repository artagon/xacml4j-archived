package com.artagon.xacml.v3;

import com.artagon.xacml.util.Preconditions;

public class RequestProcessingException extends XacmlException
{
	private static final long serialVersionUID = -8700243289139962516L;
	private Status status;
	
	public RequestProcessingException(Status code, 
			String template, Object... arguments) {
		super(template, arguments);
		Preconditions.checkNotNull(code);
		this.status = code;
	}

	public RequestProcessingException(Status code, 
			Throwable cause, String message,
			Object... arguments) {
		super(cause, message, arguments);
		Preconditions.checkNotNull(code);
		this.status = code;
	}

	public RequestProcessingException(Status code, 
			Throwable cause) {
		super(cause);
		Preconditions.checkNotNull(code);
		this.status = code;
	}	
	
	public final Status getStatusCode(){
		return status;
	}
}