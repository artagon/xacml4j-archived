package com.artagon.xacml.v3.policy.impl.function;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artagon.xacml.v3.policy.EvaluationContext;
import com.artagon.xacml.v3.policy.XPathEvaluationException;
import com.artagon.xacml.v3.policy.XPathProvider;
import com.artagon.xacml.v3.policy.annotations.XacmlFunc;
import com.artagon.xacml.v3.policy.annotations.XacmlFuncReturnType;
import com.artagon.xacml.v3.policy.annotations.XacmlParam;
import com.artagon.xacml.v3.policy.annotations.XacmlParamEvaluationContext;
import com.artagon.xacml.v3.policy.type.DataTypes;
import com.artagon.xacml.v3.policy.type.BooleanType.BooleanValue;
import com.artagon.xacml.v3.policy.type.IntegerType.IntegerValue;
import com.artagon.xacml.v3.policy.type.XPathExpressionType.XPathExpressionValue;

public class XPathFunctions 
{
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-count")
	@XacmlFuncReturnType(type=DataTypes.INTEGER)
	public static IntegerValue xpathCount(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=DataTypes.XPATHEXPRESSION) XPathExpressionValue xpath) 
	{
		XPathProvider xpathProvider = context.getXPathProvider();
		Node content = context.getContent(xpath.getAttributeCategory());
		if(content == null){
			return DataTypes.INTEGER.create(0);
		}
		try{
			NodeList nodes = xpathProvider.evaluateToNodeSet(xpath.getValue(), content);
			if(nodes != null){
				return DataTypes.INTEGER.create(nodes.getLength());
			}
			return DataTypes.INTEGER.create(0);
		}catch(XPathEvaluationException e){
			return DataTypes.INTEGER.create(0);
		}
	}
	
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-equal")
	@XacmlFuncReturnType(type=DataTypes.BOOLEAN)
	public static BooleanValue xpathNodeEqual(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=DataTypes.XPATHEXPRESSION) XPathExpressionValue xpath0,
			@XacmlParam(type=DataTypes.XPATHEXPRESSION) XPathExpressionValue xpath1) 
	{
		XPathProvider xpathProvider = context.getXPathProvider();
		Node content0 = context.getContent(xpath0.getAttributeCategory());
		Node content1 = context.getContent(xpath1.getAttributeCategory());
		if(content0 == null || content1  == null){
			return DataTypes.BOOLEAN.create(false);
		}
		
		try{
			NodeList nodes0 = xpathProvider.evaluateToNodeSet(xpath0.getValue(), content0);
			NodeList nodes1 = xpathProvider.evaluateToNodeSet(xpath0.getValue(), content0);
			for(int i = 0; i < nodes0.getLength(); i++){
				for(int j = 0; j < nodes1.getLength(); j++){
					if(nodes0.item(i).isSameNode(nodes1.item(j))){
						return DataTypes.BOOLEAN.create(true);
					}
				}
			}
			return DataTypes.BOOLEAN.create(0);
		}catch(XPathEvaluationException e){
			return DataTypes.BOOLEAN.create(false);
		}
	}
	
	@XacmlFunc(id="urn:oasis:names:tc:xacml:3.0:function:xpath-node-match")
	@XacmlFuncReturnType(type=DataTypes.BOOLEAN)
	public static BooleanValue xpathNodeMatch(
			@XacmlParamEvaluationContext EvaluationContext context,
			@XacmlParam(type=DataTypes.XPATHEXPRESSION) XPathExpressionValue xpath0,
			@XacmlParam(type=DataTypes.XPATHEXPRESSION) XPathExpressionValue xpath1) 
	{
		XPathProvider xpathProvider = context.getXPathProvider();
		Node content0 = context.getContent(xpath0.getAttributeCategory());
		Node content1 = context.getContent(xpath1.getAttributeCategory());
		if(content0 == null || 
				content1  == null){
			return DataTypes.BOOLEAN.create(false);
		}
		
		try{
			NodeList nodes0 = xpathProvider.evaluateToNodeSet(xpath0.getValue(), content0);
			NodeList nodes1 = xpathProvider.evaluateToNodeSet(xpath0.getValue(), content0);
			for(int i = 0; i < nodes0.getLength(); i++)
			{
				for(int j = 0; j < nodes1.getLength(); j++)
				{
					if(nodes0.item(i).isSameNode(nodes1.item(j))){
						return DataTypes.BOOLEAN.create(true);
					}
					NamedNodeMap a = nodes0.item(i).getAttributes();
					NamedNodeMap b = nodes1.item(j).getAttributes();
					if((a != null && b != null)){
						for(int ii = 0; ii < a.getLength(); ii++){
							for(int jj = 0; jj < b.getLength(); jj++){
								if(a.item(ii).isSameNode(b.item(jj))){
									return DataTypes.BOOLEAN.create(true);
								}
							}
						}
					}
					if(compareChildNodes(nodes0.item(i), nodes1.item(j))){
						return DataTypes.BOOLEAN.create(true);
					}
				}
			}
			return DataTypes.BOOLEAN.create(false);
		}catch(XPathEvaluationException e){
			return DataTypes.BOOLEAN.create(false);
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
