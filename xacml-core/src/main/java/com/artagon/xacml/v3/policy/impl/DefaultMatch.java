package com.artagon.xacml.v3.policy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.policy.AttributeRefExpression;
import com.artagon.xacml.v3.policy.AttributeValue;
import com.artagon.xacml.v3.policy.BagOfAttributeValues;
import com.artagon.xacml.v3.policy.EvaluationContext;
import com.artagon.xacml.v3.policy.EvaluationException;
import com.artagon.xacml.v3.policy.FunctionSpec;
import com.artagon.xacml.v3.policy.Match;
import com.artagon.xacml.v3.policy.MatchResult;
import com.artagon.xacml.v3.policy.PolicyElement;
import com.artagon.xacml.v3.policy.PolicyVisitor;
import com.artagon.xacml.v3.policy.type.BooleanType.BooleanValue;

final class DefaultMatch implements PolicyElement, Match
{	
	private final static Logger log = LoggerFactory.getLogger(DefaultMatch.class);
	
	private AttributeValue value;
	private AttributeRefExpression attributeRef;
	private FunctionSpec predicate;
	
	/**
	 * Constructs match with a given literal value 
	 * and attribute reference.
	 * @param value a literal attribute value
	 * @param attrRef an attribute reference
	 * @param function a match function
	 */
	protected DefaultMatch(FunctionSpec spec, 
			AttributeValue value, AttributeRefExpression attributeReference)
	{
		Preconditions.checkNotNull(spec);
		Preconditions.checkNotNull(value);
		Preconditions.checkNotNull(attributeReference);
		Preconditions.checkArgument(spec.getNumberOfParams() == 2);
		Preconditions.checkArgument(spec.getParamSpecAt(0).
				isValidParamType(value.getEvaluatesTo()));
		Preconditions.checkArgument(spec.getParamSpecAt(1).
				isValidParamType((attributeReference.getDataType())));
		this.value = value;
		this.predicate = spec;
		this.attributeRef = attributeReference;	
	}
	
	/**
	 * Gets match function XACML identifier.
	 * 
	 * @return match function XACML identifier
	 */
	public String getMatchId(){
		return predicate.getId();
	}
	
	/**
	 * Gets match attribute value.
	 * 
	 * @return {@link Attribute<?>} instance
	 */
	public AttributeValue getAttributeValue(){
		return value;
	}
	
	@Override
	public MatchResult match(EvaluationContext context)
	{
		try
		{
			BagOfAttributeValues<?> attributes = (BagOfAttributeValues<?>)attributeRef.evaluate(context);
			log.debug("Evaluated attribute reference=\"{}\" to " +
					"bag=\"{}\"", attributeRef, attributes);
			for(AttributeValue v : attributes.values()){
				BooleanValue match = predicate.invoke(context, value, v);
				if(match.getValue()){
					log.debug("Attribute value=\"{}\" " +
							"matches attribute value=\"{}\"", value, v);
					return MatchResult.MATCH;
				}
			}
			return MatchResult.NOMATCH;
		}catch(EvaluationException e){
			return MatchResult.INDETERMINATE;
		}
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		value.accept(v);
		attributeRef.accept(v);
		v.visitLeave(this);
	}
}
