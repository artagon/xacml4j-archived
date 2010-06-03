package com.artagon.xacml.v3.policy.impl;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.StringReader;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.AttributeReferenceEvaluationException;
import com.artagon.xacml.v3.AttributeSelector;
import com.artagon.xacml.v3.Attributes;
import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.EvaluationException;
import com.artagon.xacml.v3.Expression;
import com.artagon.xacml.v3.Request;
import com.artagon.xacml.v3.XPathVersion;
import com.artagon.xacml.v3.policy.spi.xpath.JDKXPathProvider;
import com.artagon.xacml.v3.policy.type.DataTypes;

public class DefaultContextHandlerTest
{
	private String testXml = "<md:record xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
	"xmlns:md=\"urn:example:med:schemas:record\">" +
	"<md:patient>" +
	"<md:patientDoB>1992-03-21</md:patientDoB>" +
	"<md:patient-number>555555</md:patient-number>" +
	"</md:patient>" +
	"</md:record>";
	
	private EvaluationContext context;
	private Request request;
	
	private Node content;
	
	@Before
	public void init() throws Exception
	{
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setNamespaceAware(true);
		DocumentBuilder builder = f.newDocumentBuilder();
		this.context = createStrictMock(EvaluationContext.class);
		this.request = createStrictMock(Request.class);
		this.content = builder.parse(new InputSource(new StringReader(testXml)));
	}
	
	@Test
	public void testGetContentWithCategoryContentIsInRequest()
	{
		Attributes attributes = createStrictMock(Attributes.class);
		Node content1 = createStrictMock(Node.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.RESOURCE)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content1);
		replay(request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		Node content2 = handler.getContent(context, AttributeCategoryId.RESOURCE);
		assertSame(content1, content2);
		verify(request, attributes);
	}
	
	@Test
	public void testGetContentWithCategoryContentIsNotInRequest()
	{
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.RESOURCE)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(null);
		replay(request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		Node content2 = handler.getContent(context, AttributeCategoryId.RESOURCE);
		assertNull(content2);
		verify(request, attributes);
	}
	
	
	@Test
	public void testResolveSelectorWithCorrectNodeTypeAndNodeCanBeConvertedToInteger() throws EvaluationException
	{
		AttributeSelector ref = new AttributeSelector(
				AttributeCategoryId.SUBJECT_RECIPIENT, 
				"/md:record/md:patient/md:patient-number/text()", 
				DataTypes.INTEGER.getType(), true);
		
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.SUBJECT_RECIPIENT)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content);
		expect(context.getXPathVersion()).andReturn(XPathVersion.XPATH1);
		replay(context, request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		Expression v = handler.resolve(context, ref);
		assertEquals(v, DataTypes.INTEGER.bag(DataTypes.INTEGER.create(555555)));
		verify(context, request, attributes);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testResolveSelectorWithXPathReturnsUnsupportedNodeType() throws EvaluationException
	{
		AttributeSelector ref = new AttributeSelector(
				AttributeCategoryId.SUBJECT_RECIPIENT, 
				"/md:record/md:patient", 
				DataTypes.INTEGER.getType(), true);
		
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.SUBJECT_RECIPIENT)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content);
		expect(context.getXPathVersion()).andReturn(XPathVersion.XPATH1);
		replay(context, request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		handler.resolve(context, ref);
		verify(context, request, attributes);
	}
	
	@Test(expected=AttributeReferenceEvaluationException.class)
	public void testResolveSelectorMustBePresentFalseWithCorrectNodeTypeAndNodeFailsConvertionToDate() throws EvaluationException
	{
		AttributeSelector ref = new AttributeSelector(
				AttributeCategoryId.SUBJECT_RECIPIENT, 
				"/md:record/md:patient/md:patient-number/text()", 
				DataTypes.DATE.getType(), false);
		
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.SUBJECT_RECIPIENT)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content);
		expect(context.getXPathVersion()).andReturn(XPathVersion.XPATH1);
		replay(context, request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		handler.resolve(context, ref);
		verify(context, request, attributes);
	}
	
	@Test(expected=AttributeReferenceEvaluationException.class)
	public void testResolveSelectorMustBePresentTrueWithCorrectNodeTypeAndNodeFailsConvertionToDate() throws EvaluationException
	{
		AttributeSelector ref = new AttributeSelector(
				AttributeCategoryId.SUBJECT_RECIPIENT, 
				"/md:record/md:patient/md:patient-number/text()", 
				DataTypes.DATE.getType(), false);
		
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.SUBJECT_RECIPIENT)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content);
		expect(context.getXPathVersion()).andReturn(XPathVersion.XPATH1);
		replay(context, request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		handler.resolve(context, ref);
		verify(context, request, attributes);
	}
	
	@Test
	public void testMustBePresentFalseCategoryContentExistXPathReturnsEmptySet() throws EvaluationException
	{
		AttributeSelector ref = new AttributeSelector(
				AttributeCategoryId.SUBJECT_RECIPIENT, 
				"/test", 
				DataTypes.INTEGER.getType(), true);
		
		Attributes attributes = createStrictMock(Attributes.class);
		expect(request.hasRepeatingCategories()).andReturn(false);
		expect(request.getAttributes(AttributeCategoryId.SUBJECT_RECIPIENT)).andReturn(Collections.singletonList(attributes));
		expect(attributes.getContent()).andReturn(content);
		expect(context.getXPathVersion()).andReturn(XPathVersion.XPATH1);
		replay(context, request, attributes);
		ContextHandler handler = new DefaultContextHandler(new JDKXPathProvider(), request);
		Expression v = handler.resolve(context, ref);
		assertEquals(v, DataTypes.INTEGER.emptyBag());
		verify(context, request, attributes);
	}		
}
