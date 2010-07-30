package com.artagon.xacml.v3;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.xml.namespace.QName;

import com.artagon.xacml.v3.policy.combine.DefaultDecisionCombiningAlgorithms;
import com.artagon.xacml.v3.policy.combine.legacy.LegacyDecisionCombiningAlgorithms;
import com.artagon.xacml.v3.policy.function.DefaultFunctionProvider;
import com.artagon.xacml.v3.spi.DecisionCombiningAlgorithmProvider;
import com.artagon.xacml.v3.spi.FunctionProvider;
import com.artagon.xacml.v3.spi.combine.AggregatingDecisionCombiningAlgorithmProvider;
import com.artagon.xacml.v3.spi.function.AggregatingFunctionProvider;
import com.artagon.xacml.v3.types.XacmlDataTypes;

public class DefaultXacmlFactory extends BaseXacmlFactory
{
	public DefaultXacmlFactory()
	{
		super(	new AggregatingFunctionProvider(new DefaultFunctionProvider()),
				new AggregatingDecisionCombiningAlgorithmProvider(
				new DefaultDecisionCombiningAlgorithms(), 
				new LegacyDecisionCombiningAlgorithms()));
	}
	
	public DefaultXacmlFactory(FunctionProvider extensionFunctions, 
			DecisionCombiningAlgorithmProvider extensionCombiningAlgorithms)
	{
		super(	new AggregatingFunctionProvider(new DefaultFunctionProvider(), extensionFunctions),
				new AggregatingDecisionCombiningAlgorithmProvider(
				new DefaultDecisionCombiningAlgorithms(), 
				new LegacyDecisionCombiningAlgorithms(), extensionCombiningAlgorithms));
	}
	
	@Override
	public AttributeValue createAttributeValue(String typeId, Object value) 
		throws XacmlSyntaxException
	{
		return createAttributeValue(typeId, value, 
				Collections.<QName, String>emptyMap());
	}
	
	@Override
	public AttributeValue createAttributeValue(String typeId, Object value, 
			Map<QName, String> values) 
		throws XacmlSyntaxException
	{
		AttributeValueType type = createAttributeValueType(typeId);
		try{
			return type.create(value, getXPathCategory(values));
		}catch(Exception e){
			throw new XacmlSyntaxException(e);
		}
	}
	
	private AttributeCategoryId getXPathCategory(Map<QName, String> attr){
		for(QName n : attr.keySet()){
			if(n.getLocalPart().equals("XPathCategory")){
				return AttributeCategoryId.parse(attr.get(n));
			}
		}
		return null;
	}

	@Override
	public MatchAnyOf createAnyOfMatch(Collection<MatchAllOf> matches)
			throws XacmlSyntaxException 
	{
		return new MatchAnyOf(matches);
	}

	@Override
	public Apply createApply(String functionId, Collection<Expression> arguments)
			throws XacmlSyntaxException 
	{
		return new Apply(createFunction(functionId), arguments);
	}
	
	public Apply createApply(String functionId, 
			Expression ...arguments) throws XacmlSyntaxException{
		return new Apply(createFunction(functionId), arguments);
	}

	@Override
	public Match createMatch(String functionId, AttributeValue value, AttributeReference reference)
			throws XacmlSyntaxException 
	{
		try{
			return new Match(createFunction(functionId), value, reference);
		}catch(IllegalArgumentException e){
			throw new XacmlSyntaxException(e);
		}
	}
	
	@Override
	public PolicySetIDReference createPolicySetIDReference(String policyId,
			VersionMatch version, VersionMatch earliest, VersionMatch latest) 
		throws XacmlSyntaxException 
	{
		return new PolicySetIDReference(policyId, version, earliest, latest);
	}
	
	@Override
	public PolicyIDReference createPolicyIDReference(String policyId,
			VersionMatch version, VersionMatch earliest, VersionMatch latest) 
		throws XacmlSyntaxException 
	{
		return new PolicyIDReference(policyId, version, earliest, latest);
	}
	
	public Target createTarget(Collection<MatchAnyOf> match) throws XacmlSyntaxException
	{
		return new Target(match);
	}
	
