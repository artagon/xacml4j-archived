package com.artagon.xacml.v3.policy;


public class MockFunctionImplementation <T extends Value> implements FunctionInvocation
{
	private T expectedResult;
	private boolean failWithIndeterminate = false;
	
	public MockFunctionImplementation(T expectedResult){
		this.expectedResult = expectedResult;
	}
	
	public void setFailWithIndeterminate(boolean fail){
		this.failWithIndeterminate = fail;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T invoke(FunctionSpec spec, EvaluationContext context, Expression ...args) 
		throws FunctionInvocationException 
	{
		if(failWithIndeterminate){
			throw new FunctionInvocationException(spec, "Failed to invoke mock function");
		}
		return expectedResult;
	}
	
}
