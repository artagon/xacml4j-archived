package com.artagon.xacml.v30;

import java.util.Collection;



public class AdviceExpression extends BaseDecisionRuleResponseExpression
{
	/**
	 * Constructs advice expression with a given identifier
	 * @param id an advice identifier
	 * @param appliesTo an effect when this advice is applicable
	 * @param attributeExpressions a collection of attribute
	 * assignment expression for this advice
	 * @exception XacmlSyntaxException
	 */
	public AdviceExpression(String id, 
			Effect appliesTo,
			Collection<AttributeAssignmentExpression> attributeExpressions) 
		throws XacmlSyntaxException 
	{
		super(id, appliesTo, attributeExpressions);
	}	
	
	/**
	 * Evaluates this advice expression by evaluating
	 * all {@link AttributeAssignmentExpression}
	 * 
	 * @param context an evaluation context
	 * @return {@link Advice} instance
	 * @throws EvaluationException if an evaluation error
	 * occurs
	 */
	public Advice evaluate(EvaluationContext context) throws EvaluationException
	{
		Collection<AttributeAssignment> attributes = evaluateAttributeAssingments(context);
		return new Advice(getId(), attributes);
	}
	
	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		for(AttributeAssignmentExpression exp : getAttributeAssignmentExpressions()){
			exp.accept(v);
		}
		v.visitLeave(this);
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(!(o instanceof AdviceExpression)){
			return false;
		}
		AdviceExpression ox = (AdviceExpression)o;
		return id.equals(ox.id) 
			&& effect.equals(ox.effect) 
			&& attributeExpressions.equals(ox.attributeExpressions);
	}
}