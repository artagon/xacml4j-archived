package com.artagon.xacml.v30.marshall;

import java.util.Map;

import javax.xml.namespace.QName;

import com.artagon.xacml.v30.AttributeCategories;
import com.artagon.xacml.v30.AttributeCategory;
import com.artagon.xacml.v30.AttributeValue;
import com.artagon.xacml.v30.AttributeValueType;
import com.artagon.xacml.v30.CompositeDecisionRule;
import com.artagon.xacml.v30.DecisionCombiningAlgorithm;
import com.artagon.xacml.v30.FunctionSpec;
import com.artagon.xacml.v30.Rule;
import com.artagon.xacml.v30.XacmlSyntaxException;
import com.artagon.xacml.v30.spi.combine.DecisionCombiningAlgorithmProvider;
import com.artagon.xacml.v30.spi.function.FunctionProvider;
import com.artagon.xacml.v30.types.DataTypes;
import com.google.common.base.Preconditions;

/**
 * A support class for dealing with XACML 
 * functions and decision combining algorithms
 * 
 * @author Giedrius Trumpickas
 */
public class PolicyUnmarshallerSupport 
{
	private FunctionProvider functions;
	private DecisionCombiningAlgorithmProvider combingingAlgorithms;

	protected PolicyUnmarshallerSupport(
			FunctionProvider functions,
			DecisionCombiningAlgorithmProvider decisionCombiningAlgorithms) throws Exception
	{
		Preconditions.checkNotNull(functions, 
				"Function provider can't be null");
		Preconditions.checkNotNull(decisionCombiningAlgorithms, 
				"Decision combingin algorithm provider can't be null");
		this.functions = functions;
		this.combingingAlgorithms = decisionCombiningAlgorithms;
	}
	
	/**
	 * Creates function from a given identifier
	 * 
	 * @param functionId a function identifier
	 * @return {@link FunctionSpec} instance
	 * @throws XacmlSyntaxException if function with a given
	 * identifier is not known to this factory
	 */
	protected final FunctionSpec createFunction(String functionId)
			throws XacmlSyntaxException 
	{
		FunctionSpec spec = functions.getFunction(functionId);
		if (spec == null) {
			throw new XacmlSyntaxException(
					"Function with id=\"%s\" can not be resolved", functionId);
		}
		return spec;
	}
	
	/**
	 * Creates {@link DecisionCombiningAlgorithmProvider} based
	 * on a given algorithm identifier
	 * 
	 * @param algorithmId an algorithm identifier
	 * @return {@link DecisionCombiningAlgorithmProvider} instance
	 * @throws XacmlSyntaxException if no algorithm can be found
	 * for given identifier
	 */
	protected final DecisionCombiningAlgorithm<Rule> createRuleCombingingAlgorithm(
			String algorithmId) throws XacmlSyntaxException {
		DecisionCombiningAlgorithm<Rule> algorithm = combingingAlgorithms
				.getRuleAlgorithm(algorithmId);
		if (algorithm == null) {
			throw new XacmlSyntaxException(
					"Rule comnbining algorithm=\"%s\" can not be resolved",
					algorithmId);
		}
		return algorithm;
	}
	
	/**
	 * Creates {@link DecisionCombiningAlgorithmProvider} based
	 * on a given algorithm identifier
	 * 
	 * @param algorithmId an algorithm identifier
	 * @return {@link DecisionCombiningAlgorithmProvider} instance
	 * @throws XacmlSyntaxException if no algorithm can be found
	 * for given identifier
	 */
	protected final DecisionCombiningAlgorithm<CompositeDecisionRule> createPolicyCombingingAlgorithm(
			String algorithmId) throws XacmlSyntaxException {
		DecisionCombiningAlgorithm<CompositeDecisionRule> algorithm = combingingAlgorithms
				.getPolicyAlgorithm(algorithmId);
		if (algorithm == null) {
			throw new XacmlSyntaxException(
					"Policy comnbining algorithm=\"%s\" can not be resolved",
					algorithmId);
		}
		return algorithm;
	}
	
	/**
	 * Gets data type via date type identifier
	 * 
	 * @param typeId a data type identifier
	 * @return {@link AttributeValueType}
	 * @throws XacmlSyntaxException
	 */
	protected AttributeValueType getDataType(String typeId) 
		throws XacmlSyntaxException
	{
		return DataTypes.getType(typeId);
	}
	
	protected  AttributeValue createAttributeValue(
			String typeId, 
			Object value, Map<QName, String> values) throws XacmlSyntaxException 
	{
		AttributeValueType type = getDataType(typeId);
		try {
			return type.create(value, getXPathCategory(values));
		} catch (Exception e) {
			throw new XacmlSyntaxException(e);
		}
	}

	private  AttributeCategory getXPathCategory(Map<QName, String> attr) 
		throws XacmlSyntaxException
	{
		for (QName n : attr.keySet()) {
			if (n.getLocalPart().equals("XPathCategory")) {
				return AttributeCategories.parse(attr.get(n));
			}
		}
		return null;
	}
}