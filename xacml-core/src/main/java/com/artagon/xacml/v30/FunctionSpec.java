package com.artagon.xacml.v30;




public interface FunctionSpec 
{
	/**
	 * Gets function XACML identifier.
	 * 
	 * @return XACML function identifier
	 */
	String getId();

	/**
	 * Gets function legacy identifier
	 * if any available
	 * 
	 * @return legacy identifier
	 */
	String getLegacyId();
	
	/**
	 * Gets parameter specification
	 * at given
	 * 
	 * @param index a parameter index
	 * @return {@link FunctionParamSpec} at given 
	 * index
	 */
	FunctionParamSpec getParamSpecAt(int index);

	/**
	 * Gets number of function formal 
	 * parameters
	 * 
	 * @return gets number of function formal
	 * parameters
	 */
	int getNumberOfParams();
	
	/**
	 * Tells if this function has variable length 
	 * parameter
	 * 
	 * @return <code>true</code> if it does
	 * <code>false</code> otherwise
	 */
	boolean isVariadic();
	
	/**
	 * Tests if this function requires lazy
	 * parameters evaluation
	 * 
	 * @return <code>true</code> if this
	 * function requires lazy parameters
	 * evaluation
	 */
	boolean isRequiresLazyParamEval();
	
	/**
	 * Validates given array of expressions
	 * as potential function invocation arguments
	 * 
	 * @param params an array of expressions
	 */
	boolean validateParameters(Expression ... params);
	
	/**
	 * Validates given array of expressions
	 * as potential function invocation arguments
	 * 
	 * @param params an array of expressions
	 * @exception XacmlSyntaxException
	 */
	void validateParametersAndThrow(Expression ... params) 
		throws XacmlSyntaxException;
		
	/**
	 * Resolves function return type based on function
	 * invocation arguments
	 * 
	 * @param arguments a function invocation arguments
	 * @return {@link ValueType} resolved function return type
	 */
	ValueType resolveReturnType(Expression ... arguments);
	
	/**
	 * Invokes this function with a given arguments
	 * 
	 * @return {@link ValueExpression} instance representing
	 * function invocation result
	 */
	<T extends ValueExpression> T invoke(EvaluationContext context, Expression ...expressions) 
		throws EvaluationException;
}