package com.artagon.xacml.v3;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.artagon.xacml.v3.spi.PolicyInformationPoint;
import com.artagon.xacml.v3.spi.XPathEvaluationException;
import com.artagon.xacml.v3.spi.XPathProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class DefaultContextHandler implements ContextHandler
{
	private final static Logger log = LoggerFactory.getLogger(DefaultContextHandler.class);
	
	private Request request;
	private XPathProvider xpathProvider;
	private PolicyInformationPoint pip;
	
	private Map<AttributeDesignator, BagOfAttributeValues<AttributeValue>> attributes;
	
	public DefaultContextHandler(XPathProvider xpathProvider, 
			Request request, PolicyInformationPoint pip)
	{
		Preconditions.checkNotNull(request);
		Preconditions.checkArgument(!request.hasRepeatingCategories());
		Preconditions.checkNotNull(xpathProvider);
		Preconditions.checkNotNull(pip);
		this.request = request;
		this.xpathProvider = xpathProvider;
		this.pip = pip;
		this.attributes = new HashMap<AttributeDesignator, BagOfAttributeValues<AttributeValue>>();
	}
	
	@Override
	public final Node getContent(EvaluationContext context, AttributeCategoryId category) 
	{
		Attributes attr = request.getOnlyAttributes(category);
		if(attr == null || 
				attr.getContent() == null){
			if(context.getAttributeResolutionScope() == 
				AttributeResolutionScope.REQUEST_EXTERNAL)
			return pip.resolve(context, category, new DefaultRequestAttributesCallback());
		}
		return (attr != null)?attr.getContent():null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final BagOfAttributeValues<AttributeValue> resolve(
			EvaluationContext context,
			AttributeDesignator ref) throws EvaluationException 
	{
		if(log.isDebugEnabled()){
			log.debug("Resolving designator=\"{}\" " +
					"request=\"{}\"", ref, request);
		}
		// look at the local cache
		BagOfAttributeValues<AttributeValue> resolved = attributes.get(ref);
		if(resolved != null){
			return resolved;
		}
		Collection<AttributeValue> values = request.getAttributeValues(ref.getCategory(), 
				ref.getAttributeId(), ref.getIssuer(), ref.getDataType());
		if(!values.isEmpty()){
			return (BagOfAttributeValues<AttributeValue>)ref.getDataType().bagOf().create(values);
		}
		return (BagOfAttributeValues<AttributeValue>)(
				(context.getAttributeResolutionScope() == AttributeResolutionScope.REQUEST)? 
				ref.getDataType().bagOf().createEmpty():
					pip.resolve(context, ref, new DefaultRequestAttributesCallback()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public final BagOfAttributeValues<AttributeValue> resolve(
			EvaluationContext context,
			AttributeSelector ref) throws EvaluationException {
		try
		{
			Node content = getContent(context, ref.getCategory());
			if(content == null){
				if(context.getAttributeResolutionScope() == AttributeResolutionScope.REQUEST_EXTERNAL){
					content = pip.resolve(context, ref.getCategory(), 
							new DefaultRequestAttributesCallback());
				}
			}
			if(content == null){
				return (BagOfAttributeValues<AttributeValue>) ref.getDataType().bagOf().createEmpty();
			}
			
			NodeList nodeSet = xpathProvider.evaluateToNodeSet(
					context.getXPathVersion(), ref.getPath(), content);
			if(nodeSet == null || 
					nodeSet.getLength() == 0){
				log.debug("Selected nodeset via xpath=\"{}\" and category=\"{}\" is empty", 
						ref.getPath(), ref.getCategory());
				return (BagOfAttributeValues<AttributeValue>) ref.getDataType().bagOf().createEmpty();
			}
			if(log.isDebugEnabled()){
				log.debug("Found=\"{}\" nodes via xpath=\"{}\" and category=\"{}\"", 
						new Object[]{nodeSet.getLength(), ref.getPath(), ref.getCategory()});
			}
			return toBag(context, ref, nodeSet);
		}
		catch(XPathEvaluationException e){
			throw new AttributeReferenceEvaluationException(context, ref, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private BagOfAttributeValues<AttributeValue> toBag(EvaluationContext context,
			AttributeSelector ref, NodeList nodeSet) 
		throws EvaluationException
	{
		Collection<AttributeValue> values = new LinkedList<AttributeValue>();
		for(int i = 0; i< nodeSet.getLength(); i++)
		{
			Node n = nodeSet.item(i);
			String v = null;
			switch(n.getNodeType()){
				case Node.TEXT_NODE:
					v = ((Text)n).getData();
					break;
				case Node.PROCESSING_INSTRUCTION_NODE:
					v = ((ProcessingInstruction)n).getData();
					break;
				case Node.ATTRIBUTE_NODE:
					v = ((Attr)n).getValue();
					break;
				case Node.COMMENT_NODE:
					v = ((Comment)n).getData();
					break;
				default:
					throw new AttributeReferenceEvaluationException(
							StatusCode.createSyntaxError(),
							context, ref, "Unsupported DOM node type=\"%d\"", n.getNodeType());
			}
			try{
				values.add(ref.getDataType().fromXacmlString(v));
			}catch(RuntimeException e){
				throw new AttributeReferenceEvaluationException(context, ref, e);
			}
		}
	  	return (BagOfAttributeValues<AttributeValue>) ref.getDataType().bagOf().create(values);
	}

	
	class DefaultRequestAttributesCallback implements RequestAttributesCallback
	{

		@SuppressWarnings("unchecked")
		@Override
		public <AV extends AttributeValue> BagOfAttributeValues<AV> getAttribute(
				AttributeCategoryId category, String attributeId, AttributeValueType dataType, String issuer) {
			Collection<Attributes> attributes = request.getAttributes(category);
			Attributes  found = Iterables.getOnlyElement(attributes);
			return (BagOfAttributeValues<AV>)dataType.bagOf().create(found.getAttributeValues(attributeId, issuer, dataType));
		}

		@Override
		public <AV extends AttributeValue> BagOfAttributeValues<AV> getAttribute(
				AttributeCategoryId category, String attributeId, AttributeValueType dataType) {
			return getAttribute(category, attributeId, dataType, null);
		}
	}
}
