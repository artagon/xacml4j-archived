package org.xacml4j.v30.spi.function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xacml4j.v30.AttributeExp;
import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.BagOfAttributeExpType;
import org.xacml4j.v30.Expression;
import org.xacml4j.v30.pdp.FunctionParamSpec;
import org.xacml4j.v30.types.DoubleType;
import org.xacml4j.v30.types.StringType;


public class ParamSingleTypeSpecTest
{
	private DoubleType t1;
	private StringType t2;
	private BagOfAttributeExpType b1;

	@Before
	public void init(){
		this.t1 = DoubleType.DOUBLE;
		this.t2 = StringType.STRING;
		this.b1 = t1.bagType();
	}

	@Test
	public void testValidateWithAttributeType() throws Exception
	{
		FunctionParamSpec spec = new FunctionParamValueTypeSpec(t1);
		List<Expression> good = Collections.<Expression>singletonList(t1.create(new Double(0.1)));
		List<Expression> bad = Collections.<Expression>singletonList(t2.create("AAAA"));
		assertTrue(spec.validate(good.listIterator()));
		assertFalse(spec.validate(bad.listIterator()));
	}
	
	@Test
	public void testValidateOptional() throws Exception
	{
		FunctionParamSpec spec = new FunctionParamValueTypeSpec(t1, true);
		List<Expression> param1 = Collections.<Expression>singletonList(null);
		List<Expression> param2 = Collections.<Expression>singletonList(t1.create(new Double(0.1)));
		assertTrue(spec.validate(param1.listIterator()));
		assertTrue(spec.validate(param2.listIterator()));
	}
	
	@Test
	public void testValidateRequired() throws Exception
	{
		FunctionParamSpec spec = new FunctionParamValueTypeSpec(t1, false);
		List<Expression> param1 = Collections.<Expression>singletonList(null);
		List<Expression> param2 = Collections.<Expression>singletonList(t1.create(new Double(0.1)));
		assertFalse(spec.validate(param1.listIterator()));
		assertTrue(spec.validate(param2.listIterator()));
	}

	@Test
	public void testValidateWithBagType() throws Exception
	{
		FunctionParamSpec spec = new FunctionParamValueTypeSpec(t1);
		AttributeExp v = t1.create(new Double(0.1));
		BagOfAttributeExp bag = b1.create(Collections.<AttributeExp>singletonList(v));
		List<Expression> good = Collections.<Expression>singletonList(bag);
		List<Expression> bad = Collections.<Expression>singletonList(t2.create("AAAA"));
		assertFalse(spec.validate(good.listIterator()));
		assertFalse(spec.validate(bad.listIterator()));
	}

}
