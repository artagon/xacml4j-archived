package com.artagon.xacml.v3.policy.type;

import javax.security.auth.x500.X500Principal;

import com.artagon.xacml.util.Preconditions;

final class X500NameTypeImpl extends BaseAttributeType<X500NameType.X500NameValue> implements X500NameType
{
	
	public X500NameTypeImpl(String typeId){
		super(typeId, X500Principal.class);
	}

		@Override
	public X500NameValue fromXacmlString(String v) {
		Preconditions.checkNotNull(v);
		return new X500NameValue(this, new X500Principal(v));
	}
	
	@Override
	public X500NameValue create(Object any){
		Preconditions.checkNotNull(any);
		Preconditions.checkArgument(isConvertableFrom(any), String.format(
				"Value=\"%s\" of class=\"%s\" can't ne converted to XACML \"x500Name\" type", 
				any, any.getClass()));
		if(String.class.isInstance(any)){
			return fromXacmlString((String)any);
		}
		return new X500NameValue(this, (X500Principal)any);
	}
}

