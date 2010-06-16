package com.artagon.xacml.v20;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis.xacml.v20.context.RequestType;
import org.oasis.xacml.v20.context.ResponseType;
import org.oasis.xacml.v20.policy.PolicySetType;
import org.oasis.xacml.v20.policy.PolicyType;

import com.artagon.xacml.v3.Decision;
import com.artagon.xacml.v3.DefaultEvaluationContextFactory;
import com.artagon.xacml.v3.DefaultPolicyFactory;
import com.artagon.xacml.v3.EvaluationContextFactory;
import com.artagon.xacml.v3.Obligation;
import com.artagon.xacml.v3.PolicyFactory;
import com.artagon.xacml.v3.Response;
import com.artagon.xacml.v3.Result;
import com.artagon.xacml.v3.StatusCodeId;
import com.artagon.xacml.v3.pdp.PolicyDecisionPoint;
import com.artagon.xacml.v3.pdp.SimplePolicyDecisionPoint;
import com.google.common.collect.Iterables;

public class TestXacml20Compatibility 
{
	private static JAXBContext context1;
	private static JAXBContext context2;
	private Xacml20PolicyMapper policyMapper;
	private Xacml20ContextMapper contextMapper;
	private PolicyDecisionPoint pdp;
	private EvaluationContextFactory factory;
	
	
	@BeforeClass
	public static void init_static() throws Exception
	{
		try{
			context1 = JAXBContext.newInstance("org.oasis.xacml.v20.policy");
			context2 = JAXBContext.newInstance("org.oasis.xacml.v20.context");
		}catch(JAXBException e){
			System.err.println(e.getMessage());
		}
	}
	
	@Before
	public void init() throws Exception
	{
		PolicyFactory policyFactory = new DefaultPolicyFactory();
		policyMapper = new Xacml20PolicyMapper(policyFactory);
		contextMapper = new Xacml20ContextMapper();
		this.factory = new DefaultEvaluationContextFactory();
		
	}
	
	@SuppressWarnings({"unchecked" })
	private static <T> T getPolicy(String name) throws Exception
	{
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		assertNotNull(stream);
		assertNotNull(context1);
		JAXBElement<T> e = (JAXBElement<T>)context1.createUnmarshaller().unmarshal(stream);
		assertNotNull(e);
		return e.getValue();
	}
	
	@SuppressWarnings({"unchecked" })
	private static <T> T getContext(String name) throws Exception
	{
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		assertNotNull(stream);
		assertNotNull(context2);
		JAXBElement<T> e = (JAXBElement<T>)context2.createUnmarshaller().unmarshal(stream);
		assertNotNull(e);
		return e.getValue();
	}
	
	@Test
	public void testIIIF005() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IIIF005Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IIIF005Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.INDETERMINATE, r.getDecision());
		assertEquals(StatusCodeId.STATUS_PROCESSING_ERROR, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID001() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IID001Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID001Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.PERMIT, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID002() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IID002Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID002Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.DENY, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID003() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IID003Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID003Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.NOT_APPLICABLE, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID004() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IID004Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID004Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.INDETERMINATE, r.getDecision());
		assertEquals(StatusCodeId.STATUS_PROCESSING_ERROR, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID005() throws Exception
	{
		PolicySetType policy = getPolicy("oasis-xacml20-compat-test/IID005Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID005Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.PERMIT, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID006() throws Exception
	{
		PolicySetType policy = getPolicy("oasis-xacml20-compat-test/IID006Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID006Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.DENY, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID007() throws Exception
	{
		PolicySetType policy = getPolicy("oasis-xacml20-compat-test/IID007Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID007Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.NOT_APPLICABLE, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIID008() throws Exception
	{
		PolicySetType policy = getPolicy("oasis-xacml20-compat-test/IID008Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IID008Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.DENY, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
	}
	
	@Test
	public void testIIIA001() throws Exception
	{
		PolicyType policy = getPolicy("oasis-xacml20-compat-test/IIIA001Policy.xml");
		RequestType request = getContext("oasis-xacml20-compat-test/IIIA001Request.xml");
		this.pdp = new SimplePolicyDecisionPoint(factory, policyMapper.create(policy));
		Response response = pdp.decide(contextMapper.create(request));
		Result r = Iterables.getOnlyElement(response.getResults());
		assertEquals(Decision.PERMIT, r.getDecision());
		assertEquals(StatusCodeId.OK, r.getStatus().getStatusCode().getValue());
		assertEquals(2, r.getObligations().size());
		Obligation o1 = r.getObligation("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:obligation-1");
		assertNotNull(o1);
		assertNotNull(Iterables.getOnlyElement(o1.getAttribute("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:assignment1")));
		assertNotNull(Iterables.getOnlyElement(o1.getAttribute("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:assignment2")));
		Obligation o2 = r.getObligation("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:obligation-2");
		assertNotNull(Iterables.getOnlyElement(o2.getAttribute("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:assignment1")));
		assertNotNull(Iterables.getOnlyElement(o2.getAttribute("urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA001:assignment2")));
		assertNotNull(o1);
	}
}