	@Override
	public Condition createCondition(Expression predicate) throws XacmlSyntaxException
	{
		if(predicate == null){
			throw new XacmlSyntaxException("Condition predicate must be specified");
		}
		if(!predicate.getEvaluatesTo().equals(
				XacmlDataTypes.BOOLEAN.getType())){
			throw new XacmlSyntaxException(
					"Condition predicate must evaluate to=\"%s\"", XacmlDataTypes.BOOLEAN.getType());
		}
		return new Condition(predicate);
	}
	
	public MatchAnyOf createAnyOf(Collection<MatchAllOf> allOf) throws XacmlSyntaxException{
		return new MatchAnyOf(allOf);
	}
	
	public MatchAllOf createAllOf(Collection<Match> match) throws XacmlSyntaxException{
		return new MatchAllOf(match);
	}
	
	@Override
	public Rule createRule(String ruleId, String description, 
			Target target, Condition condition, Effect effect) 
		throws XacmlSyntaxException
	{
		if(ruleId == null){
			throw new XacmlSyntaxException("Rule identifier must be specified");
		}
		if(effect == null){
			throw new XacmlSyntaxException("Rule id=\"%s\" effect must be specified", ruleId);
		}
		return new Rule(ruleId, description, target, condition, effect);
	}
	
	public Policy createPolicy(
			String policyId, 
			String version, 
			String description,
			PolicyDefaults policyDefaults, 
			Target target, 
			Collection<VariableDefinition> variables,
			String algorithmId,
			Collection<Rule> rules, 
			Collection<ObligationExpression> obligation, Collection<AdviceExpression> advice) 
		throws XacmlSyntaxException
	{
		Version v = Version.parse(version);
		Policy policy = new Policy(
				policyId, 
				v, 
				description, 
				policyDefaults, 
				target, 
				variables,
				createRuleCombingingAlgorithm(algorithmId), 
				rules, advice, obligation);
		return policy;
	}

	
	@Override
	public AttributeAssignmentExpression createAttributeAssigmentExpression(
			String attributeId, 
			Expression expression,
			AttributeCategoryId categoryId, 
			String issuer)
			throws XacmlSyntaxException {
		return new AttributeAssignmentExpression(
				attributeId, expression, categoryId, issuer);
	}
	
	
	@Override
	public AttributeAssignmentExpression createAttributeAssigmentExpression(
			String attributeId, Expression expression, String categoryId,
			String issuer) throws XacmlSyntaxException {
		
		return createAttributeAssigmentExpression(attributeId, expression, 
				createAttributeCategory(categoryId), issuer);
	}

	@Override
	public AttributeAssignmentExpression createAttributeAssigmentExpression(
			String attributeId, 
			Expression expression) throws XacmlSyntaxException {
		return createAttributeAssigmentExpression(attributeId, expression, 
				(AttributeCategoryId)null, null);
	}
	
	@Override
	public AdviceExpression createAdviceExpression(String id, Effect appliesTo,
			Collection<AttributeAssignmentExpression> attributeAssigments)
			throws XacmlSyntaxException {
		return new AdviceExpression(id, appliesTo, attributeAssigments);
	}

	@Override
	public ObligationExpression createObligationExpression(String id,
			Effect effect,
			Collection<AttributeAssignmentExpression> attributeAssigments)
			throws XacmlSyntaxException 
	{
		return new ObligationExpression(id, effect, attributeAssigments);
	}

	@Override
	public PolicySet createPolicySet(String policySetId, 
			String version, 
			String description,
			PolicySetDefaults policySetDefaults, 
			Target target,
			String algorithmId, 
			Collection<CompositeDecisionRule> policies,
			Collection<ObligationExpression> obligation,
			Collection<AdviceExpression> advice) throws XacmlSyntaxException 
	{
		DecisionCombiningAlgorithm<CompositeDecisionRule> algorithm = createPolicyCombingingAlgorithm(algorithmId);
		Version v = Version.parse(version);
		return new PolicySet(policySetId, v, description, 
				policySetDefaults, target, null, null, null,
				algorithm, policies, advice, obligation);
	}

	@Override
	public FunctionReference createFunctionReference(String functionId)
			throws XacmlSyntaxException {
		return new FunctionReference(createFunction(functionId));
	}

