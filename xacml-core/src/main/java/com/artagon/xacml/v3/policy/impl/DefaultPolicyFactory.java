package com.artagon.xacml.v3.policy.impl;

import java.util.Collection;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.policy.AdviceExpression;
import com.artagon.xacml.v3.policy.Apply;
import com.artagon.xacml.v3.policy.AttributeAssigmentExpression;
import com.artagon.xacml.v3.policy.AttributeReference;
import com.artagon.xacml.v3.policy.AttributeValue;
import com.artagon.xacml.v3.policy.AttributeValueType;
import com.artagon.xacml.v3.policy.CompositeDecisionRule;
import com.artagon.xacml.v3.policy.Condition;
import com.artagon.xacml.v3.policy.Effect;
import com.artagon.xacml.v3.policy.Expression;
import com.artagon.xacml.v3.policy.FunctionReference;
import com.artagon.xacml.v3.policy.Match;
import com.artagon.xacml.v3.policy.MatchAllOf;
import com.artagon.xacml.v3.policy.MatchAnyOf;
import com.artagon.xacml.v3.policy.ObligationExpression;
import com.artagon.xacml.v3.policy.Policy;
import com.artagon.xacml.v3.policy.PolicyDefaults;
import com.artagon.xacml.v3.policy.PolicyIDReference;
import com.artagon.xacml.v3.policy.PolicySet;
import com.artagon.xacml.v3.policy.PolicySetDefaults;
import com.artagon.xacml.v3.policy.PolicySetIDReference;
import com.artagon.xacml.v3.policy.PolicySyntaxException;
import com.artagon.xacml.v3.policy.Rule;
import com.artagon.xacml.v3.policy.Target;
import com.artagon.xacml.v3.policy.VariableDefinition;
import com.artagon.xacml.v3.policy.Version;
import com.artagon.xacml.v3.policy.VersionMatch;
import com.artagon.xacml.v3.policy.spi.DecisionCombiningAlgorithmProvider;
import com.artagon.xacml.v3.policy.spi.FunctionProvidersRegistry;
import com.artagon.xacml.v3.policy.type.DataTypes;

public class DefaultPolicyFactory extends BasePolicyFactory
{
		
	public  DefaultPolicyFactory(FunctionProvidersRegistry functions, 
			DecisionCombiningAlgorithmProvider combiningAlgorithms)
	{
		super(functions, combiningAlgorithms);
	}
	
	@Override
	public AttributeValue createValue(String typeId, Object value) 
		throws PolicySyntaxException
	{
		AttributeValueType type = DataTypes.getByTypeId(typeId);
		if(type == null){
			throw new PolicySyntaxException(
					"TypeId=\"%s\" can not be resolved as an XACML type",typeId);
		}
		try{
			return type.create(value);
		}catch(Exception e){
			throw new PolicySyntaxException(e);
		}
	}

	@Override
	public MatchAllOf createAllOfMatch(Collection<Match> matches) throws PolicySyntaxException 
	{
		return new DefaultMatchAllOf(matches);
	}

	@Override
	public MatchAnyOf createAnyOfMatch(Collection<MatchAllOf> matches)
			throws PolicySyntaxException {
		return new DefaultMatchAnyOf(matches);
	}

	@Override
	public Apply createApply(String functionId, Collection<Expression> arguments)
			throws PolicySyntaxException 
	{
		return new DefaultApply(createFunction(functionId), arguments);
	}

	@Override
	public Match createMatch(String functionId, AttributeValue value, AttributeReference reference)
			throws PolicySyntaxException 
	{
		return new DefaultMatch(createFunction(functionId), value, reference);
	}
	
	@Override
	public PolicySetIDReference createPolicySetIDReference(String policyId,
			VersionMatch version, VersionMatch earliest, VersionMatch latest) 
		throws PolicySyntaxException 
	{
		return new DefaultPolicySetIDReference(policyId, version, earliest, latest);
	}
	
	@Override
	public PolicyIDReference createPolicyIDReference(String policyId,
			VersionMatch version, VersionMatch earliest, VersionMatch latest) 
		throws PolicySyntaxException 
	{
		return new DefaultPolicyIDReference(policyId, version, earliest, latest);
	}
	
	public Target createTarget(Collection<MatchAnyOf> match) throws PolicySyntaxException
	{
		return new DefaultTarget(match);
	}
	
	@Override
	public Condition createCondition(Expression predicate) throws PolicySyntaxException
	{
		if(predicate == null){
			throw new PolicySyntaxException("Condition predicate must be specified");
		}
		if(!predicate.getEvaluatesTo().equals(
				DataTypes.BOOLEAN.getType())){
			throw new PolicySyntaxException(
					"Condition predicate must evaluate to=\"%s\"", DataTypes.BOOLEAN.getType());
		}
		return new DefaultCondition(predicate);
	}
	
	@Override
	public Rule createRule(String ruleId, Target target, Condition condition, Effect effect) 
		throws PolicySyntaxException
	{
		if(ruleId == null){
			throw new PolicySyntaxException("Rule identifier must be specified");
		}
		if(target == null && 
				condition == null){
			throw new PolicySyntaxException(
					"Rule id=\"%s\" condition or target must be specified", ruleId);
		}
		if(effect == null){
			throw new PolicySyntaxException("Rule id=\"%s\" effect must be specified", ruleId);
		}
		return new DefaultRule(ruleId, target, condition, effect);
	}
	
	public Policy createPolicy(
			String policyId, 
			Version version, 
			PolicyDefaults policyDefaults, 
			Target target, 
			Collection<VariableDefinition> variables,
			String algorithmId,
			Collection<Rule> rules, 
			Collection<ObligationExpression> obligation, Collection<AdviceExpression> advice) 
		throws PolicySyntaxException
	{
		Policy policy = new DefaultPolicy(policyId, version, policyDefaults, target, 
				variables,
				createRuleCombingingAlgorithm(algorithmId), rules, advice, obligation);
		return policy;
	}

	
	@Override
	public AttributeAssigmentExpression createAttributeAssigmentExpression(
			String attributeId, 
			Expression expression,
			AttributeCategoryId categoryId, 
			String issuer)
			throws PolicySyntaxException {
		return new DefaultAttributeAssignmentExpression(
				attributeId, expression, categoryId, issuer);
	}
	
	@Override
	public AdviceExpression createAdviceExpression(String id, Effect appliesTo,
			Collection<AttributeAssigmentExpression> attributeAssigments)
			throws PolicySyntaxException {
		return new DefaultAdviceExpression(id, appliesTo, attributeAssigments);
	}

	@Override
	public ObligationExpression createObligationExpression(String id,
			Effect effect,
			Collection<AttributeAssigmentExpression> attributeAssigments)
			throws PolicySyntaxException 
	{
		return new DefaultObligationExpression(id, effect, attributeAssigments);
	}

	@Override
	public PolicySet createPolicySet(String policySetId, Version version,
			PolicySetDefaults policyDefaults, Target target,
			String algorithmId, Collection<CompositeDecisionRule> policies,
			Collection<ObligationExpression> obligation,
			Collection<AdviceExpression> advice) throws PolicySyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionReference createFunctionReference(String functionId)
			throws PolicySyntaxException {
		// TODO Auto-generated method stub
		return null;
	}	
	
	
}