package org.xacml4j.v30.spi.function;

/*
 * #%L
 * Xacml4J Core Engine Implementation
 * %%
 * Copyright (C) 2009 - 2014 Xacml4J.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.ListIterator;

import org.xacml4j.v30.Expression;
import org.xacml4j.v30.ValueType;
import org.xacml4j.v30.pdp.FunctionReference;

import com.google.common.base.Objects;

final class FunctionParamFuncReferenceSpec extends BaseFunctionParamSpec
{
	@Override
	public boolean isValidParamType(ValueType type) {
		return false;
	}

	@Override
	public boolean validate(ListIterator<Expression> it) {
		Expression exp = it.next();
		return (exp instanceof FunctionReference);
	}

	@Override
	public String toString(){
		return Objects.
				toStringHelper(this)
				.add("optional", isOptional())
				.add("defaultValue", getDefaultValue())
				.add("variadic", isVariadic())
				.toString();
	}

	@Override
	public int hashCode(){
		return 0; // TODO: add proper hashcode
	}

	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		return (o instanceof FunctionParamFuncReferenceSpec);
	}

	public void accept(FunctionParamSpecVisitor v){
		v.visit(this);
	}
}
