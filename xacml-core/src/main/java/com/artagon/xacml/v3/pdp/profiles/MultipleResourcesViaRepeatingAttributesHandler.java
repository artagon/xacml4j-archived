package com.artagon.xacml.v3.pdp.profiles;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.artagon.xacml.v3.AttributeCategory;
import com.artagon.xacml.v3.Attributes;
import com.artagon.xacml.v3.RequestContext;
import com.artagon.xacml.v3.Result;
import com.artagon.xacml.v3.pdp.AbstractRequestContextHandler;
import com.artagon.xacml.v3.pdp.PolicyDecisionCallback;
import com.google.common.collect.Sets;

final class MultipleResourcesViaRepeatingAttributesHandler extends AbstractRequestContextHandler
{
	@Override
	public Collection<Result> handle(RequestContext request, PolicyDecisionCallback pdp) 
	{
		if(!request.hasRepeatingCategories()){
			return handleNext(request, pdp);
		}
		List<Set<Attributes>> byCategory = new LinkedList<Set<Attributes>>();
		for(AttributeCategory categoryId : request.getCategories()){
			Collection<Attributes> attributes = request.getAttributes(categoryId);
			if(attributes == null ||
					attributes.isEmpty()){
				continue;
			}
			byCategory.add(new LinkedHashSet<Attributes>(attributes));
		}
		Collection<Result> results = new LinkedList<Result>();
		Set<List<Attributes>> cartesian = Sets.cartesianProduct(byCategory);
		for(List<Attributes> requestAttr : cartesian)
		{	
			RequestContext req = new RequestContext(request.isReturnPolicyIdList(), requestAttr);
			results.addAll(handleNext(req, pdp));
		}
		return results;
	}
}