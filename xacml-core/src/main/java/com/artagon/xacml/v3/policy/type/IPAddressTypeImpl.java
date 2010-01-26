package com.artagon.xacml.v3.policy.type;

import java.net.InetAddress;

import com.artagon.xacml.util.Preconditions;

final class IPAddressTypeImpl extends BaseAttributeType<IPAddressType.IPAddressValue> implements IPAddressType
{
	public IPAddressTypeImpl(String typeId){
		super(typeId, IPAddress.class);
	}
	
	@Override
	public IPAddressValue create(InetAddress address, InetAddress mask,
			PortRange portRange) {
		IPAddress v = new IPAddress(address, mask, portRange);
		return new IPAddressValue(this, v);
	}
	
	@Override
	public IPAddressValue create(Object any) {
		Preconditions.checkNotNull(any);
		Preconditions.checkArgument(isConvertableFrom(any), String.format(
				"Value=\"%s\" of class=\"%s\" can't ne converted to XACML \"ipAddress\" type", 
				any, any.getClass()));
		return new IPAddressValue(this, (IPAddress)any);
	}

	@Override
	public IPAddressValue create(InetAddress address, PortRange portRange) {
		IPAddress v = new IPAddress(address, portRange);
		return new IPAddressValue(this, v);
	}


	@Override
	public IPAddressValue create(InetAddress address, InetAddress mask) {
		IPAddress v = new IPAddress(address, PortRange.getAnyPort());
		return new IPAddressValue(this, v);
	}


	@Override
	public IPAddressValue fromXacmlString(String v) {
		return null;
	}
}