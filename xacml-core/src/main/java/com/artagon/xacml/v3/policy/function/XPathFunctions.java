package com.artagon.xacml.v3.policy.function;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.EvaluationException;
import com.artagon.xacml.v3.policy.spi.function.XacmlFunc;
import com.artagon.xacml.v3.policy.spi.function.XacmlFuncReturnType;
import com.artagon.xacml.v3.policy.spi.function.XacmlFunctionProvider;
import com.artagon.xacml.v3.policy.spi.function.XacmlLegacyFunc;
import com.artagon.xacml.v3.policy.spi.function.XacmlParam;
import com.artagon.xacml.v3.policy.spi.function.XacmlParamEvaluationContext;
import com.artagon.xacml.v3.types.XacmlDataTypes;
import com.artagon.xacml.v3.types.BooleanType.BooleanValue;
import com.artagon.xacml.v3.types.IntegerType.IntegerValue;
import com.artagon.xacml.v3.types.XPathExpressionType.XPathExpressionValue;

/**
 * This class implements functions that take XPath expressions for arguments. 
 * An XPath expression evaluates to a node-set, which is a set of XML nodes 
 * that match the expression. A node or node-set is not in the formal data-type 
 * system of XACML. All comparison or other operations on node-sets are performed 
 * in isolation of the particular function specified. The context nodes and namespace mappings 
 * of the XPath expressions are defined by the XPath data-type, see section B.3 (XACML 3.0 core). 
 * 
 * @author Giedrius Trumpickas
 */
@XacmlFunctionProvider
public class XPathFunctions 
{
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-count")
	@XacmlLegacyFunc(id="urn:oasis:names:tc:xacml:1.0:function:xpath-node-count")
	@XacmlFuncReturnType(type=XacmlDataTypes.INTEGER)
	public static IntegerValue xpathCount(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=XacmlDataTypes.XPATHEXPRESSION) XPathExpressionValue xpath) 
	{
		try{
			NodeList nodes = context.evaluateToNodeSet(xpath.getValue(), xpath.getAttributeCategory());
			if(nodes != null){
				return XacmlDataTypes.INTEGER.create(nodes.getLength());
			}
			return XacmlDataTypes.INTEGER.create(0);
		}catch(EvaluationException e){
			return XacmlDataTypes.INTEGER.create(0);
		}
	}
	
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-equal")
	@XacmlLegacyFunc(id="urn:oasis:names:tc:xacml:1.0:function:xpath-node-equal")
	@XacmlFuncReturnType(type=XacmlDataTypes.BOOLEAN)
	public static BooleanValue xpathNodeEqual(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=XacmlDataTypes.XPATHEXPRESSION) XPathExpressionValue xpath0,
			@XacmlParam(type=XacmlDataTypes.XPATHEXPRESSION) XPathExpressionValue xpath1) 
	{		
		try{
			NodeList nodes0 = context.evaluateToNodeSet(xpath0.getValue(), xpath0.getAttributeCategory());
			NodeList nodes1 = context.evaluateToNodeSet(xpath1.getValue(), xpath1.getAttributeCategory());
			if(nodes0 == null || 
					nodes0  == null){
				return XacmlDataTypes.BOOLEAN.create(false);
			}
			for(int i = 0; i < nodes0.getLength(); i++){
				for(int j = 0; j < nodes1.getLength(); j++){
					if(nodes0.item(i).isSameNode(nodes1.item(j))){
						return XacmlDataTypes.BOOLEAN.create(true);
					}
				}
			}
			return XacmlDataTypes.BOOLEAN.create(false);
		}catch(EvaluationException e){
			return XacmlDataTypes.BOOLEAN.create(false);
		}
	}
	
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-match")
	@XacmlLegacyFunc(id="urn:oasis:names:tc:xacml:1.0:function:xpath-node-matchl")
	@XacmlFuncReturnType(type=XacmlDataTypes.BOOLEAN)
	public static BooleanValue xpathNodeMatch(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=XacmlDataTypes.XPATHEXPRESSION) XPathExpressionValue xpath0,
			@XacmlParam(type=XacmlDataTypes.XPATHEXPRESSION) XPathExpressionValue xpath1) 
	{		
		try{
			NodeList nodes0 = context.evaluateToNodeSet(xpath0.getValue(), 
					xpath0.getAttributeCategory());
			NodeList nodes1 = context.evaluateToNodeSet(xpath1.getValue(), 
					xpath1.getAttributeCategory());
			if(nodes0 == null || 
					nodes0  == null){
				return XacmlDataTypes.BOOLEAN.create(false);
			}
			for(int i = 0; i < nodes0.getLength(); i++)
			{
				for(int j = 0; j < nodes1.getLength(); j++)
				{
					if(nodes0.item(i).isSameNode(nodes1.item(j))){
						return XacmlDataTypes.BOOLEAN.create(true);
					}
					NamedNodeMap a = nodes0.item(i).getAttributes();
					NamedNodeMap b = nodes1.item(j).getAttributes();
					if((a != null && b != null)){
						for(int ii = 0; ii < a.getLength(); ii++){
							for(int jj = 0; jj < b.getLength(); jj++){
								if(a.item(ii).isSameNode(b.item(jj))){
									return XacmlDataTypes.BOOLEAN.create(true);
								}
							}
						}
					}
					if(compareChildNodes(nodes0.item(i), nodes1.item(j))){
						return XacmlDataTypes.BOOLEAN.create(true);
					}
				}
			}
			return XacmlDataTypes.BOOLEAN.create(false);
		}catch(EvaluationException e){
			return XacmlDataTypes.BOOLEAN.create(false);
		}
	}
	
	/**
	 * Recursively compares the child nodes of the first 
	 * element with the second element
	 * 
	 * @param a a first node to search recursively
	 * @param b a second node to compare to
	 * @return <code>true</code>, if some of the child nodes 
	 * of the first node is equals to the second node
	 */
	private static boolean compareChildNodes(Node a, Node b)
	{
		NodeList c = a.getChildNodes();
		if(c == null || c.getLength() == 0){
			return false;
		}
		for(int i = 0; i < c.getLength(); i++){
			if(c.item(i).isSameNode(b) || 
					compareChildNodes(c.item(i), b)){
				return true;
			}
		}
		return false;
	}
}