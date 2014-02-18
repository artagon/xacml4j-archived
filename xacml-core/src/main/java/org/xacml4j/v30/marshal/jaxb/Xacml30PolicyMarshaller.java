package org.xacml4j.v30.marshal.jaxb;

import java.io.IOException;

import org.xacml4j.v30.CompositeDecisionRule;
import org.xacml4j.v30.marshal.PolicyMarshaller;
import org.xacml4j.v30.types.Types;

public class Xacml30PolicyMarshaller extends BaseJAXBMarshaller<CompositeDecisionRule>
	implements PolicyMarshaller
{
	private Xacml30PolicyFromObjectModelToJaxbMapper mapper;
	
	public Xacml30PolicyMarshaller(Types types) {
		super(JAXBContextUtil.getInstance());
		this.mapper = new Xacml30PolicyFromObjectModelToJaxbMapper(types);
	}

	@Override
	public Object marshal(CompositeDecisionRule d) throws IOException {
		return mapper.toJaxb(d);
	}
}