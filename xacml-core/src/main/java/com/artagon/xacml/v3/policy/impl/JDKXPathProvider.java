package com.artagon.xacml.v3.policy.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artagon.xacml.util.NodeNamespaceContext;
import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.policy.XPathEvaluationException;
import com.artagon.xacml.v3.policy.XPathProvider;

public class JDKXPathProvider implements XPathProvider
{
	private XPathFactory xpathFactory;

	public JDKXPathProvider(){
		this(XPathFactory.newInstance());
	}
	
	public JDKXPathProvider(XPathFactory xpathFactory){
		Preconditions.checkNotNull(xpathFactory);
		this.xpathFactory = xpathFactory;
	}
	
	@Override
	public Node evaluateToNode(String path, Node context)
			throws XPathEvaluationException 
	{
		try
		{
			XPath xpath = xpathFactory.newXPath();
			xpath.setNamespaceContext(new NodeNamespaceContext(context));
			return (Node)xpath.evaluate(path, context, XPathConstants.NODE);
		}catch(XPathExpressionException e){
			throw new XPathEvaluationException(e);
		}
	}

	@Override
	public NodeList evaluateToNodeSet(String path, Node context)
			throws XPathEvaluationException {
		try
		{
			XPath xpath = xpathFactory.newXPath();
			xpath.setNamespaceContext(new NodeNamespaceContext(context));
			return (NodeList)xpath.evaluate(path, context, XPathConstants.NODESET);
		}catch(XPathExpressionException e){
			throw new XPathEvaluationException(e);
		}
	}

	@Override
	public String evaluateToString(String path, Node context)
			throws XPathEvaluationException {
		try
		{
			XPath xpath = xpathFactory.newXPath();
			xpath.setNamespaceContext(new NodeNamespaceContext(context));
			return (String)xpath.evaluate(path, context, XPathConstants.STRING);
		}catch(XPathExpressionException e){
			throw new XPathEvaluationException(e);
		}
	}
	
	@Override
	public Number evaluateToNumber(String path, Node context)
			throws XPathEvaluationException {
		try
		{
			XPath xpath = xpathFactory.newXPath();
			xpath.setNamespaceContext(new NodeNamespaceContext(context));
			return (Number)xpath.evaluate(path, context, XPathConstants.NUMBER);
		}catch(XPathExpressionException e){
			throw new XPathEvaluationException(e);
		}
	}
	
}