package com.artagon.xacml.v3.policy;

import com.artagon.xacml.v3.StatusId;

@SuppressWarnings("serial")
public class EvaluationException extends PolicyException
{

	public EvaluationException(String template,
			Object... arguments) {
		super(StatusId.STATUS_PROCESSING_ERROR, template, arguments);
	}
	
	public EvaluationException(Throwable cause,
			String message, Object... arguments) {
		super(StatusId.STATUS_PROCESSING_ERROR, cause, message, arguments);
	}
}