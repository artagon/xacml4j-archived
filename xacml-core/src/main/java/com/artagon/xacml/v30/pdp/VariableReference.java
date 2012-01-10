package com.artagon.xacml.v30.pdp;

import com.google.common.base.Preconditions;

public class VariableReference extends XacmlObject implements Expression
{
	private VariableDefinition varDef;
	
	/**
	 * Constructs variable reference with a given identifier.
	 * 
	 * @param variableId a  variable identifier
	 */
	public VariableReference(VariableDefinition varDef){
		Preconditions.checkNotNull(varDef);
		this.varDef = varDef;
	}
	
	public String getVariableId(){
		return varDef.getVariableId();
	}
	
	@Override
	public ValueType getEvaluatesTo() {
		return varDef.getEvaluatesTo();
	}

	public VariableDefinition getDefinition(){
		return varDef;
	}
	
	/**
	 * Evaluates appropriate variable definition.
	 * 
	 * @param context a policy evaluation context
	 * @return {@link ValueExpression} representing evaluation
	 * result
	 */
	public ValueExpression evaluate(EvaluationContext context) 
		throws EvaluationException
	{
		return varDef.evaluate(context);
	}
	
	public void accept(ExpressionVisitor v) {
		v.visitEnter(this);
		v.visitLeave(this);
	}	
}