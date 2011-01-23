package com.artagon.xacml.v30.spi.pip;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.v30.BagOfAttributeValues;
import com.google.common.base.Preconditions;

/**
 * A base implementation of {@link AttributeResolver}
 * 
 * @author Giedrius Trumpickas
 */
public abstract class BaseAttributeResolver implements AttributeResolver
{	
	private final static Logger log = LoggerFactory.getLogger(BaseAttributeResolver.class);
	
	private AttributeResolverDescriptor descriptor;
	
	protected BaseAttributeResolver(AttributeResolverDescriptor descriptor){
		Preconditions.checkNotNull(descriptor);
		this.descriptor = descriptor;
	}
	
	@Override
	public final AttributeResolverDescriptor getDescriptor(){
		return descriptor;
	}

	@Override
	public final AttributeSet resolve(
			ResolverContext context) throws Exception 
	{
		Preconditions.checkArgument(context.getDescriptor() == descriptor);
		if(log.isDebugEnabled()){
			log.debug("Retrieving attributes via resolver " +
					"id=\"{}\" name=\"{}\"", 
					descriptor.getId(), descriptor.getName());
		}
		Map<String, BagOfAttributeValues> v = doResolve(context);
		return new AttributeSet(descriptor, 
				(v != null)?v:Collections.<String, BagOfAttributeValues>emptyMap());
	}
	
	/**
	 * Performs actual attribute resolution
	 * 
	 * @param context a policy information context
	 * @return a resolved map of attribute values as instances a
	 *  {@link BagOfAttributeValues} mapped by an attribute identifier
	 * @throws Exception if an error occurs
	 */
	protected abstract Map<String, BagOfAttributeValues> doResolve(
			ResolverContext context) throws Exception;
	
}