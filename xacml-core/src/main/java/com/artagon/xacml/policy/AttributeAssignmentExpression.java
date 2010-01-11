package com.artagon.xacml.policy;

import org.oasis.xacml.azapi.constants.AzCategoryId;

import com.artagon.xacml.util.Preconditions;

public class AttributeAssignmentExpression implements PolicyElement
{
	private AzCategoryId category;
	private String attributeId;
	private String issuer;
	private Expression expression;
	
	public AttributeAssignmentExpression(
			String attributeId, 
			Expression expression, 
			AzCategoryId category, 
			String issuer)
	{
		Preconditions.checkNotNull(attributeId);
		Preconditions.checkNotNull(expression);
		this.attributeId = attributeId;
		this.expression = expression;
		this.category = category;
		this.issuer = issuer;
	}
	
	/**
	 * Gets attribute identifier
	 * 
	 * @return attribute identifier
	 */
	public String getAttributeId(){
		return attributeId;
	}
	
	/**
	 * An optional category of the attribute. 
	 * If category is not specified, the attribute has no category
	 * 
	 * @return {@link AzCategoryId} or <code>null</code>
	 */
	public AzCategoryId getCategory(){
		return category;
	}
	
	/**
	 * Gets an issuer of the attribute.
	 * If issuer is not specified, the attribute
	 * has not issuer
	 * 
	 * @return attribute issuer or <code>null</code>
	 */
	public String getIssuer(){
		return issuer;
	}
	
	public Attribute evaluate(EvaluationContext context) 
		throws PolicyEvaluationException
	{
		Attribute attribute = (Attribute)expression.evaluate(context);
		return attribute;
	}

	@Override
	public void accept(PolicyVisitor v) 
	{
		v.visitEnter(this);
		expression.accept(v);
		v.visitLeave(this);
	}
}