	@Override
	public AttributeDesignator createAttributeDesignator(AttributeCategoryId category,
			String attributeId, AttributeValueType dataType, boolean mustBePresent, String issuer)
			throws XacmlSyntaxException 
	{
		return new AttributeDesignator(category, 
				attributeId, issuer, dataType, mustBePresent);
	}
	
	@Override
	public AttributeDesignator createAttributeDesignator(String categoryId,
			String attributeId, String dataTypeId,
			boolean mustBePresent, String issuer) throws XacmlSyntaxException 
	{
		return createAttributeDesignator(createAttributeCategory(
				categoryId), 
				attributeId, createAttributeValueType(dataTypeId), 
				mustBePresent, issuer);
	}

	@Override
	public AttributeSelector createAttributeSelector(AttributeCategoryId category,
			String selectXPath, AttributeValueType dataType, boolean mustBePresent)
			throws XacmlSyntaxException {
		return new AttributeSelector(category, selectXPath, dataType, mustBePresent);
	}
	
	@Override
	public AttributeSelector createAttributeSelector(
			AttributeCategoryId category,
			String selectXPath, 
			String contextAttributeId, 
			AttributeValueType dataType, boolean mustBePresent)
			throws XacmlSyntaxException {
		return new AttributeSelector(category, selectXPath, 
				contextAttributeId, dataType, mustBePresent);
	}
	
	
	@Override
	public AttributeSelector createAttributeSelector(
			String categoryId,
			String selectXPath, 
			String contextAttributeId,
			String dataTypeId, boolean mustBePresent)
			throws XacmlSyntaxException 
	{
		return createAttributeSelector(createAttributeCategory(categoryId),
				selectXPath, contextAttributeId, 
				createAttributeValueType(dataTypeId), mustBePresent);
	}

	@Override
	public AttributeSelector createAttributeSelector(String categoryId,
			String selectXPath, 
			String dataTypeId, boolean mustBePresent)
			throws XacmlSyntaxException 
	{
		return createAttributeSelector(createAttributeCategory(categoryId),
				selectXPath, null, 
				createAttributeValueType(dataTypeId), mustBePresent);
	}
	
	@Override
	public VariableReference createVariableReference(VariableDefinition varDef)
			throws XacmlSyntaxException 
	{
		return new VariableReference(varDef);
	}
	
	public VariableDefinition createVariableDefinition(
			String variableId, Expression expression) throws XacmlSyntaxException
	{
		if(variableId == null || 
				expression == null){
			throw new XacmlSyntaxException("Variable identifier " +
					"or expression can not be null");
		}
		return new VariableDefinition(variableId, expression);
	}

	@Override
	public PolicyDefaults createPolicyDefaults(Object... objects)
			throws XacmlSyntaxException 
	{
		if(objects != null && 
				objects.length > 0){
			if(objects[0] instanceof String){
				String value = (String)objects[0];
				XPathVersion v = XPathVersion.parse(value);
				if(v == null){
					throw new XacmlSyntaxException(
							"Unparsable XPath version=\"%s\"", value);
				}
				return new PolicyDefaults(v);
			}
		}
		return null;
	}
	
	@Override
	public PolicySetDefaults createPolicySetDefaults(Object... objects)
			throws XacmlSyntaxException {
		if(objects != null && 
				objects.length > 0){
			if(objects[0] instanceof String){
				String value = (String)objects[0];
				XPathVersion v = XPathVersion.parse(value);
				if(v == null){
					throw new XacmlSyntaxException(
							"Unparsable XPath version=\"%s\"", value);
				}
				return new PolicySetDefaults(v);
			}
		}
		return null;
	}	
	
	@Override
	public AttributeCategoryId createAttributeCategory(String categoryId) 
		throws XacmlSyntaxException
	{
		if(categoryId == null){
			return null;
		}
		AttributeCategoryId c = AttributeCategoryId.parse(categoryId);
		if(c == null){
			throw new XacmlSyntaxException(
					"Unknown c=attribute category=\"%s\"", categoryId);
		}
		return c;
	}
	
	@Override
	public AttributeValueType createAttributeValueType(String dataTypeId) 
		throws XacmlSyntaxException
	{
		AttributeValueType type = XacmlDataTypes.getByTypeId(dataTypeId); 
		if(type == null){
			throw new XacmlSyntaxException(
					"Unknown XACML dataTypeId=\"%s\"", dataTypeId);
		}
		return type;
	}
}