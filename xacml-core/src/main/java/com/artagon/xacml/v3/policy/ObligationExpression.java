package com.artagon.xacml.v3.policy;

import java.util.Collection;

import com.artagon.xacml.v3.Obligation;

public final class ObligationExpression extends BaseDecisionResponseExpression
{
	public ObligationExpression(String id, Effect effect,
			Collection<AttributeAssignmentExpression> attributeExpressions) {
		super(id, effect, attributeExpressions);
	}
	
	public Obligation evaluate(EvaluationContext context) throws EvaluationException
	{
		Collection<AttributeAssignment> attributes = evaluateAttributeAssingments(context);
		return new Obligation(getId(), attributes);
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		for(AttributeAssignmentExpression exp : getAttributeAssignmentExpressions()){
			exp.accept(v);
		}
		v.visitLeave(this);
	}
}