package com.artagon.xacml.v3.policy;

public class PolicySyntaxException extends PolicyException
{

	public PolicySyntaxException(String template, Object... arguments) {
		super(template, arguments);
	}
	
	public PolicySyntaxException(Throwable cause, String message,
			Object... arguments) {
		super(cause, message, arguments);
	}
	
	public PolicySyntaxException(Throwable cause) {
		super(cause);
	}
}