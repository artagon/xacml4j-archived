package org.xacml4j.v30.marshal.jaxb;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.oasis.xacml.v30.jaxb.AdviceExpressionType;
import org.oasis.xacml.v30.jaxb.AdviceExpressionsType;
import org.oasis.xacml.v30.jaxb.AllOfType;
import org.oasis.xacml.v30.jaxb.AnyOfType;
import org.oasis.xacml.v30.jaxb.AttributeAssignmentExpressionType;
import org.oasis.xacml.v30.jaxb.AttributeDesignatorType;
import org.oasis.xacml.v30.jaxb.AttributeSelectorType;
import org.oasis.xacml.v30.jaxb.AttributeType;
import org.oasis.xacml.v30.jaxb.AttributeValueType;
import org.oasis.xacml.v30.jaxb.ConditionType;
import org.oasis.xacml.v30.jaxb.DefaultsType;
import org.oasis.xacml.v30.jaxb.EffectType;
import org.oasis.xacml.v30.jaxb.IdReferenceType;
import org.oasis.xacml.v30.jaxb.MatchType;
import org.oasis.xacml.v30.jaxb.ObjectFactory;
import org.oasis.xacml.v30.jaxb.ObligationExpressionType;
import org.oasis.xacml.v30.jaxb.ObligationExpressionsType;
import org.oasis.xacml.v30.jaxb.PolicyIssuerType;
import org.oasis.xacml.v30.jaxb.PolicySetType;
import org.oasis.xacml.v30.jaxb.PolicyType;
import org.oasis.xacml.v30.jaxb.RuleType;
import org.oasis.xacml.v30.jaxb.TargetType;
import org.oasis.xacml.v30.jaxb.VariableDefinitionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xacml4j.util.DOMUtil;
import org.xacml4j.v30.Attribute;
import org.xacml4j.v30.AttributeExp;
import org.xacml4j.v30.CompositeDecisionRule;
import org.xacml4j.v30.Effect;
import org.xacml4j.v30.pdp.AdviceExpression;
import org.xacml4j.v30.pdp.AttributeAssignmentExpression;
import org.xacml4j.v30.pdp.AttributeDesignator;
import org.xacml4j.v30.pdp.AttributeReference;
import org.xacml4j.v30.pdp.AttributeSelector;
import org.xacml4j.v30.pdp.Match;
import org.xacml4j.v30.pdp.MatchAllOf;
import org.xacml4j.v30.pdp.MatchAnyOf;
import org.xacml4j.v30.pdp.ObligationExpression;
import org.xacml4j.v30.pdp.Policy;
import org.xacml4j.v30.pdp.PolicyDefaults;
import org.xacml4j.v30.pdp.PolicyIDReference;
import org.xacml4j.v30.pdp.PolicyIssuer;
import org.xacml4j.v30.pdp.PolicySet;
import org.xacml4j.v30.pdp.PolicySetDefaults;
import org.xacml4j.v30.pdp.PolicySetIDReference;
import org.xacml4j.v30.pdp.Rule;
import org.xacml4j.v30.pdp.Target;
import org.xacml4j.v30.pdp.VariableDefinition;
import org.xacml4j.v30.types.TypeToXacml30;
import org.xacml4j.v30.types.Types;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Xacml30PolicyFromObjectModelToJaxbMapper
{
	private final static Logger log = LoggerFactory.getLogger(Xacml30PolicyFromObjectModelToJaxbMapper.class);

	private final static ObjectFactory factory = new ObjectFactory();

	private final static Map<Effect, EffectType> nativeToJaxbEffectMappings;

	static {
		ImmutableMap.Builder<Effect, EffectType> nativeToJaxbB = ImmutableMap.builder();
		nativeToJaxbB.put(Effect.DENY, EffectType.DENY);
		nativeToJaxbB.put(Effect.PERMIT, EffectType.PERMIT);
		nativeToJaxbEffectMappings = nativeToJaxbB.build();
	}

	private Types types;
	
	public Xacml30PolicyFromObjectModelToJaxbMapper(Types types){
		Preconditions.checkNotNull(types);
		this.types = types;
	}
	
	public JAXBElement<?> toJaxb(CompositeDecisionRule d){
		if(d instanceof PolicySet){
			return toJaxb((PolicySet)d);
		}
		if(d instanceof Policy){
			return toJaxb((Policy)d);
		}
		if(d instanceof Rule){
			return toJaxb((Policy)d);
		}
		if(d instanceof PolicyIDReference){
			return toJaxb((PolicyIDReference)d);
		}
		if(d instanceof PolicySetIDReference){
			return toJaxb((PolicySetIDReference)d);
		}
		throw new IllegalArgumentException(
				String.format(
						"Unsupported decision rule type=\"%s\"",
						d.getClass().getName()));
	}

	private JAXBElement<?> toJaxb(PolicyIDReference ref)
	{
		IdReferenceType jaxbRef = factory.createIdReferenceType();
		if(ref.getEarliestVersion() != null){
			jaxbRef.setEarliestVersion(ref.getEarliestVersion().getPattern());
		}
		if(ref.getLatestVersion() != null){
			jaxbRef.setLatestVersion(ref.getLatestVersion().getPattern());
		}
		if(ref.getVersion() != null){
			jaxbRef.setLatestVersion(ref.getVersion().getPattern());
		}
		jaxbRef.setValue(ref.getId());
		return factory.createPolicyIdReference(jaxbRef);
	}

	private JAXBElement<?> toJaxb(PolicySetIDReference ref)
	{
		IdReferenceType jaxbRef = factory.createIdReferenceType();
		if(ref.getEarliestVersion() != null){
			jaxbRef.setEarliestVersion(ref.getEarliestVersion().getPattern());
		}
		if(ref.getLatestVersion() != null){
			jaxbRef.setLatestVersion(ref.getLatestVersion().getPattern());
		}
		if(ref.getVersion() != null){
			jaxbRef.setLatestVersion(ref.getVersion().getPattern());
		}
		jaxbRef.setValue(ref.getId());
		return factory.createPolicySetIdReference(jaxbRef);
	}

	private JAXBElement<?> toJaxb(Rule d){
		if(log.isDebugEnabled()){
			log.debug("Mapping Rule id=\"{}\"", d.getId());
		}
		Rule rule = (Rule)d;
		RuleType jaxbRule = factory.createRuleType();
		jaxbRule.setRuleId(rule.getId());
		jaxbRule.setDescription(rule.getDescription());
		jaxbRule.setTarget(toJaxb(d.getTarget()));
		EffectType effect = nativeToJaxbEffectMappings.get(rule.getEffect());
		Preconditions.checkState(effect != null);
		jaxbRule.setEffect(effect);
		if(d.getCondition() != null){
			ConditionType condition = factory.createConditionType();
			condition.setExpression(ExpressionTypeBuilder.getBuilder(d.getCondition().getExpression()).from(d.getCondition().getExpression()));
			jaxbRule.setCondition(condition);
		}
		jaxbRule.setAdviceExpressions(toJaxbAdvices(rule.getAdviceExpressions()));
		jaxbRule.setObligationExpressions(toJaxbObigations(rule.getObligationExpressions()));
		return factory.createRule(jaxbRule);
	}

	private JAXBElement<?> toJaxb(Policy d){
		if(log.isDebugEnabled()){
			log.debug("Mapping Policy id=\"{}\"", d.getId());
		}
		Policy p = (Policy)d;
		PolicyType jaxbPolicy = factory.createPolicyType();
		jaxbPolicy.setPolicyId(p.getId());
		jaxbPolicy.setPolicyIssuer(toJaxb(p.getIssuer()));
		jaxbPolicy.setPolicyDefaults(toJaxb(p.getDefaults()));
		jaxbPolicy.setDescription(p.getDescription());
		jaxbPolicy.setTarget(toJaxb(d.getTarget()));
		jaxbPolicy.setRuleCombiningAlgId(p.getRuleCombiningAlgorithm().getId());
		if(d.getCondition() != null){
			ConditionType condition = factory.createConditionType();
			condition.setExpression(ExpressionTypeBuilder.getBuilder(d.getCondition().getExpression()).from(d.getCondition().getExpression()));
			jaxbPolicy.setCondition(condition);
		}
		jaxbPolicy.setAdviceExpressions(toJaxbAdvices(p.getAdviceExpressions()));
		jaxbPolicy.setObligationExpressions(toJaxbObigations(p.getObligationExpressions()));
		for(VariableDefinition var : p.getVariableDefinitions()){
			jaxbPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(toJaxb(var));
		}
		for(Rule r : p.getRules()){
			jaxbPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(toJaxb(r));
		}
		return factory.createPolicy(jaxbPolicy);
	}

	private JAXBElement<?> toJaxb(PolicySet d){
		if(log.isDebugEnabled()){
			log.debug("Mapping PolicySet id=\"{}\"", d.getId());
		}
		PolicySet ps = (PolicySet)d;
		PolicySetType jaxbPolicySet = factory.createPolicySetType();
		jaxbPolicySet.setPolicySetId(ps.getId());
		jaxbPolicySet.setPolicyIssuer(toJaxb(ps.getIssuer()));
		jaxbPolicySet.setPolicySetDefaults(toJaxb(d.getDefaults()));
		jaxbPolicySet.setDescription(ps.getDescription());
		jaxbPolicySet.setTarget(toJaxb(d.getTarget()));
		jaxbPolicySet.setPolicyCombiningAlgId(ps.getPolicyDecisionCombiningAlgorithm().getId());
		if(d.getCondition() != null){
			ConditionType condition = factory.createConditionType();
			condition.setExpression(ExpressionTypeBuilder.getBuilder(d.getCondition().getExpression()).from(d.getCondition().getExpression()));
			jaxbPolicySet.setCondition(condition);
		}
		jaxbPolicySet.setAdviceExpressions(toJaxbAdvices(ps.getAdviceExpressions()));
		jaxbPolicySet.setObligationExpressions(toJaxbObigations(ps.getObligationExpressions()));
		for(CompositeDecisionRule r : ps.getDecisions()){
			jaxbPolicySet.getPolicySetOrPolicyOrPolicySetIdReference().add(toJaxb(r));
		}
		return factory.createPolicySet(jaxbPolicySet);
	}

	private DefaultsType toJaxb(PolicyDefaults d)
	{
		if(d == null){
			return null;
		}
		DefaultsType jaxbDef = factory.createDefaultsType();
		jaxbDef.setXPathVersion(d.getXPathVersion().toString());
		return jaxbDef;
	}

	private DefaultsType toJaxb(PolicySetDefaults d)
	{
		if(d == null){
			return null;
		}
		DefaultsType jaxbDef = factory.createDefaultsType();
		jaxbDef.setXPathVersion(d.getXPathVersion().toString());
		return jaxbDef;
	}

	private VariableDefinitionType toJaxb(VariableDefinition var)
	{
		Preconditions.checkNotNull(var);
		VariableDefinitionType jaxbVar = factory.createVariableDefinitionType();
		jaxbVar.setVariableId(var.getVariableId());
		JAXBElement<?> expression = ExpressionTypeBuilder.getBuilder(var.getExpression()).from(var.getExpression());
		jaxbVar.setExpression(expression);
		return jaxbVar;
	}

	private PolicyIssuerType toJaxb(PolicyIssuer issuer)
	{
		if(issuer == null){
			return null;
		}
		PolicyIssuerType jaxbIssuer = factory.createPolicyIssuerType();
		if(issuer.getContent() != null){
			jaxbIssuer.getContent().getContent().add(DOMUtil.copyNode(issuer.getContent()));
		}
		for(Attribute a : issuer.getAttributes()){
			jaxbIssuer.getAttribute().add(toJaxb(a));
		}
		return jaxbIssuer;
	}

	private AttributeType toJaxb(Attribute a)
	{
		Preconditions.checkNotNull(a);
		AttributeType jaxbAttr = factory.createAttributeType();
		jaxbAttr.setAttributeId(a.getAttributeId());
		jaxbAttr.setIncludeInResult(a.isIncludeInResult());
		jaxbAttr.setIssuer(a.getIssuer());
		for(AttributeExp v : a.getValues()){
			jaxbAttr.getAttributeValue().add(toJaxb(v));
		}
		return jaxbAttr;
	}

	private AttributeValueType toJaxb(AttributeExp a)
	{
		Preconditions.checkNotNull(a);
		TypeToXacml30 toXacml30 = types.getCapability(a.getType(), TypeToXacml30.class);
		return toXacml30.toXacml30(types, a);
	}

	private TargetType toJaxb(Target t)
	{
		if(t == null){
			return null;
		}
		TargetType jaxbTarget = factory.createTargetType();
		for(MatchAnyOf m : t.getAnyOf()){
			jaxbTarget.getAnyOf().add(toJaxb(m));
		}
		return jaxbTarget;
	}

	private AnyOfType toJaxb(MatchAnyOf t)
	{
		Preconditions.checkNotNull(t);
		AnyOfType jaxbAnyOf = factory.createAnyOfType();
		for(MatchAllOf m : t.getAllOf()){
			jaxbAnyOf.getAllOf().add(toJaxb(m));
		}
		return jaxbAnyOf;
	}

	private AllOfType toJaxb(MatchAllOf t){
		Preconditions.checkNotNull(t);
		AllOfType jaxbAllOf = factory.createAllOfType();
		for(Match m : t.getMatch()){
			jaxbAllOf.getMatch().add(toJaxb(m));
		}
		return jaxbAllOf;
	}

	private MatchType toJaxb(Match t)
	{
		Preconditions.checkNotNull(t);
		MatchType jaxbMatch = factory.createMatchType();
		jaxbMatch.setMatchId(t.getMatchId());
		jaxbMatch.setAttributeValue(toJaxb(t.getAttributeValue()));
		AttributeReference ref = t.getReference();
		if(ref instanceof AttributeSelector){
			@SuppressWarnings("unchecked")
			JAXBElement<AttributeSelectorType> s = (JAXBElement<AttributeSelectorType>)ExpressionTypeBuilder
			.getBuilder(ref)
			.from(ref);
			jaxbMatch.setAttributeSelector(s.getValue());
		}
		if(ref instanceof AttributeDesignator){
			@SuppressWarnings("unchecked")
			JAXBElement<AttributeDesignatorType> d = (JAXBElement<AttributeDesignatorType>)ExpressionTypeBuilder
			.getBuilder(ref)
			.from(ref);
			jaxbMatch.setAttributeDesignator(d.getValue());
		}
		return jaxbMatch;
	}

	private ObligationExpressionsType toJaxbObigations(
			Collection<ObligationExpression> obligations){
		if(obligations == null ||
				obligations.isEmpty()){
			return null;
		}
		ObligationExpressionsType o = factory.createObligationExpressionsType();
		for(ObligationExpression e : obligations){
			o.getObligationExpression().add(toJaxb(e));
		}
		return o;
	}

	private AdviceExpressionsType toJaxbAdvices(
			Collection<AdviceExpression> advices){
		if(advices == null ||
				advices.isEmpty()){
			return null;
		}
		AdviceExpressionsType a = factory.createAdviceExpressionsType();
		for(AdviceExpression advice : advices){
			a.getAdviceExpression().add(toJaxb(advice));
		}
		return a;
	}

	private ObligationExpressionType toJaxb(ObligationExpression o){
		Preconditions.checkNotNull(o);
		ObligationExpressionType jaxb = factory.createObligationExpressionType();
		jaxb.setObligationId(o.getId());
		EffectType effect = nativeToJaxbEffectMappings.get(o.getEffect());
		Preconditions.checkState(effect != null);
		jaxb.setFulfillOn(effect);
		jaxb.getAttributeAssignmentExpression().addAll(toJaxb(o.getAttributeAssignmentExpressions()));
		return jaxb;
	}

	private AdviceExpressionType toJaxb(AdviceExpression o){
		Preconditions.checkNotNull(o);
		AdviceExpressionType jaxb = factory.createAdviceExpressionType();
		jaxb.setAdviceId(o.getId());
		EffectType effect = nativeToJaxbEffectMappings.get(o.getEffect());
		Preconditions.checkState(effect != null);
		jaxb.setAppliesTo(effect);
		jaxb.getAttributeAssignmentExpression().addAll(toJaxb(o.getAttributeAssignmentExpressions()));
		return jaxb;
	}

	private Collection<AttributeAssignmentExpressionType> toJaxb(
			Collection<AttributeAssignmentExpression> exps){
		ImmutableList.Builder<AttributeAssignmentExpressionType> jaxbExps = ImmutableList.builder();
		for(AttributeAssignmentExpression  e : exps){
			jaxbExps.add(toJaxb(e));
		}
		return jaxbExps.build();
	}

	private AttributeAssignmentExpressionType toJaxb(
			AttributeAssignmentExpression exp){
		AttributeAssignmentExpressionType jaxbExp = factory.createAttributeAssignmentExpressionType();
		JAXBElement<?> expression = ExpressionTypeBuilder.getBuilder(exp.getExpression()).from(exp.getExpression());
		jaxbExp.setExpression(expression);
		jaxbExp.setAttributeId(exp.getAttributeId());
		jaxbExp.setCategory(exp.getCategory().getId());
		jaxbExp.setIssuer(exp.getIssuer());
		return jaxbExp;
	}
}