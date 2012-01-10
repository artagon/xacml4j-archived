package com.artagon.xacml.v30.pdp;



/**
 * A XACML policy expression
 * 
 * @author Giedrius Trumpickas
 */
public interface Expression 
{	
	/**
	 * Gets type to which this expression
	 * evaluates to
	 * 
	 * @return {@link ValueType}
	 */
	ValueType getEvaluatesTo();
	
	/**
	 * Evaluates this expression
	 * 
	 * @param context an evaluation context
	 * @return {@link Expression} an expression 
	 * representing evaluation result, usually evaluation result
	 * is an instance {@link ValueExpression} but in some cases
	 * expression evaluates to itself
	 * @throws EvaluationException if an evaluation error
	 * occurs
	 */
	Expression evaluate(
			EvaluationContext context) throws EvaluationException;
	
	void accept(ExpressionVisitor v);
}