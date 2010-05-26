package com.artagon.xacml.v3.policy.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.artagon.xacml.v3.AttributeAssigmentExpression;
import com.artagon.xacml.v3.AttributeAssignment;
import com.artagon.xacml.v3.Decision;
import com.artagon.xacml.v3.Effect;
import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.EvaluationException;
import com.artagon.xacml.v3.PolicyElement;
import com.artagon.xacml.v3.PolicySyntaxException;
import com.artagon.xacml.v3.StatusCode;
import com.artagon.xacml.v3.XacmlObject;

abstract class BaseDecisionRuleResponseExpression extends XacmlObject implements PolicyElement
{
	private String id;
	private Effect effect;
	private Collection<AttributeAssigmentExpression> attributeExpressions;
	
	/**
	 * Constructs expression with a given identifier,
	 * effect and collection of {@link DefaultAttributeAssignmentExpression}
	 * expressions
	 *  
	 * @param id an identifier
	 * @param effect an effect
	 * @param attributeExpressions a collection of {@link DefaultAttributeAssignmentExpression}
	 */
	public BaseDecisionRuleResponseExpression(
			String id, 
			Effect effect, 
			Collection<AttributeAssigmentExpression> attributeExpressions) 
		throws PolicySyntaxException
	{
		checkNotNull(id, "Decision rule id can not be null");
		checkNotNull(effect, "Decision rule effect can not be null");
		checkNotNull(attributeExpressions, 
				"Decision rule attribute expression can not be null");
		this.id = id;
		this.effect = effect;
		this.attributeExpressions = new LinkedList<AttributeAssigmentExpression>(attributeExpressions);
	}
	
	/**
	 * Unique identifier
	 * 
	 * @return an identifier
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Gets {@link Effect} instance
	 * 
	 * @return {@link Effect} instance
	 */
	public Effect getEffect(){
		return effect;
	}
	
	/**
	 * Tests if this decision info expression
	 * is applicable for a given {@link Decision}
	 * 
	 * @param result a decision result
	 * @return <code>true</code> if an expression is applicable
	 */
	public boolean isApplicable(Decision result){
		return (result == Decision.PERMIT && effect == Effect.PERMIT) ||
		(result == Decision.DENY && effect == Effect.DENY);
	}
	
	public Collection<AttributeAssigmentExpression> getAttributeAssignmentExpressions(){
		return Collections.unmodifiableCollection(attributeExpressions);
	}
	
	/**
	 * Evaluates collection of {@link DefaultAttributeAssignmentExpression} instances
	 * and return collection of {@link DefaultAttributeAssignment} instances
	 * @param context an evaluation context
	 * @return collection of {@link DefaultAttributeAssignment} instances
	 * @throws EvaluationException if an evaluation error occurs
	 */
	protected Collection<AttributeAssignment> evaluateAttributeAssingments(
			EvaluationContext context) 
		throws EvaluationException
	{
		try{
			Collection<AttributeAssignment> attr = new LinkedList<AttributeAssignment>();
			for(AttributeAssigmentExpression attrExp : attributeExpressions){
				attr.add(new DefaultAttributeAssignment(
						attrExp.getAttributeId(), 
						attrExp.getCategory(), 
						attrExp.getIssuer(), 
						attrExp.evaluate(context)));
			}
			return attr;
		}catch(PolicySyntaxException e){
			throw new EvaluationException(
					StatusCode.createProcessingError(), context, e);
		}
		
	}
}
