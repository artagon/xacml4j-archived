package com.artagon.xacml.v3.policy;



public interface FunctionSpecBuilder 
{
	
	FunctionSpecBuilder withParam(ValueType type);	
	FunctionSpecBuilder withLazyArgumentsEvaluation();
	FunctionSpecBuilder withParam(ValueType type, int min, int max);
	
	/**
	 * Builds {@link FunctionSpec} with a given
	 * {@link ValueType} as a function return type
	 * and given {@link FunctionInvocation} as a
	 * function implementation
	 * 
	 * @param returnType a function return type
	 * @param invocation a function
	 * @return {@link FunctionSpec} a f
	 */
	FunctionSpec build(ValueType returnType, 
			FunctionInvocation invocation);
	
	FunctionSpec build(FunctionReturnTypeResolver returnType, 
			FunctionInvocation invocation);
}